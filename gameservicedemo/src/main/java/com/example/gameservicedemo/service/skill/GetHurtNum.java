package com.example.gameservicedemo.service.skill;

import com.example.gameservicedemo.bean.Buffer;
import com.example.gameservicedemo.bean.Creature;
import com.example.gameservicedemo.bean.PlayerBeCache;
import com.example.gameservicedemo.bean.shop.ToolsProperty;
import com.example.gameservicedemo.bean.shop.ToolsPropertyInfo;
import com.example.gameservicedemo.bean.skill.SkillHurtType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    /**
     * 获取某一技能对目标造成的伤害值：
     * @param initiator 技能发起方
     * @param target    技能承受方
     * @return
     */
    public  void getHurtNum(Creature initiator, Creature target, Integer initialHurt,Integer hurtType) {
        //不同的伤害类型进行不同的运算流程。
        if (SkillHurtType.PHYSICS.getType().equals(hurtType)) {
            //调用物理伤害
            physicsHurt((PlayerBeCache) initiator,(PlayerBeCache) target,initialHurt,hurtType);
        } else if (SkillHurtType.MAGIC.getType().equals(hurtType)) {
            //调用法术伤害
            magicHurt((PlayerBeCache) initiator,(PlayerBeCache) target,initialHurt, hurtType);
        } else if (SkillHurtType.PH_REAL.getType().equals(hurtType)) {
            //物理加成的真实伤害，没有反伤。无视防御、护盾。直接对生命值扣减。（可暴击）
            PlayerBeCache player = (PlayerBeCache) initiator;
            int baseHurt = player.getToolsInfluence().get(ToolsPropertyInfo.Physical_attack.getId()).getValue();
            //物理真实伤害可暴击
            Integer hurt = markUpHurt(player.getToolsInfluence(), baseHurt);
            this.hurt=hurt;
        }else if(SkillHurtType.MA_REAL.getType().equals(hurtType)) {
            //法术加成的真实伤害，没有反伤。无视防御、护盾。直接对生命值扣减。
            PlayerBeCache player = (PlayerBeCache) initiator;
            int hurt =  player.getToolsInfluence().get(ToolsPropertyInfo.Magic_Attack.getId()).getValue();
            this.hurt=hurt;
        }
    }

    /**
     * 法术伤害
     * 2.法术伤害：不可暴击、可防御穿透、不可反伤
     * @param player
     * @param target1
     * @param hurtType
     * @return
     */
    public  Integer magicHurt(PlayerBeCache player,PlayerBeCache target1, Integer initHurt,Integer hurtType){
        //获取属性
        Map<Integer, ToolsProperty> initiatorInfo = player.getToolsInfluence();
        Map<Integer, ToolsProperty> targetInfo = target1.getToolsInfluence();
        //计算承受者被穿透后的防御
        Integer defense = targetInfo.get(ToolsPropertyInfo.Magic_defense.getId()).getValue();
        Integer pierceThrough = initiatorInfo.get(ToolsPropertyInfo.Spell_penetration.getId()).getValue();
        Integer finalDefense = cutDownDefense(defense, pierceThrough);
        //得到最终伤害值
        int hurt = initHurt - finalDefense;
        //扣除护盾打出的真实伤害
        this.hurt = shield(hurt, target1, hurtType);
        return 0;
    }

    /**
     * 物理伤害
     * 1.物理伤害：可暴击、可防御穿透机制
     * @param player
     * @param target1
     * @param hurtType
     * @return
     */
    public  void   physicsHurt(PlayerBeCache player,PlayerBeCache target1,Integer initHurt, Integer hurtType){
        //获取属性
        Map<Integer, ToolsProperty> initiatorInfo = player.getToolsInfluence();
        Map<Integer, ToolsProperty> targetInfo = target1.getToolsInfluence();
        //计算承受者被穿透后的防御
        Integer defense = targetInfo.get(ToolsPropertyInfo.Physical_defense.getId()).getValue();
        Integer pierceThrough = initiatorInfo.get(ToolsPropertyInfo.Physical_penetration.getId()).getValue();
        Integer finalDefense = cutDownDefense(defense, pierceThrough);
        //进行暴击加成
        Integer shouldBeBear = markUpHurt(initiatorInfo, initHurt - defense);
        //得到最终伤害值
        int hurt = shouldBeBear - finalDefense;
        //扣除护盾打出的真实伤害
        this.hurt= shield(hurt, target1, hurtType);
    }

    /**
     * 进行暴击加成
     * （(末世、破军、回响等)为什么要在这个位置进行，其实这些伤害应该分开来一次一次的实现不好吗？）即打出技能伤害后，再遍历装备列表，装备中如果有这些buf则这些buf的执行器
     * 中再次调用技能系统，打出第二次伤害。
     *
     * @param map      属性集合
     * @param baseHurt 基础伤害
     * @return
     */
    public  Integer markUpHurt(Map<Integer, ToolsProperty> map, Integer baseHurt) {
        //暴击率
        Integer probability = map.get(ToolsPropertyInfo.Critical_hit_rate.getId()).getValue();
        //暴击效果
        Integer effect = map.get(ToolsPropertyInfo.Critical_hit_effect.getId()).getValue();
        double v = Math.random() * 100;
        //进行加成
        if (probability >= v) {
            baseHurt = baseHurt * effect / 100 + baseHurt;
        }
        return baseHurt;
    }

    /**
     * 对防御值进行穿透削减
     *
     * @param defense
     * @param pierceThrough
     * @return
     */
    public  Integer cutDownDefense(Integer defense, Integer pierceThrough) {
        return defense * (100-pierceThrough) / 100;
    }

    /**
     * 获得反伤值
     * @return
     */
    public  Integer BackInjury(PlayerBeCache target, Integer shouldBeBear) {
        ConcurrentHashMap<Integer, Buffer> bufferList = target.getBufferMap();
        //判断是否有反伤刺甲
        bufferList.contains(18);
        return shouldBeBear / 10 * 2;
    }

    /**
     * 护盾抵消部分伤害
     *
     * @param hurt
     * @param target
     * @param hurtType
     * @return
     */
    public  Integer shield(Integer hurt, PlayerBeCache target, Integer hurtType) {
        if (SkillHurtType.MAGIC.getType().equals(hurtType)) {
            if (target.getMagicShield() != 0) {
                if (hurt - target.getMagicShield() > 0) {
                    //护盾不足以抵抗伤害
                    hurtToMaShield= target.getMagicShield();
                    hurt=hurt-hurtToMaShield;
                } else {
                    //护盾可以抵抗所有伤害
                    hurtToMaShield=hurt;
                    hurt = 0;
                }
            }
        }
        //存在基础护盾
        if (target.getShield() > 0) {
            if (hurt - target.getShield() > 0) {
                //护盾不足以抵抗伤害
                hurtToShield=target.getShield();
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
