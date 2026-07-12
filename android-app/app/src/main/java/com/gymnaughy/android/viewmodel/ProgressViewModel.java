package com.gymnaughy.android.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gymnaughy.android.model.WorkoutLog;
import com.gymnaughy.android.repository.RepositoryCallback;
import com.gymnaughy.android.repository.WorkoutRepository;

import java.util.List;

public class ProgressViewModel extends ViewModel {

    private static final int HISTORY_LIMIT = 60;

    private final WorkoutRepository workoutRepository = new WorkoutRepository();

    private final MutableLiveData<List<WorkoutLog>> history = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public void loadHistory() {
        workoutRepository.getWorkoutHistory(HISTORY_LIMIT, new RepositoryCallback<List<WorkoutLog>>() {
            @Override
            public void onSuccess(List<WorkoutLog> data) {
                history.postValue(data);
            }

            @Override
            public void onError(String message) {
                error.postValue(message);
            }
        });
    }

    public LiveData<List<WorkoutLog>> getHistory() {
        return history;
    }

    public LiveData<String> getError() {
        return error;
    }
}
