package com.example.gameservicedemo.game.skill.service;

import com.example.commondemo.base.RequestCode;
import com.example.gameservicedemo.game.player.service.PlayerDataService;
import com.example.gameservicedemo.game.player.service.PlayerLoginService;
import com.example.gameservicedemo.base.bean.Creature;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.scene.bean.Monster;
import com.example.gameservicedemo.game.scene.cache.MonsterCache;
import com.example.gameservicedemo.game.skill.bean.Skill;
import com.example.gameservicedemo.game.scene.bean.Scene;
import com.example.gameservicedemo.game.player.cache.RoleTypeCache;
import com.example.gameservicedemo.game.skill.cache.SkillCache;
import com.example.gameservicedemo.manager.NotificationManager;
import com.example.gameservicedemo.manager.TimedTaskManager;
import com.example.gameservicedemo.game.player.service.PlayerService;
import com.example.gameservicedemo.game.scene.service.SceneObjectService;
import com.example.gameservicedemo.game.scene.service.SceneService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/13/15:39
 * @Description: 主要进行对技能的处理
 */
@Service
@Slf4j
public class SkillService {
    @Autowired
    PlayerService playerService;
    @Autowired
    PlayerLoginService playerLoginService;
    @Autowired
    SceneService sceneService;
    @Autowired
    RoleTypeCache roleTypeCache;
    @Autowired
    PlayerDataService playerDataService;
    @Autowired
    MonsterCache monsterCache;
    @Autowired
    SceneObjectService sceneObjectService;
    @Autowired
    NotificationManager notificationManager;
    @Autowired
    SkillEffect skillEffect;

    /**
     * 对己方使用技能
     *
     * @param context
     * @param skillId
     */
    public void useSkillToSelf(ChannelHandlerContext context, Integer skillId) {
        //获取技能、玩家、使用技能的场景
        Skill skill = SkillCache.get(skillId);
        PlayerBeCache player = playerLoginService.getPlayerByContext(context);
        Scene scene = sceneService.getScene(player.getNowAt());
        if (!canUseSkill(player, skill)) {
            notificationManager.notifyPlayer(player, "你现在不可以使用这个技能", RequestCode.NOT_SUPPORTED_OPERATION.getCode());
            return ;
        }
        skill=player.getSkillHaveMap().get(skill.getId());
        castSkill(player,player,scene,skill);
        skill.setActiveTime(System.currentTimeMillis());
        //将全部的装备耐久度减一
        player.getEquipmentBar().values().forEach(tools->{
            tools.setDurability(tools.getDurability()-1);
        });
        playerDataService.showPlayerEqu(player);
        playerDataService.showSkill(player);
        playerDataService.showPlayerInfo(player);
    }



    /**
     * 判断此生物是否可以使用某个技能
     * 主要判断三个方面：
     * 1.判断生物的mp是否足够
     * 2.判断生物所属角色类型是否具有当前技能
     * 3.判断生物上次释放此技能到现在CD是否冷却
     *
     * @param creature
     * @param skill
     * @return
     */
    public boolean canUseSkill(Creature creature, Skill skill) {
        if (null == skill) {
            if (creature instanceof PlayerBeCache) {
                notificationManager.notifyPlayer((PlayerBeCache) creature, "该技能不存在", RequestCode.BAD_REQUEST.getCode());
            }
            return false;
        }
        //只有玩家角色释放技能才需要魔法消耗
        if(creature instanceof PlayerBeCache){
            PlayerBeCache player = (PlayerBeCache) creature;
            if (player.getMp() < skill.getMpConsumption()) {
                notificationManager.notifyPlayer(player, "你的mp不足", RequestCode.BAD_REQUEST.getCode());
                return false;
            }
        }
        if (creature instanceof PlayerBeCache) {
            PlayerBeCache player = (PlayerBeCache) creature;
            if (Objects.isNull(roleTypeCache.getRoleType(player.getRoleClass()).getSkillMap().get(skill.getId()))) {
                notificationManager.notifyPlayer((PlayerBeCache) creature, "这个职业没有这个技能，使用 \" see_player_skill \"查看当前角色的技能与CD状态", RequestCode.BAD_REQUEST.getCode());
            }
        }
        if (!inCd(creature, skill.getId())) {
            if (creature instanceof PlayerBeCache) {
                notificationManager.notifyPlayer((PlayerBeCache) creature, "该技能正处于CD当中，使用\" see_player_skill \" 查看当前角色的技能与CD状态", RequestCode.BAD_REQUEST.getCode());
            }
            return false;
        }
        return true;
    }

