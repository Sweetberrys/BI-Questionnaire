package com.questionnaire.common.enums;

public enum ChartStatusTypeEnum {

    WAIT("wait"),

    RUNNING("running"),

    SUCCEED("succeed"),

    FAILED("failed");


    private String value;

    public String getValue() {
        return value;
    }

    ChartStatusTypeEnum(String value) {
        this.value = value;
    }

}
