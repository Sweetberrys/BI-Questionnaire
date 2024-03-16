package com.questionnaire.common.enums;

public enum QuestionTypeEnum {
    SINGLE("单选题"),
    MULTIPLE("多选题"),
    TEXT("填空题");


    private String value;

    public String getValue() {
        return value;
    }

    QuestionTypeEnum(String value){
        this.value=value;
    }


}
