package com.gymnaughy.android.model;

import java.util.ArrayList;
import java.util.List;

public class CompletedSet {

    private String exerciseId;
    private int setsCompleted;
    private List<Integer> repsCompleted;

    public CompletedSet() {
        this.repsCompleted = new ArrayList<>();
    }

    public CompletedSet(String exerciseId) {
        this.exerciseId = exerciseId;
        this.setsCompleted = 0;
        this.repsCompleted = new ArrayList<>();
    }

    public String getExerciseId() {
        return exerciseId;
    }

    public int getSetsCompleted() {
        return setsCompleted;
    }

    public List<Integer> getRepsCompleted() {
        return repsCompleted;
    }

    public void recordSet(int reps) {
        repsCompleted.add(reps);
        setsCompleted = repsCompleted.size();
    }
}
