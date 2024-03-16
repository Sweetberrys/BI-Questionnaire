package com.questionnaire.entity;



import java.io.Serializable;

/**
 * 问卷表
 */
public class Pages implements Serializable {
    private static final long serialVersionUID = 1L;

    /** ID */
    private Integer id;
    /** 模板名称 */
    private String name;
    /** 模板介绍 */
    private String descr;
    /** 封面图 */
    private String img;
    /** 使用次数 */
    private Integer count;
    /** 创建人ID */
    private Integer userId;
    /** 创建时间 */
    private String createTime;
    /** 是否公开 */
    private String open;
    /** 是否发布 */
    private String saved;
    /** 用户名称 */
    private String userName;

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

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getSaved() {
        return saved;
    }

    public void setSaved(String saved) {
        this.saved = saved;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "Pages{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", descr='" + descr + '\'' +
                ", img='" + img + '\'' +
                ", count=" + count +
                ", userId=" + userId +
                ", createTime='" + createTime + '\'' +
                ", open='" + open + '\'' +
                ", saved='" + saved + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}