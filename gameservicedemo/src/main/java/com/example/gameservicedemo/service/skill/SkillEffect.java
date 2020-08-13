package com.example.gameservicedemo.service.skill;

import com.example.commondemo.base.RequestCode;
import com.example.gameservicedemo.bean.Creature;
import com.example.gameservicedemo.bean.Skill;
import com.example.gameservicedemo.bean.SkillType;
import com.example.gameservicedemo.bean.scene.Scene;
import com.example.gameservicedemo.manager.NotificationManager;
import lombok.extern.slf4j.Slf4j;
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
    NotificationManager notificationManager;
    private Map<Integer, ISkillEffect> skillEffectMap = new HashMap<>();

    {
        skillEffectMap.put(SkillType.ATTACK_SINGLE.getTypeId(),this::attackSingle);
        skillEffectMap.put(SkillType.CALL_PET.getTypeId(),this::callPet);
        skillEffectMap.put(SkillType.TAUNT.getTypeId(),this::taunt);
        skillEffectMap.put(SkillType.FRIENDLY.getTypeId(),this::friendly);
        // 群体攻击技能
        skillEffectMap.put(SkillType.ATTACK_MULTI.getTypeId(),this::attackSMulti);

    }

    /**
     *  根据技能类型触发技能效果
     * @param skillTypeId 技能类型id
     * @param initiator 施放者
     * @param target 施放目标
     * @param gameScene 场景
     * @param skill 技能
     */
    public void castSkill(Integer skillTypeId,
                          Creature initiator,
                          Creature target,
                          Scene gameScene,
                          Skill skill) {
        Optional.ofNullable(skillEffectMap.get(skillTypeId)).ifPresent(s -> s.skillEffect(initiator,target,gameScene,skill));
    }

    /**
     *  群体攻击技能
     */
    private void attackSMulti(Creature initiator, Creature target, Scene gameScene, Skill skill) {
        // 消耗mp和损伤目标hp
        initiator.setMp(initiator.getMp() - skill.getMpConsumption());
        target.setHp(target.getHp() - skill.getHurt());
        target.setHp(target.getHp() + skill.getHeal());
        notificationManager.notifyScene(gameScene.getId(),
                MessageFormat.format(" {0} 受到 {1} 群体攻击技能 {2}攻击，  hp减少{3},当前hp为 {4}\n",
                        target.getName(),initiator.getName(),skill.getName(),skill.getHurt(), target.getHp()), RequestCode.SUCCESS.getCode());
    }
    /**
     *  施放单体攻击技能造成影响
     * @param initiator 施放者
     * @param target 施放目标
     * @param gameScene 场景
     * @param skill 技能
     */
    private  void attackSingle(Creature initiator, Creature target, Scene gameScene, Skill skill) {
        // 消耗mp和损伤目标hp
        initiator.setMp(initiator.getMp() - skill.getMpConsumption());
        target.setHp(target.getHp() - skill.getHurt());
        target.setHp(target.getHp() + skill.getHeal());

//        // 如果技能触发的buffer不是0，则对敌方单体目标释放buffer
//        if (!skill.getBuffer().equals(0)) {
//            Buffer buffer = bufferService.getTBuffer(skill.getBuffer());
//            // 如果buffer存在则启动buffer
//            Optional.ofNullable(buffer).map(
//                    (b) -> bufferService.startBuffer(target,b)
//            );
//        }
//
//        notificationManager.notifyScene(gameScene,
//                MessageFormat.format(" {0} 受到 {1} 技能 {2}攻击，  hp减少{3},当前hp为 {4}\n",
//                        target.getName(),initiator.getName(),skill.getName(),skill.getHurt(), target.getHp()));
    }
    /**
     *  召唤兽类型的技能
     */
    private void callPet(Creature initiator, Creature target, Scene gameScene, Skill skill) {}
    /**
     *  嘲讽技能
     */
    private void taunt(Creature initiator, Creature target, Scene gameScene, Skill skill) {}
    /**
     *  对友方使用的技能
     */
    private void friendly(Creature initiator, Creature target, Scene gameScene, Skill skill) {
// 消耗mp和治疗目标hp
        initiator.setMp(initiator.getMp() - skill.getMpConsumption());
        target.setHp(target.getHp() + skill.getHeal());

        if(skill.getHeal() > 0) {
            notificationManager.notifyScene(gameScene.getId(), MessageFormat.format("{0} 受到 {1} 的治疗,hp增加了 {2}，当前hp是 {3}",
                    target.getName(),initiator.getName(),skill.getHeal(),target.getHp()),RequestCode.SUCCESS.getCode());
        }
    }
}
