package com.gymnaughy.android.network;

import com.gymnaughy.android.model.Exercise;
import com.gymnaughy.android.model.User;
import com.gymnaughy.android.model.WorkoutLog;
import com.gymnaughy.android.model.WorkoutPlan;
import com.gymnaughy.android.network.dto.PlanGenerateRequest;
import com.gymnaughy.android.network.dto.SessionRequest;
import com.gymnaughy.android.network.dto.WorkoutLogRequest;
import com.gymnaughy.android.network.dto.WorkoutLogResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Retrofit contract for backend-api. Endpoint shapes are documented in docs/API_SPEC.md
 * and must stay in sync with backend-api/src/routes/*.
 */
public interface ApiService {

    @POST("auth/session")
    Call<User> createOrUpdateSession(@Body SessionRequest request);

    @GET("exercises")
    Call<List<Exercise>> getExercises(@Query("equipment") String equipmentCsv,
                                       @Query("muscle") String muscleGroup);

    @POST("plans/generate")
    Call<WorkoutPlan> generatePlan(@Body PlanGenerateRequest request);

    @GET("plans/me")
    Call<WorkoutPlan> getCurrentPlan();

    @POST("workouts/log")
    Call<WorkoutLogResponse> logWorkout(@Body WorkoutLogRequest request);

    @GET("workouts/history")
    Call<List<WorkoutLog>> getWorkoutHistory(@Query("limit") int limit);
}
