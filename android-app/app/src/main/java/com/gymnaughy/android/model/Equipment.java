package com.gymnaughy.android.model;

import com.google.gson.annotations.SerializedName;

/**
 * Mirrors the EQUIPMENT_OPTIONS enumeration documented in docs/DATA_MODEL.md.
 * Kept as a fixed enum (rather than a free-text string) so the onboarding UI
 * can render a checklist without needing a network round-trip first.
 */
public enum Equipment {
    @SerializedName("bodyweight")
    BODYWEIGHT("bodyweight", "Bodyweight only"),
    @SerializedName("dumbbells")
    DUMBBELLS("dumbbells", "Dumbbells"),
    @SerializedName("resistance_bands")
    RESISTANCE_BANDS("resistance_bands", "Resistance bands"),
    @SerializedName("full_gym")
    FULL_GYM("full_gym", "Full gym access");

    private final String apiValue;
    private final String displayName;

    Equipment(String apiValue, String displayName) {
        this.apiValue = apiValue;
        this.displayName = displayName;
    }

    public String getApiValue() {
        return apiValue;
    }

    public String getDisplayName() {
        return displayName;
    }
}
