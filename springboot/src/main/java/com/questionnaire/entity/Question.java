package com.questionnaire.entity;
import java.io.Serializable;
import java.util.List;

/**
 * 题目表
 */
public class Question implements Serializable {
    private static final long serialVersionUID = 1L;

    /** ID */
    private Integer id;
    /** 题目名称 */
    private String name;
    /** 题目类型 */
    private String type;
    /** 问卷ID */
    private Integer pageId;
    /** 问卷名称 */
    private String pageName;
    /** 创建人ID */
    private Integer userId;
    /** 创建人 */
    private String userName;

    /** 创建时间 */
    private String createTime;

    /** 题目信息 */
    private List<QuestionItem> questionItemList;

    /** 填写数量 */
    private Integer count;
    /** 百分比值 */
    private Integer percentage;

    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<QuestionItem> getQuestionItemList() {
        return questionItemList;
    }

    public void setQuestionItemList(List<QuestionItem> questionItemList) {
        this.questionItemList = questionItemList;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getPageId() {
        return pageId;
    }

    public void setPageId(Integer pageId) {
        this.pageId = pageId;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
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
        return "Question{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", pageId=" + pageId +
                ", pageName='" + pageName + '\'' +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", createTime='" + createTime + '\'' +
                ", questionItemList=" + questionItemList +
                ", count=" + count +
                ", percentage=" + percentage +
                '}';
    }
}