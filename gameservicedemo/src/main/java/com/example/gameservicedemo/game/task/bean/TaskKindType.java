package com.example.gameservicedemo.game.task.bean;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/21/14:52
 * @Description: 任务类型种类
 */
public enum  TaskKindType {
    /** 成就 **/
    ACHIEVEMENT(0,"成就"),

    /** 新手 **/
    NOVICE(1,"新手"),

    /** 主线 **/
    MAIN(2,"主线"),


    SIDE(3,"支线"),

    UNLIMITED(4,"无限制"),

    DAILY(5,"日常"),

    WEEK(6,"每周"),

    MONTH(7,"每月"),
    ;
    private Integer code;
    private String describe;

    TaskKindType(Integer code, String describe) {
        this.code = code;
        this.describe = describe;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescribe() {
        return describe;
    }
}
