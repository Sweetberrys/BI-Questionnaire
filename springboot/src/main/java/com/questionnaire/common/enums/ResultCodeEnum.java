package com.questionnaire.common.enums;

public enum ResultCodeEnum {
    SUCCESS("200", "成功"),

    PARAM_ERROR("400", "参数异常"),
    TOKEN_INVALID_ERROR("401", "登录失败"),
    TOKEN_CHECK_ERROR("401", "请重新登录"),
    PARAM_LOST_ERROR("4001", "参数缺失"),

    SYSTEM_ERROR("500", "系统异常"),
    USER_EXIST_ERROR("5001", "用户账号已存在"),
    USER_NOT_LOGIN("5002", "用户账号未登录"),
    USER_ACCOUNT_ERROR("5003", "用户账号或密码错误"),
    USER_NOT_EXIST_ERROR("5004", "用户账号不存在"),
    PARAM_PASSWORD_ERROR("5005", "用户账号原密码输入错误"),
    FALSE_PASSWORD_ERROR("5006", "用户账号密码长度不正确"),
    DATA_IMPORT_ERROR("5007","数据导入错误"),
    NULL_EXCEL("5008","空表，请重新上传"),
    FILE_MORE("5009","文件超出10M限制，请重新上传"),
    FILE_NAME_MORE("50010","文件名过长，请重新上传"),
    FILE_NAME_NO("50011","文件不符合要求，请重新上传"),
    TOO_MANY_REQUEST("50012","请求过于频繁"),
    AI_GENERATE_FAIL("50013","AI生成错误")
    ;

    public String code;
    public String msg;

    ResultCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
