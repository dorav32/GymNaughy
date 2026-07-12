package com.gymnaughy.android.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.ListenerRegistration;
import com.gymnaughy.android.model.User;
import com.gymnaughy.android.model.WorkoutPlan;
import com.gymnaughy.android.repository.UserRepository;
import com.gymnaughy.android.repository.WorkoutRepository;

/**
 * Feeds the Dashboard tab: today's plan summary + the user's live streak, both observed
 * in real time straight from Firestore (see docs/ARCHITECTURE.md for why this screen
 * reads directly instead of polling backend-api).
 */
public class DashboardViewModel extends ViewModel {

    private final UserRepository userRepository = new UserRepository();
    private final WorkoutRepository workoutRepository = new WorkoutRepository();

    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<WorkoutPlan> currentPlan = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    private ListenerRegistration userListener;
    private ListenerRegistration planListener;

    public void start() {
        userListener = userRepository.observeCurrentUser(new com.gymnaughy.android.firebase.OnDataChangeListener<User>() {
            @Override
            public void onChanged(User data) {
                user.postValue(data);
            }

            @Override
            public void onError(Exception e) {
                error.postValue(e.getMessage());
            }
        });

        planListener = workoutRepository.observeCurrentPlan(new com.gymnaughy.android.firebase.OnDataChangeListener<WorkoutPlan>() {
            @Override
            public void onChanged(WorkoutPlan data) {
                currentPlan.postValue(data);
            }

            @Override
            public void onError(Exception e) {
                error.postValue(e.getMessage());
            }
        });
    }

    public LiveData<User> getUser() {
        return user;
    }

    public LiveData<WorkoutPlan> getCurrentPlan() {
        return currentPlan;
    }

    public LiveData<String> getError() {
        return error;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (userListener != null) {
            userListener.remove();
        }
        if (planListener != null) {
            planListener.remove();
        }
    }
}
