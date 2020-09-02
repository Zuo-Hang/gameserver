package com.example.commondemo.base;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/29/15:27
 * @Description:自定义请求码
 */
public enum RequestCode {
    /** 操作成功 */
    SUCCESS(200, "OK"),

    ABOUT_PLAYER(1212,"有关角色信息"),

    ABOUT_SCENE(1213,"有关场景"),

    ABOUT_BAG(1214,"有关背包"),

    ABOUT_EQU(1215,"有关装备"),

    WARNING(1216,"警告"),

    ABOUT_SKILL(1217,"有关技能"),

    ABOUT_AIM(1218,"有关目标"),

    BAD_REQUEST(400, "Bad Request"),
    /**找不到 */
    NOT_FOUND(404, "Not Found"),
    //未知内部错误
    INTERNAL_SERVER_ERROR(500, "Unknown Internal Error"),
    //无效的参数
    NOT_VALID_PARAM(40005, "Not valid Params"),
    //操作不被支持
    NOT_SUPPORTED_OPERATION(40006, "Operation not supported"),
    //未登录
    NOT_LOGIN(50000, "Not Login");

    private int code;
    private String standardMessage;

    RequestCode(int code, String standardMessage) {
        this.code = code;
        this.standardMessage = standardMessage;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStandardMessage() {
        return standardMessage;
    }

    public void setStandardMessage(String standardMessage) {
        this.standardMessage = standardMessage;
    }
}
