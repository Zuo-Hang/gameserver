package com.example.gameservicedemo.game.skill.service;

import com.example.commondemo.base.RequestCode;
import com.example.gameservicedemo.base.IdGenerator;
import com.example.gameservicedemo.game.bag.service.BagService;
import com.example.gameservicedemo.game.buffer.bean.Buffer;
import com.example.gameservicedemo.base.bean.Creature;
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
    BufferService bufferService;
    @Autowired
    SkillService skillService;
    @Autowired
    MonsterAiService monsterAiService;
    @Autowired
    BagService bagService;
    @Autowired
    SceneObjectService sceneObjectService;
    @Autowired
    PlayerDataService playerDataService;
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

        attackSingle(initiator,target,gameScene,skill);
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
        /**


         initiator.setTarget(target);
         // 消耗mp和损伤目标hp
         //只有玩家角色释放技能才需要魔法消耗
         if (initiator instanceof PlayerBeCache) {
         PlayerBeCache player = (PlayerBeCache) initiator;
         player.setMp(player.getMp() - skill.getMpConsumption());
         }
         //        Integer hurt = 0;
         //        if (skill.getSkillHurtType().equals(SkillHurtType.PHYSICS.getType()) || skill.getSkillHurtType().equals(SkillHurtType.PH_REAL.getType())) {
         //            hurt = initiator.getPHurt();
         //        } else if (skill.getSkillHurtType().equals(SkillHurtType.MAGIC.getType()) || skill.getSkillHurtType().equals(SkillHurtType.MA_REAL.getType())) {
         //            hurt = initiator.getMHurt();
         //        }
         //        //技能造成的生命比值伤害
         //        int addHpHurt =0;
         //        if(Objects.nonNull(skill.getHPPercentage())){
         //            addHpHurt=skill.getHPPercentage() * target.getMaxHp() / 100;
         //        }
         //        //计算伤害 技能基础伤害=技能伤害值+法术/物理攻击提成+技能对敌人造成的生命比伤害加成
         //        int initialHurt = skill.getHurt() + hurt * skill.getAddHurtPercentage() / 100 + addHpHurt;
         //        GetHurtNum skillHurtNum = new GetHurtNum();
         //        skillHurtNum.getHurtNum(initiator, target, initialHurt, skill.getSkillHurtType());
         //        //更改属性-----------------------------------------------------------------------安全问题
         //对血量要进行判断，不能形成负值
         //        if(target.getHp() - skillHurtNum.hurt>0){
         //            target.setHp(target.getHp() - skillHurtNum.hurt);
         //        }else{
         //            target.setHp(0);
         //        }
         //        if(initiator instanceof PlayerBeCache){
         //            PlayerBeCache player = (PlayerBeCache) initiator;
         //            String s=target.getHp()==0?0+"%":target.getHp()*1.0/target.getMaxHp()+"%";
         //            notificationManager.notifyPlayer(player,MessageFormat.format("目标:{0} 当前血量:{1} 血量比值:{2}",
         //                    target.getName(),target.getHp(),s),RequestCode.ABOUT_AIM.getCode());
         //            if(skillHurtNum.isMarkUp){
         //                notificationManager.notifyPlayer(player,"发生暴击！",RequestCode.BAD_REQUEST.getCode());
         //            }
         //            if(Objects.nonNull((player).getPet())){
         //                (player).getPet().setTarget(target);
         //            }
         //            if(target instanceof PlayerBeCache){
         //                PlayerBeCache target1 = (PlayerBeCache) target;
         //                //击败后获取对方所有的装备
         //                if(playerDataService.checkIsDead(target1)){
         //                    Map<Long, Tools> equipmentBar = target1.getEquipmentBar();
         //                    equipmentBar.values().forEach(v->{
         //                        bagService.putInBag(player,v);
         //                    });
         //                    equipmentBar.clear();
         //                }
         //            }
         //        }
         target.setMagicShield(target.getMagicShield() - skillHurtNum.hurtToMaShield);
         target.setShield(target.getShield() - skillHurtNum.hurtToShield);
         notificationManager.notifyScene(gameScene,
         MessageFormat.format(" {0} 受到 {1} 技能 {2}攻击， 对魔法护盾造成{3}点伤害，对通用护盾造成{4}点伤害，对血量造成{5}点伤害。{6}现在的血量是{7}\n",
         target.getName(), initiator.getName(), skill.getName(),
         skillHurtNum.hurtToMaShield, skillHurtNum.hurtToShield,skillHurtNum.hurt,
         target.getName(),target.getHp()), RequestCode.SUCCESS.getCode());
         if(target.getHp().equals(0)){
         notificationManager.notifyScene(gameScene,
         MessageFormat.format("{0} 击败了 {1}",initiator.getName(),target.getName()), RequestCode.BAD_REQUEST.getCode());
         }
         //        // 如果技能触发的buffer不是0，则对敌方单体目标释放buffer
         //        if (!skill.getBuffer().equals(0)) {
         //            Buffer buffer = bufferService.getBuffer(skill.getBuffer());
         //            // 如果buffer存在则启动buffer
         //            Optional.ofNullable(buffer).map(
         //                    (b) -> bufferService.startBuffer(target, b)
         //            );
         //        }
         if(target instanceof PlayerBeCache){
         PlayerBeCache targetPlayer = (PlayerBeCache) target;
         //判断是否死亡，死亡则进行死亡处理
         playerDataService.isPlayerDead(targetPlayer,initiator);
         if(Objects.nonNull((targetPlayer).getPet())){
         (targetPlayer).getPet().setTarget(initiator);
         }
         notificationManager.notifyPlayer(targetPlayer,MessageFormat.format("你受到了来自{0}的攻击！",initiator.getName()),RequestCode.WARNING.getCode());
         playerDataService.showPlayerInfo(targetPlayer);
         //如果是受到物理伤害进行反伤
         if (skill.getSkillHurtType().equals(SkillHurtType.PHYSICS.getType()) && target.getSkillHaveMap().containsKey(24)) {
         Skill skill1 = new Skill();
         BeanUtils.copyProperties(skillService.getSkillById(24), skill1);
         skill1.setHurt((skillHurtNum.hurt + skillHurtNum.hurtToShield) * skill1.getAddHurtPercentage() / 100);
         skill1.setAddHurtPercentage(0);
         attackSingle(target, initiator, gameScene, skill1);
         }
         }
         //如果被攻击者是怪物，开启怪物ai
         if(target instanceof Monster){
         Monster monster = (Monster) target;
         monsterAiService.notifyMonsterBeAttack(initiator, monster, gameScene, skillHurtNum.hurt);
         monster.setTarget(initiator);
         monsterAiService.startAI(monster, gameScene);
         }

         */
    }

    /**
     * 召唤兽类型的技能
     */
    private void callPet(Creature initiator, Creature target, Scene gameScene, Skill skill) {
        SceneObject object = sceneObjectService.getSceneObject(skill.getCall());
        Pet pet = new Pet();
        BeanUtils.copyProperties(object,pet);
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
        gameScene.getMonsters().put(pet.getUuid(),pet);
        notificationManager.notifyScene(gameScene,
                MessageFormat.format("{0} 召唤了 {1}",initiator.getName(),pet.getName()),RequestCode.WARNING.getCode());
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
                        target.getName(),initiator.getName(),skill.getName(),skill.getHurt(), target.getHp()),RequestCode.WARNING.getCode());
        //如果被攻击者是怪物，开启怪物ai
        if(target instanceof Monster){
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
