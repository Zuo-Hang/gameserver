package com.example.gameservicedemo.bean.shop;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/11/17:38
 * @Description: 道具类型
 */
public enum ToolsType {

    /** 普通物品，占一个格子*/
    COMMON_THING(1),

    /** 装备 **/
    EQUIPMENT(2),

    /** 可堆叠 **/
    STACKABLE(3)
    ;

    private Integer type;

    ToolsType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
