package com.example.gameservicedemo.bean.skill;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/21/17:07
 * @Description:
 */
public enum  SkillHurtType {
    PHYSICS(0,"物理伤害"),
    MAGIC(1,"法术伤害"),
    PH_REAL(3,"物理加成类真实伤害"),
    MA_REAL(4,"法术加成类真实伤害")
    ;
    Integer type;
    String describe;

    SkillHurtType(Integer type, String describe) {
        this.type = type;
        this.describe = describe;
    }

    public String getDescribe() {
        return describe;
    }

    public Integer getType() {
        return type;
    }
}
