package com.example.gameservicedemo.game.skill.bean;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/24/17:38
 * @Description: 技能的主动被动枚举
 */
public enum SkillActiveAndPassiveType {
    ACTIVE(0, "主动技能"),
    PASSIVE_AT_GRADE_CHANGE(1, "被动技能,在等级变化时触发"),
    PASSIVE_AT_HP_CHANGE(2,"血量变化时，触发"),
    PASSIVE_AT_ACTIVE_SKILL(3,"发动主动技能时触发")
    ;
    Integer code;
    String describe;

    SkillActiveAndPassiveType(Integer code, String describe) {
        this.code = code;
        this.describe = describe;
    }

    public String getDescribe() {
        return describe;
    }

    public Integer getCode() {
        return code;
    }
}
