package com.example.gameservicedemo.game.skill.service;

import com.example.commondemo.base.RequestCode;
import com.example.gameservicedemo.base.bean.Creature;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.tools.bean.ToolsProperty;
import com.example.gameservicedemo.game.tools.bean.ToolsPropertyInfo;
import com.example.gameservicedemo.game.skill.bean.SkillHurtType;
import com.example.gameservicedemo.manager.NotificationManager;
import com.example.gameservicedemo.util.ProbabilityUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/21/16:29
 * @Description: 伤害运算器
 */
public class GetHurtNum {
    /**
     * 对血量造成的伤害(真伤)
     * 对魔法护盾造成的伤害
     * 对基础护盾造成的伤害
     */
    public int hurt=0;
    public int hurtToMaShield=0;
    public int hurtToShield=0;
    public boolean isMarkUp=false;

    /**
     * 获取某一伤害对目标造成的各方面损伤
     * @param murderer 技能发起方
     * @param victim    技能承受方
     * @param initialHurt 基础伤害值
     * @param hurtType  伤害类型
     * @return
     */
    public  void getHurtNum(Creature murderer, Creature victim, Integer initialHurt,Integer hurtType) {
        //不同的伤害类型进行不同的运算流程。
        if (SkillHurtType.PHYSICS.getType().equals(hurtType)) {
            physicsHurt(murderer,victim,initialHurt,hurtType);
        } else if (SkillHurtType.MAGIC.getType().equals(hurtType)) {
            magicHurt(murderer,victim,initialHurt,hurtType);
        } else if (SkillHurtType.PH_REAL.getType().equals(hurtType)) {
            //物理加成的真实伤害无视防御、护盾。直接对生命值扣减。（可暴击）
            if(murderer instanceof PlayerBeCache){
                initialHurt = markUpHurt(((PlayerBeCache)murderer).getToolsInfluence(), initialHurt);
            }
            this.hurt=initialHurt;
        }else if(SkillHurtType.MA_REAL.getType().equals(hurtType)) {
            //法术加成的真实伤害无视防御、护盾。直接对生命值扣减。
            this.hurt=initialHurt;
        }
    }

    /**
     * 2.法术伤害：不可暴击、可防御穿透、不可反伤
     * @return
     */
    public  void magicHurt(Creature murderer,Creature victim, Integer initHurt,Integer hurtType){
        //计算承受者被穿透后的防御
        Integer finalDefense = cutDownDefense(victim.getMDefense(), murderer.getMPenetration());
        //得到最终伤害值
        int hurt = initHurt - finalDefense;
        //扣除护盾打出的真实伤害
        this.hurt = shield(hurt, victim, hurtType);
    }

    /**
     * 1.物理伤害：可暴击、可防御穿透机制
     */
    public  void   physicsHurt(Creature murderer, Creature victim,Integer initHurt, Integer hurtType){
        //计算穿透后的防御值
        Integer finalDefense = cutDownDefense(victim.getPDefense(), murderer.getPPenetration());
        //只有玩家可以打出暴击
        if(murderer instanceof PlayerBeCache){
            Map<Integer, ToolsProperty> initiatorInfo = ((PlayerBeCache)murderer).getToolsInfluence();
            //将玩家打出的伤害进行暴击
            initHurt= markUpHurt(initiatorInfo, initHurt - finalDefense);
        }
        //扣除护盾打出的真实伤害
        this.hurt= shield(initHurt, victim, hurtType);
    }

    /**
     * 进行暴击加成
     */
    public  Integer markUpHurt(Map<Integer, ToolsProperty> map, Integer baseHurt) {
        //暴击率
        Integer probability = map.get(ToolsPropertyInfo.Critical_hit_rate.getId()).getValue();
        //暴击效果
        Integer effect = map.get(ToolsPropertyInfo.Critical_hit_effect.getId()).getValue();
        if (ProbabilityUtil.getProbability(probability)) {
            isMarkUp=true;
            baseHurt = baseHurt * effect / 100 + baseHurt;
        }
        return baseHurt;
    }

    /**
     * 对防御值进行穿透削减
     */
    public  Integer cutDownDefense(Integer defense, Integer pierceThrough) {
        return defense * (100-pierceThrough) / 100;
    }

    /**
     * 计算对目标具有的护盾造成的伤害
     */
    public  Integer shield(Integer hurt, Creature victim, Integer hurtType) {
        if (SkillHurtType.MAGIC.getType().equals(hurtType)) {
            if (victim.getMagicShield() != 0) {
                if (hurt - victim.getMagicShield() > 0) {
                    //护盾不足以抵抗伤害
                    hurtToMaShield= victim.getMagicShield();
                    hurt=hurt-hurtToMaShield;
                } else {
                    //护盾可以抵抗所有伤害
                    hurtToMaShield=hurt;
                    hurt = 0;
                }
            }
        }
        //存在基础护盾
        if (victim.getShield() > 0) {
            if (hurt - victim.getShield() > 0) {
                //护盾不足以抵抗伤害
                hurtToShield=victim.getShield();
                hurt=hurt-hurtToShield;
            } else {
                //护盾可以抵抗所有伤害
                hurtToShield=hurt;
                hurt = 0;
            }
        }
        return hurt;
    }
}
