package com.example.gameservicedemo.game.skill.controller;

import com.example.commondemo.base.Command;
import com.example.commondemo.base.RequestCode;
import com.example.commondemo.message.Message;
import com.example.gameservicedemo.base.controller.ControllerManager;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.service.PlayerLoginService;
import com.example.gameservicedemo.game.scene.bean.Monster;
import com.example.gameservicedemo.game.scene.bean.Scene;
import com.example.gameservicedemo.game.scene.service.MonsterAiService;
import com.example.gameservicedemo.game.scene.service.SceneService;
import com.example.gameservicedemo.game.skill.service.SkillService;
import com.example.gameservicedemo.manager.NotificationManager;
import com.example.gameservicedemo.util.CheckParametersUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

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
        ControllerManager.add(Command.SKILL_TO_PVP.getRequestCode(),this::skillToPvP);
        ControllerManager.add(Command.SKILL_TO_GROUP.getRequestCode(),this::skillToGroup);
        ControllerManager.add(Command.SKILL_CALL.getRequestCode(),this::useSkillCall);
    }

    private void skillToGroup(ChannelHandlerContext context, Message message) {
        String[] strings = CheckParametersUtil.checkParameter(context, message, 3);
        PlayerBeCache player = playerLoginService.isLoad(context);
        if(Objects.isNull(player)){
            notificationManager.notifyByCtx(context,"还未加载化身！",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        if(Objects.isNull(strings)){
            notificationManager.notifyByCtx(context,"输入参数个数错误！",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        skillService.skillToGroup(player,Integer.valueOf(strings[1]),strings[2]);
    }

    private void skillToPvP(ChannelHandlerContext context, Message message) {
        //指令 技能 目标id
        String[] strings = CheckParametersUtil.checkParameter(context, message, 3);
        PlayerBeCache player = playerLoginService.isLoad(context);
        if(Objects.isNull(player)){
            notificationManager.notifyByCtx(context,"还未加载化身！",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        if(Objects.isNull(strings)){
            notificationManager.notifyByCtx(context,"输入参数个数错误！",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        skillService.skillToPvP(player, Integer.valueOf(strings[1]), Integer.valueOf(strings[2]));
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
        Monster monster = playerByContext.getSceneNowAt().getMonsters().get(monsterUuid);
        //这个100有问题

    }

    public void useSkillCall(ChannelHandlerContext context, Message message) {
        String[] strings = CheckParametersUtil.checkParameter(context, message, 2);
        PlayerBeCache player = playerLoginService.isLoad(context);
        if(Objects.isNull(player)){
            notificationManager.notifyByCtx(context,"还未加载化身！",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        if(Objects.isNull(strings)){
            notificationManager.notifyByCtx(context,"输入参数个数错误！",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        skillService.useSkillCall(player,Integer.valueOf(strings[1]));


    }
}
