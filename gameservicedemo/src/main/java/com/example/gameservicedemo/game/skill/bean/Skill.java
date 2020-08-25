package com.example.gameservicedemo.game.skill.bean;

import com.example.gameservicedemo.util.excel.EntityName;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/11/17:35
 * @Description: 技能实体类
 */
@Data
public class Skill {
    @EntityName(column = "id")
    private Integer id;
    @EntityName(column = "名字")
    private String name;
    /**
     * 技能类型：天赋/装备/常驻
     */
    /**
     * 技能影响类型
     */
    @EntityName(column = "影响类型")
    private Integer skillInfluenceType;
    @EntityName(column = "技能类型")
    private Integer skillType;
    @EntityName(column = "技能主被动")
    private Integer skillActiveOrPassiveType;
    @EntityName(column = "技能伤害类型")
    private Integer skillHurtType;

    /**
     * 前摇
     */
    @EntityName(column = "施法时间")
    private Integer castTime;
    @EntityName(column = "cd")
    private Integer cd;
    @EntityName(column = "消耗mp")
    private Integer mpConsumption;
    @EntityName(column = "伤害量")
    private Integer hurt;
    @EntityName(column = "治疗量")
    private Integer heal;
    @EntityName(column = "等级")
    private Integer level;
    @EntityName(column = "buffer")
    private Integer buffer;
    @EntityName(column = "召唤")
    private Integer call;
    @EntityName(column = "对mp的改变比值")
    private Integer MPPercentage;
    @EntityName(column = "额外伤害比值")
    private Integer addHurtPercentage;
    /**
     * 大于0影响自己
     * 小于0影响敌人
     */
    @EntityName(column = "造成生命影响")
    private Integer HPPercentage;
    @EntityName(column = "描述")
    private String   describe;
    @EntityName(column = "技能语音")
    private String sound;
    /**
     * 技能上一次使用的时间
     */
    private Long activeTime;
}
