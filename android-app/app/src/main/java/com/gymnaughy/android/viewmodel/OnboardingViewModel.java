package com.gymnaughy.android.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gymnaughy.android.model.Equipment;
import com.gymnaughy.android.model.FitnessLevel;
import com.gymnaughy.android.model.WorkoutPlan;
import com.gymnaughy.android.repository.RepositoryCallback;
import com.gymnaughy.android.repository.WorkoutRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Backs the two-step onboarding flow (fitness level, then equipment) and triggers the
 * initial plan generation once both selections are made.
 */
public class OnboardingViewModel extends ViewModel {

    private static final int DEFAULT_DAYS_PER_WEEK = 4;
    private static final String DEFAULT_GOAL = "hypertrophy";

    private final WorkoutRepository workoutRepository = new WorkoutRepository();

    private FitnessLevel selectedLevel;
    private final List<Equipment> selectedEquipment = new ArrayList<>();

    private final MutableLiveData<WorkoutPlan> generatedPlan = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);

    public void selectFitnessLevel(FitnessLevel level) {
        this.selectedLevel = level;
    }

    public void toggleEquipment(Equipment equipment, boolean selected) {
        if (selected) {
            if (!selectedEquipment.contains(equipment)) {
                selectedEquipment.add(equipment);
            }
        } else {
            selectedEquipment.remove(equipment);
        }
    }

    public boolean canSubmit() {
        return selectedLevel != null && !selectedEquipment.isEmpty();
    }

    public void submit() {
        if (!canSubmit()) {
            error.setValue("Pick a fitness level and at least one equipment option.");
            return;
        }
        loading.setValue(true);
        workoutRepository.generatePlan(selectedLevel, selectedEquipment, DEFAULT_GOAL, DEFAULT_DAYS_PER_WEEK,
                new RepositoryCallback<WorkoutPlan>() {
                    @Override
                    public void onSuccess(WorkoutPlan data) {
                        loading.postValue(false);
                        generatedPlan.postValue(data);
                    }

                    @Override
                    public void onError(String message) {
                        loading.postValue(false);
                        error.postValue(message);
                    }
                });
    }

    public LiveData<WorkoutPlan> getGeneratedPlan() {
        return generatedPlan;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }
}
