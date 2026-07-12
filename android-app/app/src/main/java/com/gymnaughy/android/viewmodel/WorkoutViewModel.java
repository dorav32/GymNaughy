package com.gymnaughy.android.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gymnaughy.android.model.CompletedSet;
import com.gymnaughy.android.model.PlannedExercise;
import com.gymnaughy.android.model.WorkoutDay;
import com.gymnaughy.android.network.dto.WorkoutLogResponse;
import com.gymnaughy.android.repository.RepositoryCallback;
import com.gymnaughy.android.repository.WorkoutRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Drives a single guided workout session (WorkoutDetailActivity): walks through the
 * exercises of one {@link WorkoutDay} one at a time and accumulates completed sets
 * until the whole day is logged in one call to backend-api.
 */
public class WorkoutViewModel extends ViewModel {

    private final WorkoutRepository workoutRepository = new WorkoutRepository();

    private String planId;
    private WorkoutDay day;
    private int currentExerciseIndex = 0;
    private long sessionStartMillis;

    private final Map<String, CompletedSet> completedSetsByExercise = new HashMap<>();

    private final MutableLiveData<Integer> currentIndex = new MutableLiveData<>(0);
    private final MutableLiveData<WorkoutLogResponse> logResult = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public void start(String planId, WorkoutDay day) {
        this.planId = planId;
        this.day = day;
        this.sessionStartMillis = System.currentTimeMillis();
        this.currentExerciseIndex = 0;
        currentIndex.setValue(0);
    }

    public PlannedExercise getCurrentExercise() {
        if (day == null || day.getExercises() == null || day.getExercises().isEmpty()) {
            return null;
        }
        return day.getExercises().get(currentExerciseIndex);
    }

    public LiveData<Integer> getCurrentIndex() {
        return currentIndex;
    }

    public int getTotalExercises() {
        return day == null || day.getExercises() == null ? 0 : day.getExercises().size();
    }

    public void recordSet(int repsCompleted) {
        PlannedExercise exercise = getCurrentExercise();
        if (exercise == null) {
            return;
        }
        CompletedSet completedSet = completedSetsByExercise.computeIfAbsent(
                exercise.getExerciseId(), CompletedSet::new);
        completedSet.recordSet(repsCompleted);
    }

    public boolean advanceToNextExercise() {
        if (currentExerciseIndex + 1 >= getTotalExercises()) {
            return false;
        }
        currentExerciseIndex++;
        currentIndex.setValue(currentExerciseIndex);
        return true;
    }

    public void finishWorkout() {
        long durationSeconds = (System.currentTimeMillis() - sessionStartMillis) / 1000;
        List<CompletedSet> completedSets = new ArrayList<>(completedSetsByExercise.values());

        workoutRepository.logWorkout(planId, day.getId(), completedSets, durationSeconds,
                new RepositoryCallback<WorkoutLogResponse>() {
                    @Override
                    public void onSuccess(WorkoutLogResponse data) {
                        logResult.postValue(data);
                    }

                    @Override
                    public void onError(String message) {
                        error.postValue(message);
                    }
                });
    }

    public LiveData<WorkoutLogResponse> getLogResult() {
        return logResult;
    }

    public LiveData<String> getError() {
        return error;
    }
}
