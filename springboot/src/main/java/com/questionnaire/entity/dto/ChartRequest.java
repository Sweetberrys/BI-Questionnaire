package com.questionnaire.entity.dto;

import com.questionnaire.entity.Question;

import java.util.List;
import java.util.Map;

public class ChartRequest {
    private Map<String, Object> answerCount;
    private List<Question> questionList;
    private List<String> questionGoalNameType;

    public List<String> getQuestionGoalNameType() {
        return questionGoalNameType;
    }

    public void setQuestionGoalNameType(List<String> questionGoalNameType) {
        this.questionGoalNameType = questionGoalNameType;
    }

    public Map<String, Object> getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(Map<String, Object> answerCount) {
        this.answerCount = answerCount;
    }

    public List<Question> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<Question> questionList) {
        this.questionList = questionList;
    }
}
