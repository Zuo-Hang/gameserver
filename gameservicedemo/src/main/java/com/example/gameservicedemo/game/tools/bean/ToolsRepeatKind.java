package com.example.gameservicedemo.game.tools.bean;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/11/17:38
 * @Description: 道具类型
 */
public enum ToolsRepeatKind {

    /** 普通物品，占一个格子*/
    COMMON_THING(1),

    /** 装备 **/
    EQUIPMENT(2),

    /** 可堆叠 **/
    STACKABLE(3)
    ;

    private Integer type;

    ToolsRepeatKind(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
