package com.example.gameservicedemo.game.scene.bean;


import com.example.gameservicedemo.game.buffer.bean.Buffer;
import com.example.gameservicedemo.game.skill.bean.Skill;

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
    Integer getId();
    String getName();
    Integer getHp();
    Integer getMp();
    void setMp(Integer mp);
    void setHp(Integer hp);
    Integer getMaxHp();
    Integer getMagicShield();
    Integer getShield();
    void setMagicShield(Integer magicShield);
    void setShield(Integer shield);
    Integer getState();
    void setState(Integer state);
    Creature getTarget();
    void setTarget(Creature target);
    Map<Integer,Skill> getCanUseSkillMap();
    void setCanUseSkillMap(Map<Integer,Skill> map);
    Map<Integer, Skill> getHasUseSkillMap();
    void setHasUseSkillMap(Map<Integer, Skill> skillMap);
    ConcurrentHashMap<Integer, Buffer> getBufferMap();
    void setBufferMap(ConcurrentHashMap<Integer,Buffer> bufferMap);

}
