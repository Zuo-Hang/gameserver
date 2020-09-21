package com.example.gameservicedemo.game.task.bean;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/21/15:00
 * @Description: 任务类型
 */
public enum  TaskType {
    KILL_MONSTER(1,"击杀特定小怪N只"),
    COLLECT_TOOLS(2,"获得N件极品装备"),
    TALK_WITH(3,"和某个npc对话"),
    MISSION(4,"完成某一系列任务"),
    LEVEL(5,"等级提升到N级"),
    MONEY(6,"当前金币达到xxxx"),
    TEAM(7,"第一次组队"),
    GUILD(8,"第一次加入公会"),
    PK(9,"第一次在pk中战胜"),
    GAME_COPY(10,"通关某个副本"),
    EQUIPMENT(11,"穿戴的装备等级总和达到XXX"),
    TRADE(12,"第一次与玩家交易"),
    FRIEND(13,"添加一个好友"),
    FIRST_ACHIEVEMENT(14,""),
    ;
    Integer code;
    String describe;

    TaskType(Integer code, String describe) {
        this.code = code;
        this.describe = describe;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescribe() {
        return describe;
    }
}
