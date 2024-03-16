package com.questionnaire.entity;


import java.io.Serializable;

/**
 * 题目内容表
 */
public class QuestionItem implements Serializable {
    private static final long serialVersionUID = 1L;

    /** ID */
    private Integer id;
    /** 内容 */
    private String content;
    /** 题目ID */
    private Integer questionId;
    /** 题目名称 */
    private String questionName;
    /** 创建人ID */
    private Integer userId;
    /** 创建人 */
    private String userName;
    /** 题目内容创建时间 */
    private String createTime;

    /** 选项数量 */
    private Long count;
    private Integer percentage;

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }

    public String getQuestionName() {
        return questionName;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "QuestionItem{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", questionId=" + questionId +
                ", questionName='" + questionName + '\'' +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", createTime='" + createTime + '\'' +
                ", count=" + count +
                ", percentage=" + percentage +
                '}';
    }
}