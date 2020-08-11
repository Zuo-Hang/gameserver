package com.example.gameservicedemo.bean;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/11/17:59
 * @Description: 技能类型
 */
public enum SkillType {

    /** 对自身或友方使用 */
    FRIENDLY(1),

    /** 对敌人单人使用 **/
    ATTACK_SINGLE(2),

    /** 多人技能 **/
    ATTACK_MULTI(3),

    /** 召唤宠物 */
    CALL_PET(5),

    /** 只能自身使用 */
    ONLY_SELF(6),

    /** 嘲讽技能 **/
    TAUNT(7)



    ;


    SkillType(Integer typeId) {
        this.typeId = typeId;
    }

    private Integer typeId;


    public Integer getTypeId() {
        return typeId;
    }

}
