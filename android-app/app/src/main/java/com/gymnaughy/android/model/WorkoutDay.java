package com.gymnaughy.android.model;

import java.util.List;

public class WorkoutDay {

    private String id;
    private String label;
    private List<PlannedExercise> exercises;

    public WorkoutDay() {
        // Required by Gson for deserialization.
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public List<PlannedExercise> getExercises() {
        return exercises;
    }
}
