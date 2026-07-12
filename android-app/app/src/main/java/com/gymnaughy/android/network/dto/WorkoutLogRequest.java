package com.gymnaughy.android.network.dto;

import com.gymnaughy.android.model.CompletedSet;

import java.util.List;

public class WorkoutLogRequest {
    private final String planId;
    private final String dayId;
    private final List<CompletedSet> completedSets;
    private final long durationSeconds;

    public WorkoutLogRequest(String planId, String dayId, List<CompletedSet> completedSets, long durationSeconds) {
        this.planId = planId;
        this.dayId = dayId;
        this.completedSets = completedSets;
        this.durationSeconds = durationSeconds;
    }
}
