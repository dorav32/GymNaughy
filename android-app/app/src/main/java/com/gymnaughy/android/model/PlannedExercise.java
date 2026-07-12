package com.gymnaughy.android.model;

public class PlannedExercise {

    private String exerciseId;
    private int sets;
    private int reps;
    private int restSeconds;

    public PlannedExercise() {
        // Required by Gson for deserialization.
    }

    public String getExerciseId() {
        return exerciseId;
    }

    public int getSets() {
        return sets;
    }

    public int getReps() {
        return reps;
    }

    public int getRestSeconds() {
        return restSeconds;
    }
}
