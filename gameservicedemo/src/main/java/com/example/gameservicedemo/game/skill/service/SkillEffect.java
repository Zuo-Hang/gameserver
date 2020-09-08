package com.example.gameservicedemo.game.skill.service;

import com.example.commondemo.base.RequestCode;
import com.example.gameservicedemo.base.IdGenerator;
import com.example.gameservicedemo.game.bag.service.BagService;
import com.example.gameservicedemo.game.buffer.bean.Buffer;
import com.example.gameservicedemo.base.bean.Creature;
import com.example.gameservicedemo.game.hurt.ChangeCreatureInformation;
import com.example.gameservicedemo.game.hurt.ChangePlayerInformationImp;
import com.example.gameservicedemo.game.hurt.MakeHurt;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.service.PlayerDataService;
import com.example.gameservicedemo.game.scene.bean.Monster;
import com.example.gameservicedemo.game.scene.bean.Pet;
import com.example.gameservicedemo.game.scene.bean.SceneObject;
import com.example.gameservicedemo.game.scene.service.MonsterAiService;
import com.example.gameservicedemo.game.scene.service.SceneObjectService;
import com.example.gameservicedemo.game.skill.bean.Skill;
import com.example.gameservicedemo.game.skill.bean.SkillHurtType;
import com.example.gameservicedemo.game.skill.bean.SkillInfluenceType;
import com.example.gameservicedemo.game.scene.bean.Scene;
import com.example.gameservicedemo.game.tools.bean.Tools;
import com.example.gameservicedemo.manager.NotificationManager;
import com.example.gameservicedemo.game.buffer.service.BufferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
    MonsterAiService monsterAiService;
    @Autowired
    ChangePlayerInformationImp changePlayerInformationImp;
    @Autowired
    ChangeCreatureInformation changeCreatureInformation;
    @Autowired
    SceneObjectService sceneObjectService;
    @Autowired
    MakeHurt makeHurt;
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

        attackSingle(initiator, target, gameScene, skill);
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
        changeCreatureInformation.changeTarget(initiator,target);
        // 消耗mp和损伤目标hp
        //只有玩家角色释放技能才需要魔法消耗
        if (initiator instanceof PlayerBeCache) {
            PlayerBeCache player = (PlayerBeCache) initiator;
            changePlayerInformationImp.changePlayerMagic(player,player.getMp() - skill.getMpConsumption());
        }
        makeHurt.skillMakeHurt(initiator,target,gameScene,skill);
    }

    /**
     * 召唤兽类型的技能
     */
    private void callPet(Creature initiator, Creature target, Scene gameScene, Skill skill) {
        SceneObject object = sceneObjectService.getSceneObject(skill.getCall());
        Pet pet = new Pet();
        BeanUtils.copyProperties(object, pet);
        pet.setUuid(IdGenerator.getAnId());
        pet.setMaster(initiator);
        pet.setAttackTime(System.currentTimeMillis());
        pet.setAttackSpeed(1000);
        //需要添加技能（普通场景内的怪物没有技能，NPC没有技能）
        //设置攻击目标
        pet.setTarget(initiator.getTarget());
        PlayerBeCache initiator1 = (PlayerBeCache) initiator;
        initiator1.setPet(pet);
        //放入场景
        gameScene.getMonsters().put(pet.getUuid(), pet);
        notificationManager.notifyScene(gameScene,
                MessageFormat.format("{0} 召唤了 {1}", initiator.getName(), pet.getName()), RequestCode.WARNING.getCode());
        monsterAiService.startAI(pet, gameScene);
        // cd结束召唤兽就消失
//        TimedTaskManager.singleThreadSchedule(pet.getRefreshTime(),
//                ()-> {
//            notificationManager.notifyCreature(master,"你的宠物已经解散                                                ");
//            gameScene.getMonsters().remove(pet.getPetId());
//
//        });
    }

    /**
     * 嘲讽技能  只能对怪物使用
     */
    private void taunt(Creature initiator, Creature target, Scene gameScene, Skill skill) {

        // 将怪物目标设定为发起者
        target.setTarget(initiator);
        // 消耗mp和损伤目标hp
        initiator.setMp(initiator.getMp() - skill.getMpConsumption());
        target.setHp(target.getHp() - skill.getHurt());
        target.setHp(target.getHp() + skill.getHeal());

        notificationManager.notifyScene(gameScene,
                MessageFormat.format(" {0} 受到 {1} 技能 {2}攻击，  hp减少{3},当前hp为 {4}， {0}受到嘲讽\n",
                        target.getName(), initiator.getName(), skill.getName(), skill.getHurt(), target.getHp()), RequestCode.WARNING.getCode());
        //如果被攻击者是怪物，开启怪物ai
        if (target instanceof Monster) {
            Monster monster = (Monster) target;
            monster.setTarget(initiator);
            monsterAiService.startAI(monster, gameScene);
        }
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
