package com.example.gameservicedemo.game.hurt;

import com.example.commondemo.base.RequestCode;
import com.example.gameservicedemo.base.bean.Creature;
import com.example.gameservicedemo.game.bag.service.BagService;
import com.example.gameservicedemo.game.buffer.bean.Buffer;
import com.example.gameservicedemo.game.buffer.service.BufferService;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.service.PlayerDataService;
import com.example.gameservicedemo.game.scene.bean.Scene;
import com.example.gameservicedemo.game.skill.bean.Skill;
import com.example.gameservicedemo.game.skill.bean.SkillHurtType;
import com.example.gameservicedemo.game.skill.service.GetHurtNum;
import com.example.gameservicedemo.game.tools.bean.Tools;
import com.example.gameservicedemo.manager.NotificationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/04/16:59
 * @Description: 将伤害来进行执行
 */
@Component
public class MakeHurt  {
    @Autowired
    ChangeInformation changeInformation;
    @Autowired
    NotificationManager notificationManager;
    @Autowired
    PlayerDataService playerDataService;
    @Autowired
    BagService bagService;
    @Autowired
    BufferService bufferService;

    /**
     * 调用用户更改类
     * @param murderer
     * @param target
     * @param scene
     * @param hurtNum
     */
    public void makeHurt(Creature murderer, Creature target, Scene scene, GetHurtNum hurtNum) {
        //进行生命值扣减
        changeInformation.changeHp(target,-hurtNum.hurt);
        if(murderer instanceof PlayerBeCache){
            PlayerBeCache player = (PlayerBeCache) murderer;
            String s=target.getHp()==0?0+"%":target.getHp()*1.0/target.getMaxHp()+"%";
            notificationManager.notifyPlayer(player, MessageFormat.format("目标:{0} 当前血量:{1} 血量比值:{2}",
                    target.getName(),target.getHp(),s), RequestCode.ABOUT_AIM.getCode());
            if(hurtNum.isMarkUp){
                notificationManager.notifyPlayer(player,"发生暴击！",RequestCode.BAD_REQUEST.getCode());
            }
            if(Objects.nonNull((player).getPet())){
                (player).getPet().setTarget(target);
            }
            if(target instanceof PlayerBeCache){
                PlayerBeCache target1 = (PlayerBeCache) target;
                //击败后获取对方所有的装备
                if(playerDataService.checkIsDead(target1)){
                    Map<Long, Tools> equipmentBar = target1.getEquipmentBar();
                    equipmentBar.values().forEach(v->{
                        bagService.putInBag(player,v);
                    });
                    equipmentBar.clear();
                }
            }
        }
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
