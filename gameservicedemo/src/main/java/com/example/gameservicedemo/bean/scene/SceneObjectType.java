package com.example.gameservicedemo.bean.scene;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/11/16:26
 * @Description: 场景对象的类型
 */
public enum SceneObjectType {

    /** npc类型*/
    NPC(1),
    /** 野怪类型 */
    WILD_MONSTER(2),
    /** 副本怪物类型 */
    INSTANCE_MONSTER(3),
    /** 召唤兽 **/
    PET(4),
    ;

    Integer type;

    SceneObjectType(Integer type) {
        this.type = type;
    }
    public Integer getType() {
        return type;
    }
}
