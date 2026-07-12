package com.gymnaughy.android.network.dto;

public class WorkoutLogResponse {
    private String logId;
    private int streak;
    private int totalWorkouts;

    public String getLogId() {
        return logId;
    }

    public int getStreak() {
        return streak;
    }

    public int getTotalWorkouts() {
        return totalWorkouts;
    }
}
