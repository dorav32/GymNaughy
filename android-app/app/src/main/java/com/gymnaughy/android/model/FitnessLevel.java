package com.gymnaughy.android.model;

import com.google.gson.annotations.SerializedName;

public enum FitnessLevel {
    @SerializedName("beginner")
    BEGINNER("beginner"),
    @SerializedName("intermediate")
    INTERMEDIATE("intermediate"),
    @SerializedName("advanced")
    ADVANCED("advanced");

    private final String apiValue;

    FitnessLevel(String apiValue) {
        this.apiValue = apiValue;
    }

    public String getApiValue() {
        return apiValue;
    }
}