    /**
     * 判断某生物的某个技能是否处于CD状态
     *
     * @param creature
     * @param skillId
     * @return true可用 false不可用
     */
    public boolean inCd(Creature creature, Integer skillId) {
        Skill skill = creature.getSkillHaveMap().get(skillId);
        boolean b = System.currentTimeMillis() - skill.getActiveTime() > skill.getCd();
        return b;
    }

    /**
     * 对生物使用技能
     * 执行的时候主要有三个步骤：
     * 1.前摇
     * 2.执行
     * 3.使其技能进入CD状态
     *
     * @param initiator 技能发起者
     * @param target    技能目标
     * @param skill     技能
     * @return 是否成功
     */
    public boolean castSkill(Creature initiator, Creature target, Scene scene, Skill skill) {
        if (!canUseSkill(initiator, skill)) {
            return false;
        }
        log.info("{}开始使用技能：{}", initiator.getName(), skill.getName());
        if (skill.getCastTime() != 0) {
            notificationManager.notifyCreature(initiator,
                    MessageFormat.format("开始施法，吟唱需要{0}秒", skill.getCastTime() / 1000), RequestCode.SUCCESS.getCode());
            // 开启技能冷却
            startSkillCd(initiator, skill);
            // 按吟唱时间延迟执行
            TimedTaskManager.singleThreadSchedule( skill.getCastTime(),
                    () -> scene.getSingleThreadSchedule().execute(
                            () -> {
                                notificationManager.notifyScene(scene,
                                        MessageFormat.format(" {0}  对 {1} 使用了技能  {2} ",
                                                initiator.getName(),target.getName(),skill.getName()), RequestCode.SUCCESS.getCode());
                                // 注意，这里的技能进行还是要用场景执行器执行，不然会导致多线程问题
                                skillEffect.castSkill(skill.getSkillInfluenceType(), initiator, target, scene, skill);
                            }
                    )
            );
        }else{
            notificationManager.notifyScene(scene,
                    MessageFormat.format(" {0}  对 {1} 使用了技能  {2} ",
                            initiator.getName(),target.getName(),skill.getName()),RequestCode.SUCCESS.getCode());
            skillEffect.castSkill(skill.getSkillInfluenceType(),initiator,target,scene,skill);
            // 开启技能冷却
            startSkillCd(initiator,skill);
        }
        return true;
    }

    /**
     * 使技能进入CD
     *
     * @param creature
     * @param skill
     */
    public void startSkillCd(Creature creature, Skill skill) {
        skill.setActiveTime(System.currentTimeMillis());
    }

    public Skill getSkillById(Integer skillId){
        return  SkillCache.get(skillId);
    }

    public boolean useSkillToMonster(ChannelHandlerContext context,Integer skillId, Long monsterUUId) {
        //获取技能、玩家、使用技能的场景
        PlayerBeCache player = playerLoginService.getPlayerByContext(context);
        Skill skill = player.getSkillHaveMap().get(skillId);
        Scene scene = sceneService.getScene(player.getNowAt());
        if (!canUseSkill(player, skill)) {
            notificationManager.notifyPlayer(player, "你现在不可以使用这个技能", RequestCode.NOT_SUPPORTED_OPERATION.getCode());
            return false;
        }
        //-------------------------------------------------------------------------------------------
        Monster monsterById = monsterCache.getMonsterByUUId(monsterUUId);
        castSkill(player,monsterById,scene,skill);
        return true;
    }


    public void useSkillCall(ChannelHandlerContext context, Integer petId, Long targetUuid) {
        PlayerBeCache playerByContext = playerLoginService.getPlayerByContext(context);
        //检验是否有召唤技能
       // roleTypeCache.getRoleType(playerByContext.getRoleClass()).getSkillMap().containsKey();


    }
}