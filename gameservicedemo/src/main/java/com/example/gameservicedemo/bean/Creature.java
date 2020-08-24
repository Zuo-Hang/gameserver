package com.example.gameservicedemo.bean;


import com.example.gameservicedemo.bean.shop.ToolsProperty;
import com.example.gameservicedemo.bean.skill.Skill;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/12/17:50
 * @Description: 生物：可以被实现为玩家、NPC、怪物等。在这里定义这类生物所具有的通用操作。
 */
public interface Creature {
    /**
     * 获取id
     * @return
     */
    Integer getId();

    /**
     * 获取名称
     * @return
     */
    String getName();

    /**
     * 获取血量
     * @return
     */
    Integer getHp();
    Integer getMaxHp();
    /**
     * 设置血量
     * @param hp
     */
    void setHp(Integer hp);
    void setMaxHp(Integer maxHp);

    Integer getMagicShield();
    Integer getShield();
    void setMagicShield(Integer magicShield);
    void setShield(Integer shield);


    /**
     * 获取魔法值（能量值）
     * @return
     */
    Integer getMp();
    Integer getMaxMp();

    /**
     * 设置魔法值
     * @param mp
     */
    void setMp(Integer mp);
    void setMaxMp(Integer maxMp);

    /**
     * 获取状态
     * @return
     */
    Integer getState();

    /**
     * 设置状态
     * @param state
     */
    void setState(Integer state);
    /**  活物当前处于CD的技能 */
    Map<Integer, Skill> getHasUseSkillMap();
    void setHasUseSkillMap(Map<Integer, Skill> skillMap);

    /** 活物的当前buffer */
    ConcurrentHashMap<Integer,Buffer>  getBufferMap();
    void setBufferMap(ConcurrentHashMap<Integer,Buffer> bufferMap);

    Map<Integer, ToolsProperty> getToolsInfluence();
    void setToolsInfluence(Map<Integer, ToolsProperty> map);
    /** 活物的当前目标 */
    Creature getTarget();
    void setTarget(Creature target);

    Map<Integer,Skill> getCanUseSkillMap();

}
