package com.gymnaughy.android.network.dto;

import com.gymnaughy.android.model.Equipment;
import com.gymnaughy.android.model.FitnessLevel;

import java.util.List;

public class PlanGenerateRequest {
    private final FitnessLevel fitnessLevel;
    private final List<Equipment> equipment;
    private final String goal;
    private final int daysPerWeek;

    public PlanGenerateRequest(FitnessLevel fitnessLevel, List<Equipment> equipment, String goal, int daysPerWeek) {
        this.fitnessLevel = fitnessLevel;
        this.equipment = equipment;
        this.goal = goal;
        this.daysPerWeek = daysPerWeek;
    }
}
