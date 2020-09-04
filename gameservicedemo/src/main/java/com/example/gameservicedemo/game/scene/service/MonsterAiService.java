package com.example.gameservicedemo.game.scene.service;

import com.example.commondemo.base.RequestCode;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.service.PlayerDataService;
import com.example.gameservicedemo.base.bean.Creature;
import com.example.gameservicedemo.game.scene.bean.Monster;
import com.example.gameservicedemo.game.scene.bean.Pet;
import com.example.gameservicedemo.game.scene.bean.Scene;
import com.example.gameservicedemo.game.skill.bean.Skill;
import com.example.gameservicedemo.game.skill.bean.SkillHurtType;
import com.example.gameservicedemo.game.skill.service.GetHurtNum;
import com.example.gameservicedemo.game.skill.service.SkillService;
import com.example.gameservicedemo.manager.NotificationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/25/15:49
 * @Description: 怪物Ai活动
 */
@Service
public class MonsterAiService {

    @Autowired
    NotificationManager notificationManager;
    @Autowired
    SkillService skillService;
    @Autowired
    SceneObjectService sceneObjectService;
    @Autowired
    MonsterDropsService monsterDropsService;
    @Autowired
    PlayerDataService playerDataService;
    /**
     *  怪物进行攻击
     * @param monster 怪物
     * @param target 目标
     */
    private void monsterAttack(Monster monster, Creature target, Scene gameScene) {
        Integer physicalAttack = monster.getPhysicalAttack();
        //调用伤害计算系统-------------------------------------------------------------------
        GetHurtNum getHurtNum = new GetHurtNum();
        getHurtNum.getHurtNum(monster,target,monster.getPHurt(), SkillHurtType.PHYSICS.getType());
        target.setHp(target.getHp() - getHurtNum.hurt);
        notificationManager.notifyScene(gameScene,
                MessageFormat.format("{0}在攻击{1}，造成了{2}点伤害，{3}当前的hp为 {4} \n",
                        monster.getName(),
                        target.getName(),
                        monster.getPhysicalAttack(),
                        target.getName(),
                        target.getHp()
                ),RequestCode.SUCCESS.getCode());
        if (target instanceof PlayerBeCache) {
            playerDataService.isPlayerDead((PlayerBeCache) target,monster);
            playerDataService.showPlayerInfo((PlayerBeCache) target);
        } else {
            monsterBeAttack(monster,(Monster)target,gameScene,physicalAttack);
        }

    }

    /**
     *  怪物被攻击并广播
     * @param creature 玩家
     * @param monster    怪物目标
     * @param gameScene 游戏场景
     * @param damage    伤害
     */
    public void notifyMonsterBeAttack(Creature creature,Monster monster,Scene gameScene,Integer damage) {
        monsterBeAttack(creature,monster,gameScene,damage);
    }

    /**
     *  怪物被攻击
     * @param creature 玩家
     * @param monster    怪物目标
     * @param gameScene 游戏场景
     * @param damage    伤害
     */
    public void monsterBeAttack(Creature creature,Monster monster,Scene gameScene,Integer damage) {

        // 将怪物当前目标设置为玩家,让怪物攻击玩家
        if (Objects.isNull(monster.getTarget())) {
            monster.setTarget(creature);
        }
        PlayerBeCache player = null;
        if (creature instanceof PlayerBeCache) {
            player = (PlayerBeCache) creature;
        }

        if (creature instanceof Pet && ((Pet) creature).getMaster() instanceof PlayerBeCache) {
            Pet pet = (Pet) creature;
            player =  (PlayerBeCache) pet.getMaster();
        }

        if (Objects.nonNull(player)) {
            // 如果怪物死亡
            if (sceneObjectService.sceneObjectAfterDead(monster)) {
                notificationManager.notifyScene(gameScene,MessageFormat.format("{0}被{1}击败！",
                        monster.getName(),player.getName()),RequestCode.WARNING.getCode());
                // 结算掉落，这里暂时直接放到背包里
                monsterDropsService.dropItem(player,monster);
                // 怪物死亡的处理
                //EventBus.publish(new MonsterEventDeadEvent(player,monster,gameScene,damage));
            }
        }
    }

    /**
     *  怪物（包括玩家宠物）自动攻击
     * @param monster   怪物
     * @param gameScene 战斗的场景
     */
    public void startAI(Monster monster, Scene gameScene) {
        Creature target = monster.getTarget();
        if (target instanceof PlayerBeCache){
            // 玩家不在场景内，不进行攻击
            if (null == gameScene.getPlayers().get(target.getId())) {
                monster.setTarget(null);
                return;
            }// 目标死了,不进行攻击
            if (playerDataService.isPlayerDead((PlayerBeCache) target, monster)) {
                monster.setTarget(null);
                return;
            }
        }
        //除玩家以外的其他目标
        if (target.getHp() <=0 || target.getState() == -1) {
            monster.setTarget(null);
            return;
        }
        // 怪物死亡了，不进行攻击
        if (monster.getHp() <=0 || monster.getState() == -1) {
            monster.setTarget(null);
            return;
        }
        if ((monster.getAttackTime() + monster.getAttackSpeed()) < System.currentTimeMillis()) {
            // 进行普通攻击
            monsterAttack(monster, target,gameScene);
            // 更新普通攻击的攻击时间
            monster.setAttackTime(System.currentTimeMillis());
        }
        // 技能冷却好了就使用技能
        monster.getSkillHaveMap().values().forEach(skill -> {
            if(System.currentTimeMillis()- skill.getActiveTime()>skill.getCd()){
                if (skillService.castSkill(monster,target,gameScene,skill)) {
                    if (target instanceof PlayerBeCache) {
                        playerDataService.isPlayerDead((PlayerBeCache) target,monster);
                    }  else {
                        monsterBeAttack(monster,(Monster)target,gameScene,skill.getHurt());
                    }
                }
            }
        });
    }
    /**
     *     怪物使用技能，从怪物拥有的技能随机触发
     * @param monster 怪物
     * @param target 怪物攻击的目标
     */
    private void monsterUseSkill(Monster monster, Creature target, Scene gameScene) {

        Arrays.stream(monster.getSkills().split(","))
                .map(Integer::valueOf).parallel()
                // 随机返回一个技能id
                .findAny().ifPresent(
                skillId -> {
                    // 如果技能存在，则释放技能
                    Skill skill = monster.getSkillHaveMap().get(skillId);
                    if (skillService.castSkill(monster,target,gameScene,skill)) {
                        if (target instanceof PlayerBeCache) {
                            playerDataService.isPlayerDead((PlayerBeCache) target,monster);
                        }  else {
                            monsterBeAttack(monster,(Monster)target,gameScene,skill.getHurt());
                        }
                    }
                }
        );
    }
}
