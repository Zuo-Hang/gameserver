package com.example.gameservicedemo.game.task.bean;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/22/20:30
 * @Description:
 */
public enum Condition {
    NUMBER(0,"以数量为目标"),
    ID(1,"以特定id为目标")
    ;
    Integer code;
    String describe;

    Condition(Integer code, String describe) {
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
