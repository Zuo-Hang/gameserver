package com.example.gameservicedemo.bean.skill;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/11/17:59
 * @Description: 技能影响类型
 */
public enum SkillInfluenceType {
    FRIENDLY(1,"对自身或友方使用"),
    ATTACK_SINGLE(2,"对敌人单人使用"),
    ATTACK_MULTI(3,"多人技能"),
    CALL_PET(5,"召唤宠物"),
    ONLY_SELF(6,"只能自身使用"),
    TAUNT(7,"嘲讽技能")
    ;

    SkillInfluenceType(Integer typeId, String describe) {
        this.typeId = typeId;
        this.describe = describe;
    }

    private Integer typeId;
    private String describe;


    public Integer getTypeId() {
        return typeId;
    }

    public String getDescribe() {
        return describe;
    }
}
