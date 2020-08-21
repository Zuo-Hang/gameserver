package com.example.gameservicedemo.bean.skill;

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
    @EntityName(column = "技能类型")
    private Integer skillType;
    /**
     * 技能影响类型
     */
    @EntityName(column = "影响类型")
    private Integer skillInfluenceType;

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

    @EntityName(column = "描述")
    private String   describe;

    @EntityName(column = "技能语音")
    private String sound;

    /**
     * 技能上一次使用的时间
     */
    private Long activeTime;
}
