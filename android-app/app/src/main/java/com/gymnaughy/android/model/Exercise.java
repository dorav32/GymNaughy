package com.gymnaughy.android.model;

import java.util.List;

public class Exercise {

    private String id;
    private String name;
    private String muscleGroup;
    private List<String> equipment;
    private FitnessLevel difficulty;
    private String instructions;
    private String imageUrl;

    public Exercise() {
        // Required by Gson for deserialization.
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public List<String> getEquipment() {
        return equipment;
    }

    public FitnessLevel getDifficulty() {
        return difficulty;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
