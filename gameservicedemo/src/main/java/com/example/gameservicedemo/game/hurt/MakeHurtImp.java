package com.example.gameservicedemo.game.hurt;

import com.example.gameservicedemo.base.bean.Creature;
import com.example.gameservicedemo.game.buffer.bean.Buffer;
import com.example.gameservicedemo.game.buffer.service.BufferService;
import com.example.gameservicedemo.game.scene.bean.Scene;
import com.example.gameservicedemo.game.skill.bean.Skill;
import com.example.gameservicedemo.game.skill.bean.SkillHurtType;
import com.example.gameservicedemo.game.skill.service.GetHurtNum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/04/16:59
 * @Description: 实现 {@link MakeHurt} 接口，并将伤害来源进行扩展。
 */
@Component
public class MakeHurtImp implements MakeHurt {
    @Autowired
    BufferService bufferService;

    @Override
    public void makeHurt(Creature murderer, Creature target, Scene scene, GetHurtNum hurtNum) {

    }

    /**
     * 造成单体技能伤害
     *
     * @param murderer
     * @param target
     * @param scene
     * @param skill
     */
    public void skillMakeHurt(Creature murderer, Creature target, Scene scene, Skill skill) {
        GetHurtNum getHurtNum = computeSkillHurtNum(murderer, target, skill);
        //执行伤害
        makeHurt(murderer,target,scene,getHurtNum);
        // 如果技能触发的buffer不是0，则对敌方单体目标释放buffer
        if (!skill.getBuffer().equals(0)) {
            Buffer buffer = bufferService.getBuffer(skill.getBuffer());
            // 如果buffer存在则启动buffer
            Optional.ofNullable(buffer).map(
                    (b) -> bufferService.startBuffer(target, b)
            );
        }
    }

    /**
     * 造成群体技能伤害
     *
     * @param murderer
     * @param targetList
     * @param scene
     * @param skill
     */
    public void skillMakeHurt(Creature murderer, List<Creature> targetList, Scene scene, Skill skill) {
        targetList.forEach(target -> {
            skillMakeHurt(murderer, target, scene,skill);
        });
    }

    /**
     * 普通的伤害(怪物的攻击)
     *
     * @param murderer
     * @param target
     * @param scene
     */
    public void ordinaryMakeHurt(Creature murderer, Creature target, Scene scene) {
        //获取影响值
        GetHurtNum getHurtNum = computeOrdinaryHurtNum(murderer, target,
                murderer.getPHurt(),SkillHurtType.PHYSICS.getType());
        //------------------------------------------------------转换线程
        //执行伤害
        makeHurt(murderer,target,scene,getHurtNum);
    }

    /**
     * 普通的群体伤害
     *
     * @param murderer
     * @param targetList
     * @param scene
     */
    public void ordinaryMakeHurt(Creature murderer, List<Creature> targetList, Scene scene) {
        targetList.forEach(target -> {
            ordinaryMakeHurt(murderer, target, scene);
        });
    }

    /**
     * 计算凶手可以对目标造成的伤害值
     *
     * @param murderer 凶手
     * @param target   目标
     * @param skill    技能
     * @return
     */
    private GetHurtNum computeSkillHurtNum(Creature murderer, Creature target, Skill skill) {
        Integer hurt = 0;
        if (skill.getSkillHurtType().equals(SkillHurtType.PHYSICS.getType()) || skill.getSkillHurtType().equals(SkillHurtType.PH_REAL.getType())) {
            hurt = murderer.getPHurt();
        } else if (skill.getSkillHurtType().equals(SkillHurtType.MAGIC.getType()) || skill.getSkillHurtType().equals(SkillHurtType.MA_REAL.getType())) {
            hurt = murderer.getMHurt();
        }
        //技能造成的生命比值伤害
        int addHpHurt = 0;
        if (Objects.nonNull(skill.getHPPercentage())) {
            addHpHurt = skill.getHPPercentage() * target.getMaxHp() / 100;
        }
        //计算伤害 技能基础伤害=技能伤害值+法术/物理攻击提成+技能对敌人造成的生命比伤害加成
        int initialHurt = skill.getHurt() + hurt * skill.getAddHurtPercentage() / 100 + addHpHurt;
        return computeOrdinaryHurtNum(murderer, target, initialHurt, skill.getSkillHurtType());
    }

    /**
     * 计算普通伤害造成的伤害值
     *
     * @param murderer
     * @param target
     * @param initialHurt
     * @return
     */
    private GetHurtNum computeOrdinaryHurtNum(Creature murderer, Creature target, Integer initialHurt,Integer hurtType) {
        GetHurtNum skillHurtNum = new GetHurtNum();
        skillHurtNum.getHurtNum(murderer, target, initialHurt, hurtType);
        return skillHurtNum;
    }
}
