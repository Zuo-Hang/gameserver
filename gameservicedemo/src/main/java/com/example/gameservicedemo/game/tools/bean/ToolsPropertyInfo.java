package com.example.gameservicedemo.game.tools.bean;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/19/18:47
 * @Description:
 */
public enum ToolsPropertyInfo {
    HP_gain(1,"HP增益"),
    MP_gain(2,"MP增益"),
    Physical_attack(3,"物理攻击"),
    Magic_Attack(4,"法术攻击"),
    Physical_penetration(5,"物理穿透"),
    Spell_penetration(6,"法术穿透"),
    Physical_defense(7,"物理防御"),
    Magic_defense(8,"法术防御"),
    Physical_blood_sucking(9,"物理吸血"),
    Magic_blood_sucking(10,"法术吸血"),
    Critical_hit_rate(11,"暴击率"),
    Cooling_reduction(12,"冷却缩减"),
    Critical_hit_effect(13,"暴击效果"),
    Restore_HP_speed(14,"恢复HP速度"),
    Restore_MP_speed(15,"恢复MP速度")
    ;
    Integer id;
    String describe;

    ToolsPropertyInfo(Integer id, String describe) {
        this.id = id;
        this.describe = describe;
    }

    public String getDescribe() {
        return describe;
    }

    public Integer getId() {
        return id;
    }
}
