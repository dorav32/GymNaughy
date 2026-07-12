package com.gymnaughy.android.model;

import java.util.List;

public class WorkoutPlan {

    private String id;
    private String userId;
    private FitnessLevel fitnessLevel;
    private String goal;
    private int daysPerWeek;
    private List<WorkoutDay> days;
    private String createdAt;

    public WorkoutPlan() {
        // Required by Gson for deserialization.
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public FitnessLevel getFitnessLevel() {
        return fitnessLevel;
    }

    public String getGoal() {
        return goal;
    }

    public int getDaysPerWeek() {
        return daysPerWeek;
    }

    public List<WorkoutDay> getDays() {
        return days;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public WorkoutDay findDay(String dayId) {
        if (days == null) {
            return null;
        }
        for (WorkoutDay day : days) {
            if (day.getId().equals(dayId)) {
                return day;
            }
        }
        return null;
    }
}
