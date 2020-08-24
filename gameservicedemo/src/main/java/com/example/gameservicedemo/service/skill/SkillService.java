package com.example.gameservicedemo.service.skill;

import com.example.commondemo.base.RequestCode;
import com.example.gameservicedemo.bean.Creature;
import com.example.gameservicedemo.bean.PlayerBeCache;
import com.example.gameservicedemo.bean.skill.Skill;
import com.example.gameservicedemo.bean.scene.Scene;
import com.example.gameservicedemo.cache.RoleTypeCache;
import com.example.gameservicedemo.cache.SkillCache;
import com.example.gameservicedemo.manager.NotificationManager;
import com.example.gameservicedemo.manager.TimedTaskManager;
import com.example.gameservicedemo.service.PlayerService;
import com.example.gameservicedemo.service.SceneService;
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
    SceneService sceneService;
    @Autowired
    RoleTypeCache roleTypeCache;
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
        PlayerBeCache player = playerService.getPlayerByContext(context);
        Scene scene = sceneService.getScene(player.getNowAt());
        if (!canUseSkill(player, skill)) {
            notificationManager.notifyPlayer(player, "你现在不可以使用这个技能", RequestCode.NOT_SUPPORTED_OPERATION.getCode());
            return ;
        }
        castSkill(player,player,scene,skill);
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
        if (creature.getMp() < skill.getMpConsumption()) {
            if (creature instanceof PlayerBeCache) {
                notificationManager.notifyPlayer((PlayerBeCache) creature, "你的mp不足", RequestCode.BAD_REQUEST.getCode());
            }
            return false;
        }
        if (creature instanceof PlayerBeCache) {
            PlayerBeCache player = (PlayerBeCache) creature;
            if (Objects.isNull(roleTypeCache.getRoleType(player.getRoleClass()).getSkillMap().get(skill.getId()))) {
                notificationManager.notifyPlayer((PlayerBeCache) creature, "这个职业没有这个技能，使用 \" see_player_skill \"查看当前角色的技能与CD状态", RequestCode.BAD_REQUEST.getCode());
            }
        }
        if (inCD(creature, skill.getId())) {
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
     * @return 为null表示可用, 不为null表示正在cd
     */
    public boolean inCD(Creature creature, Integer skillId) {
        return !Objects.isNull(creature.getHasUseSkillMap().get(skillId));
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
                                notificationManager.notifyScene(scene.getId(),
                                        MessageFormat.format(" {0}  对 {1} 使用了技能  {2} ",
                                                initiator.getName(),target.getName(),skill.getName()), RequestCode.SUCCESS.getCode());
                                // 注意，这里的技能进行还是要用场景执行器执行，不然会导致多线程问题
                                skillEffect.castSkill(skill.getSkillInfluenceType(), initiator, target, scene, skill);
                            }
                    )
            );
        }else{
            notificationManager.notifyScene(scene.getId(),
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
        //创建一个新的对象来存入map
        Skill skillWillCD = new Skill();
        BeanUtils.copyProperties(skill,skillWillCD);
//        //设置id用于判断是否在map中
//        skillWillCD.setId(skill.getId());
//        skillWillCD.setName(skill.getName());
//        //用于给用户显示还需cd多久
//        skillWillCD.setCd(skill.getCd());
//        skillWillCD.setLevel(skill.getLevel());
//        skillWillCD.setMpConsumption(skill.getMpConsumption());
//        skillWillCD.setCastTime(skill.getCastTime());
        //设置上次使用技能的时间
        skillWillCD.setActiveTime(System.currentTimeMillis());
        creature.getHasUseSkillMap().put(skill.getId(), skillWillCD);
        //定时器——>移除CD完成的技能
        TimedTaskManager.schedule(skill.getCd(), () -> creature.getHasUseSkillMap().remove(skill.getId()));
    }

    public Skill getSkillById(Integer skillId){
        return  SkillCache.get(skillId);
    }
}
