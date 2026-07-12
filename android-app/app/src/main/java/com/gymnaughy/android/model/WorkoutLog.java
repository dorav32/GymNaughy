package com.gymnaughy.android.model;

import java.util.List;

public class WorkoutLog {

    private String id;
    private String userId;
    private String planId;
    private String dayId;
    private List<CompletedSet> completedSets;
    private long durationSeconds;
    private String completedAt;

    public WorkoutLog() {
        // Required by Gson for deserialization.
    }

    public WorkoutLog(String planId, String dayId, List<CompletedSet> completedSets, long durationSeconds) {
        this.planId = planId;
        this.dayId = dayId;
        this.completedSets = completedSets;
        this.durationSeconds = durationSeconds;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getPlanId() {
        return planId;
    }

    public String getDayId() {
        return dayId;
    }

    public List<CompletedSet> getCompletedSets() {
        return completedSets;
    }

    public long getDurationSeconds() {
        return durationSeconds;
    }

    public String getCompletedAt() {
        return completedAt;
    }
}
