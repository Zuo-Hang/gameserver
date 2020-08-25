package com.example.gameservicedemo.game.skill.controller;

import com.example.commondemo.base.Command;
import com.example.commondemo.base.RequestCode;
import com.example.commondemo.message.Message;
import com.example.gameservicedemo.base.controller.ControllerManager;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.service.PlayerLoginService;
import com.example.gameservicedemo.game.scene.bean.Monster;
import com.example.gameservicedemo.game.scene.bean.Scene;
import com.example.gameservicedemo.game.scene.cache.MonsterCache;
import com.example.gameservicedemo.game.scene.service.MonsterAiService;
import com.example.gameservicedemo.game.scene.service.SceneService;
import com.example.gameservicedemo.game.skill.service.SkillService;
import com.example.gameservicedemo.manager.NotificationManager;
import com.example.gameservicedemo.util.CheckParametersUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/13/15:38
 * @Description: 技能控制器
 */
@Component
@Slf4j
public class SkillController {
    @Autowired
    MonsterCache monsterCache;
    @Autowired
    PlayerLoginService playerLoginService;
    @Autowired
    MonsterAiService monsterAiService;
    @Autowired
    SceneService sceneService;
    @Autowired
    NotificationManager notificationManager;
    @Autowired
    SkillService skillService;


    {
        ControllerManager.add(Command.SKILL_TO_SELF.getRequestCode(), this::useSkillToSelf);
        ControllerManager.add(Command.SKILL_TO_MONSTER.getRequestCode(), this::useSkillToMonster);
    }

    /**
     * 使用技能
     *
     * @param context
     * @param message
     */
    public void useSkillToSelf(ChannelHandlerContext context, Message message) {
        //命令名称 技能id
        String[] strings = CheckParametersUtil.checkParameter(context, message, 2);
        Integer skillId = Integer.valueOf(strings[1]);
        //---------------------------------判断玩家是否加载
        skillService.useSkillToSelf(context, skillId);
    }

    public void useSkillToMonster(ChannelHandlerContext context, Message message) {
        //命令 技能 怪物id
        String[] strings = CheckParametersUtil.checkParameter(context, message, 3);
        Integer skillId = Integer.valueOf(strings[1]);
        Long monsterUuid = Long.valueOf(strings[2]);
        //判断角色是否加载
        PlayerBeCache playerByContext = playerLoginService.getPlayerByContext(context);
        Scene scene = sceneService.getScene(playerByContext.getNowAt());
        boolean b = skillService.useSkillToMonster(context, skillId, monsterUuid);
        if (!b) {
            notificationManager.notifyPlayer(playerByContext, "攻击失败", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        Monster monsterById = monsterCache.getMonsterByUUId(monsterUuid);
        monsterAiService.notifyMonsterBeAttack(playerByContext, monsterById, scene, 100);
        monsterById.setTarget(playerByContext);
        monsterAiService.startAI(monsterById, scene);
    }

public void useSkillCall(ChannelHandlerContext context,Message message){
        //判断是否加载用户
        //命令 召唤兽 攻击目标
    String[] strings = CheckParametersUtil.checkParameter(context, message, 3);
    Integer petId = Integer.valueOf(strings[1]);
    Long targetUuid = Long.valueOf(strings[2]);
    skillService.useSkillCall(context,petId,targetUuid);



}
}
