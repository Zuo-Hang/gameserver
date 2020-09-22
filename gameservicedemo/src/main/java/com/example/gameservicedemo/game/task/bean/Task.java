package com.example.gameservicedemo.game.task.bean;

import com.example.gameservicedemo.util.excel.EntityName;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/21/11:56
 * @Description:
 */
@Data
public class Task {
    @EntityName(column = "id")
    Integer id;

    @EntityName(column = "名字")
    private String name;

    @EntityName(column = "类型")
    private Integer type;

    /**
     * 成就，主线，日常，周常，月常
     **/
    @EntityName(column = "种类")
    private Integer kind;


    @EntityName(column = "等级")
    private Integer level;


    @EntityName(column = "接受条件")
    private String acceptConditions;


    @EntityName(column = "完成条件")
    private String completionConditions;


    @EntityName(column = "描述")
    private String describe;

    @EntityName(column = "奖励金币")
    private Integer rewardMoney;

    @EntityName(column = "奖励物品装备")
    private String rewardTools;


    /**
     * 完成条件
     */
    private TaskCondition taskCondition;

    /**
     * 装备奖励<装备id，数量>
     */
    private Map<Integer, Integer> rewardToolsMap = new HashMap<>();




}
