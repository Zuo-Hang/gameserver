package com.example.gameservicedemo.game.skill.service;

import com.example.commondemo.base.RequestCode;
import com.example.gameservicedemo.game.buffer.bean.Buffer;
import com.example.gameservicedemo.base.bean.Creature;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.skill.bean.Skill;
import com.example.gameservicedemo.game.skill.bean.SkillHurtType;
import com.example.gameservicedemo.game.skill.bean.SkillInfluenceType;
import com.example.gameservicedemo.game.scene.bean.Scene;
import com.example.gameservicedemo.manager.NotificationManager;
import com.example.gameservicedemo.game.buffer.service.BufferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/13/18:24
 * @Description: 技能效果
 */
@Component
@Slf4j
public class SkillEffect {
    @Autowired
    BufferService bufferService;
    @Autowired
    SkillService skillService;
    @Autowired
    NotificationManager notificationManager;
    private Map<Integer, ISkillEffect> skillEffectMap = new HashMap<>();

    {
        skillEffectMap.put(SkillInfluenceType.ATTACK_SINGLE.getTypeId(), this::attackSingle);
        skillEffectMap.put(SkillInfluenceType.CALL_PET.getTypeId(), this::callPet);
        skillEffectMap.put(SkillInfluenceType.TAUNT.getTypeId(), this::taunt);
        skillEffectMap.put(SkillInfluenceType.FRIENDLY.getTypeId(), this::friendly);
        // 群体攻击技能
        skillEffectMap.put(SkillInfluenceType.ATTACK_MULTI.getTypeId(), this::attackSMulti);

    }

    /**
     * 根据技能类型触发技能效果
     *
     * @param skillTypeId 技能类型id
     * @param initiator   施放者
     * @param target      施放目标
     * @param gameScene   场景
     * @param skill       技能
     */
    public void castSkill(Integer skillTypeId,
                          Creature initiator,
                          Creature target,
                          Scene gameScene,
                          Skill skill) {
        Optional.ofNullable(skillEffectMap.get(skillTypeId)).
                ifPresent(s -> s.skillEffect(initiator, target, gameScene, skill));
    }

    /**
     * 群体攻击技能
     */
    private void attackSMulti(Creature initiator, Creature target, Scene gameScene, Skill skill) {
        // 消耗mp和损伤目标hp
        //只有玩家角色释放技能才需要魔法消耗
        if (initiator instanceof PlayerBeCache) {
            PlayerBeCache player = (PlayerBeCache) initiator;
            player.setMp(player.getMp() - skill.getMpConsumption());
        }
        target.setHp(target.getHp() - skill.getHurt());
        //target.setHp(target.getHp() + skill.getHeal());
        notificationManager.notifyScene(gameScene,
                MessageFormat.format(" {0} 受到 {1} 群体攻击技能 {2}攻击，  hp减少{3},当前hp为 {4}\n",
                        target.getName(), initiator.getName(), skill.getName(), skill.getHurt(), target.getHp()), RequestCode.SUCCESS.getCode());
    }

    /**
     * 施放单体攻击技能造成影响(伤害型）
     * （(末世、破军、回响等)为什么要在这个位置进行，其实这些伤害应该分开来一次一次的实现不好吗？）即打出技能伤害后，再遍历装备列表，装备中如果有这些buf则这些buf的执行器
     * * 中再次调用技能系统，打出第二次伤害。
     *
     * @param initiator 施放者
     * @param target    施放目标
     * @param gameScene 场景
     * @param skill     技能
     */
    private void attackSingle(Creature initiator, Creature target, Scene gameScene, Skill skill) {
        // 消耗mp和损伤目标hp
        initiator.setMp(initiator.getMp() - skill.getMpConsumption());
        Integer hurt = 0;
        if (skill.getSkillHurtType().equals(SkillHurtType.PHYSICS.getType()) || skill.getSkillHurtType().equals(SkillHurtType.PH_REAL.getType())) {
            hurt = initiator.getPHurt();
        } else if (skill.getSkillHurtType().equals(SkillHurtType.MAGIC.getType()) || skill.getSkillHurtType().equals(SkillHurtType.MA_REAL.getType())) {
            hurt = initiator.getMHurt();
        }
        //技能造成的生命比值伤害
        int addHpHurt = skill.getHPPercentage() * target.getMaxHp() / 100;
        //计算伤害 技能基础伤害=技能伤害值+法术/物理攻击提成+技能对敌人造成的生命比伤害加成
        int initialHurt = skill.getHurt() + hurt * skill.getAddHurtPercentage() / 100 + addHpHurt;
        GetHurtNum skillHurtNum = new GetHurtNum();
        skillHurtNum.getHurtNum(initiator, target, initialHurt, skill.getSkillHurtType());
        //更改属性-----------------------------------------------------------------------安全问题
        target.setHp(target.getHp() - skillHurtNum.hurt);
        target.setMagicShield(target.getMagicShield() - skillHurtNum.hurtToMaShield);
        target.setShield(target.getShield() - skillHurtNum.hurtToShield);
        // 如果技能触发的buffer不是0，则对敌方单体目标释放buffer
        if (!skill.getBuffer().equals(0)) {
            Buffer buffer = bufferService.getBuffer(skill.getBuffer());
            // 如果buffer存在则启动buffer
            Optional.ofNullable(buffer).map(
                    (b) -> bufferService.startBuffer(target, b)
            );
        }
        //如果是受到物理伤害进行反伤
        if (skill.getSkillType().equals(SkillHurtType.PHYSICS) && target.getSkillHaveMap().containsKey(24)) {
            Skill skill1 = new Skill();
            BeanUtils.copyProperties(skillService.getSkillById(24), skill1);
            skill1.setHurt((skillHurtNum.hurt + skillHurtNum.hurtToShield) * skill1.getAddHurtPercentage() / 100);
            skill1.setAddHurtPercentage(0);
            attackSingle(target, initiator, gameScene, skill1);
        }
        notificationManager.notifyScene(gameScene,
                MessageFormat.format(" {0} 受到 {1} 技能 {2}攻击，  hp减少{3},当前hp为 {4}\n",
                        target.getName(), initiator.getName(), skill.getName(), skill.getHurt(), target.getHp()), RequestCode.SUCCESS.getCode());
    }

    /**
     * 召唤兽类型的技能
     */
    private void callPet(Creature initiator, Creature target, Scene gameScene, Skill skill) {
    }

    /**
     * 嘲讽技能
     */
    private void taunt(Creature initiator, Creature target, Scene gameScene, Skill skill) {
    }

    /**
     * 对友方使用的技能
     */
    private void friendly(Creature initiator, Creature target, Scene gameScene, Skill skill) {
        // 消耗mp和治疗目标hp
        initiator.setMp(initiator.getMp() - skill.getMpConsumption());
        Integer nowHp = target.getHp() + skill.getHeal() > target.getMaxHp() ? target.getMaxHp() : target.getHp() + skill.getHeal();
        target.setHp(nowHp);
        //释放技能音效
        notificationManager.notifyScene(gameScene, skill.getSound(), RequestCode.SUCCESS.getCode());
        //场景内通知
        if (skill.getHeal() > 0) {
            notificationManager.notifyScene(gameScene, MessageFormat.format("{0} 受到 {1} 的治疗,hp增加了 {2}，当前hp是 {3}",
                    target.getName(), initiator.getName(), skill.getHeal(), target.getHp()), RequestCode.SUCCESS.getCode());
        }
    }
}
