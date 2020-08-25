package com.example.gameservicedemo.game.skill.bean;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/21/14:41
 * @Description: 技能类型
 */
public enum SkillType {
    ROLE(0,"角色类型所带的常驻技能"),
    TALENT(1,"玩家可自定义的天赋技能"),
    EQUIPMENT(2,"装备所带来的增益型技能"),
    ;
    Integer type;
    String describe;

    SkillType(Integer type, String describe) {
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
