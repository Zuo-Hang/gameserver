package com.example.gameservicedemo.bean.shop;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/19/11:41
 * @Description: 物品类型
 */
public enum ToolsType {

    PHYSICS(1,"物理加成类装备"),

    MAGIC(2,"法术加成类装备"),

    DEFENSE(3,"防御类装备"),

    MEDICINE(4,"药品"),

    PEST(5,"宠物")
    ;
    Integer code;
    String describe;

    ToolsType(Integer code, String describe) {
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
