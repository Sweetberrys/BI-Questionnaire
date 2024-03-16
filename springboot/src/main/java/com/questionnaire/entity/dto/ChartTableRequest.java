package com.questionnaire.entity.dto;

import java.io.Serializable;
import java.util.List;

public class ChartTableRequest implements Serializable {
    private List<String> goalNameType;

    public List<String> getGoalNameType() {
        return goalNameType;
    }

    public void setGoalNameType(List<String> goalNameType) {
        this.goalNameType = goalNameType;
    }
}
