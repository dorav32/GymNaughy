package com.gymnaughy.android.repository;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ListenerRegistration;
import com.gymnaughy.android.firebase.FirebaseAuthManager;
import com.gymnaughy.android.firebase.FirestoreRepository;
import com.gymnaughy.android.firebase.OnDataChangeListener;
import com.gymnaughy.android.model.CompletedSet;
import com.gymnaughy.android.model.Equipment;
import com.gymnaughy.android.model.FitnessLevel;
import com.gymnaughy.android.model.WorkoutLog;
import com.gymnaughy.android.model.WorkoutPlan;
import com.gymnaughy.android.network.ApiClient;
import com.gymnaughy.android.network.dto.PlanGenerateRequest;
import com.gymnaughy.android.network.dto.WorkoutLogRequest;
import com.gymnaughy.android.network.dto.WorkoutLogResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Single source of truth for plans and workout logs. Generation and logging are
 * writes, so they always go through backend-api; the resulting plan is then observed
 * in real time straight from Firestore (see {@link #observeCurrentPlan}).
 */
public class WorkoutRepository {

    private final FirebaseAuthManager authManager = new FirebaseAuthManager();
    private final FirestoreRepository firestoreRepository = new FirestoreRepository();

    public void generatePlan(FitnessLevel level, List<Equipment> equipment, String goal, int daysPerWeek,
                              RepositoryCallback<WorkoutPlan> callback) {
        PlanGenerateRequest request = new PlanGenerateRequest(level, equipment, goal, daysPerWeek);
        ApiClient.getService().generatePlan(request).enqueue(new Callback<WorkoutPlan>() {
            @Override
            public void onResponse(Call<WorkoutPlan> call, Response<WorkoutPlan> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Plan generation failed (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<WorkoutPlan> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public ListenerRegistration observeCurrentPlan(OnDataChangeListener<WorkoutPlan> listener) {
        FirebaseUser user = authManager.getCurrentUser();
        if (user == null) {
            listener.onError(new IllegalStateException("No signed-in user"));
            return null;
        }
        return firestoreRepository.observeCurrentPlan(user.getUid(), listener);
    }

    public void logWorkout(String planId, String dayId, List<CompletedSet> completedSets, long durationSeconds,
                            RepositoryCallback<WorkoutLogResponse> callback) {
        WorkoutLogRequest request = new WorkoutLogRequest(planId, dayId, completedSets, durationSeconds);
        ApiClient.getService().logWorkout(request).enqueue(new Callback<WorkoutLogResponse>() {
            @Override
            public void onResponse(Call<WorkoutLogResponse> call, Response<WorkoutLogResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Logging workout failed (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<WorkoutLogResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getWorkoutHistory(int limit, RepositoryCallback<List<WorkoutLog>> callback) {
        ApiClient.getService().getWorkoutHistory(limit).enqueue(new Callback<List<WorkoutLog>>() {
            @Override
            public void onResponse(Call<List<WorkoutLog>> call, Response<List<WorkoutLog>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Fetching history failed (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<List<WorkoutLog>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}
