package com.example.gameservicedemo.game.tools.bean;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/23/10:52
 * @Description: 装备等级
 */
public enum  ToolsLevel {
    PRIMARY(0,"初级"),
    INTERMEDIATE(1,"中级"),
    SENIOR(2,"高级"),
    TOP(3,"顶级"),
    ;
    Integer code;
    String describe;

    ToolsLevel(Integer code, String describe) {
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
