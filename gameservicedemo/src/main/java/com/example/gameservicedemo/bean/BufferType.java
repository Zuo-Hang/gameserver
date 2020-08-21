package com.example.gameservicedemo.bean;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/21/14:54
 * @Description: buf种类
 */
public enum  BufferType {
    /**
     *有关当前角色属性相关（属性改变就要执行）
     */
    ABOUT_ATILITY(0,"有关属性改变"),
    /**
     * 有关等级提升（等级改变就要执行）
     */
    ABOUT_GRADE(1,"有关等级改变"),
    /**
     * 特别buf 有关血量（自动护盾/名刀/复活甲）（血量达到一定值触发）
     */
    ABOUT_HP(3,"有关血量"),
    /**
     * 与受到伤害相关（反甲，反伤）（被攻击触发）
     */
    ABOUT_BE_ATTACK(4,"有关被攻击"),
    /**
     * 不利型buf（限制回血，减速）（攻击者为被攻击者加上buf，异步，一定时间后移除此buf）
     */
    DISADVANTAGEOUS(5,"不利型buf"),
    /**
     * 有关技能：使用某特定技能后触发
     */
    ABOUT_SKILL(6,"被技能触发类buf"),
    /**
     * 发起攻击时与攻击力计算有关（比如额外造成敌人生命值百分比的伤害）
     */
    ABOUT_ATTACK(7,"有关攻击"),

    ABOUT_ADD(8,"增益"),
    ;
    //类型数值
    Integer type;
    //类型描述
    String describe;

    BufferType(Integer type, String describe) {
        this.type = type;
        this.describe = describe;
    }
}
