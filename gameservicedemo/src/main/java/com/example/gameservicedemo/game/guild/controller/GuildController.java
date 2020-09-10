package com.example.gameservicedemo.game.guild.controller;

import com.example.commondemo.base.Command;
import com.example.commondemo.base.RequestCode;
import com.example.commondemo.message.Message;
import com.example.gameservicedemo.base.controller.ControllerManager;
import com.example.gameservicedemo.game.guild.service.GuildService;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.service.PlayerLoginService;
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
 * @Date: 2020/09/09/17:06
 * @Description:
 */
@Slf4j
@Component
public class GuildController {
    @Autowired
    NotificationManager notificationManager;
    @Autowired
    PlayerLoginService playerLoginService;
    @Autowired
    GuildService guildService;

    {
        ControllerManager.add(Command.GUILD_CREATE.getRequestCode(),this::guildCreate);
        ControllerManager.add(Command.GUILD_SHOW.getRequestCode(),this::guildShow);
        ControllerManager.add(Command.GUILD_JOIN.getRequestCode(),this::guildJoin);
        ControllerManager.add(Command.GUILD_PERMIT.getRequestCode(),this::guildPermit);
        ControllerManager.add(Command.GUILD_DONATE.getRequestCode(),this::guildDonate);
        ControllerManager.add(Command.GUILD_TAKE.getRequestCode(),this::guildTake);

        ControllerManager.add(Command.GUILD_TAKE_COLD.getRequestCode(),this::guildTakeCole);
        ControllerManager.add(Command.GUILD_GRANT.getRequestCode(),this::guildGrant);
        ControllerManager.add(Command.GUILD_QUIT.getRequestCode(),this::guildQuit);
        ControllerManager.add(Command.GUILD_SHOW_REQ.getRequestCode(),this::guildShowReq);
        ControllerManager.add(Command.GUILD_DONATE_COLD.getRequestCode(),this::guildDonateCold);
        ControllerManager.add(Command.GUILD_DISMISS.getRequestCode(),this::guildDismiss);
        ControllerManager.add(Command.GUILD_KICK.getRequestCode(),this::guildKick);
    }

    private void guildTakeCole(ChannelHandlerContext context, Message message) {
        PlayerBeCache load = playerLoginService.isLoad(context);
        if(Objects.isNull(load)){
            notificationManager.notifyByCtx(context,"你还未加载游戏角色！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //指令 被踢玩家id
        String[] strings = CheckParametersUtil.checkParameter(context, message, 1);
        if(Objects.isNull(strings)){
            notificationManager.notifyByCtx(context,"输入的参数个数错误！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        guildService.guildTakeCole(load);
    }

    private void guildKick(ChannelHandlerContext context, Message message) {
        PlayerBeCache load = playerLoginService.isLoad(context);
        if(Objects.isNull(load)){
            notificationManager.notifyByCtx(context,"你还未加载游戏角色！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //指令 被踢玩家id
        String[] strings = CheckParametersUtil.checkParameter(context, message, 2);
        if(Objects.isNull(strings)){
            notificationManager.notifyByCtx(context,"输入的参数个数错误！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        guildService.guildKick(load,Integer.valueOf(strings[1]));
    }

    private void guildDismiss(ChannelHandlerContext context, Message message) {
        PlayerBeCache load = playerLoginService.isLoad(context);
        if(Objects.isNull(load)){
            notificationManager.notifyByCtx(context,"你还未加载游戏角色！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //指令 申请人id
        String[] strings = CheckParametersUtil.checkParameter(context, message, 2);
        if(Objects.isNull(strings)){
            notificationManager.notifyByCtx(context,"输入的参数个数错误！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        guildService.guildDismiss(load);
    }

    private void guildDonateCold(ChannelHandlerContext context, Message message) {
        PlayerBeCache load = playerLoginService.isLoad(context);
        if(Objects.isNull(load)){
            notificationManager.notifyByCtx(context,"你还未加载游戏角色！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //指令 申请人id
        String[] strings = CheckParametersUtil.checkParameter(context, message, 2);
        if(Objects.isNull(strings)){
            notificationManager.notifyByCtx(context,"输入的参数个数错误！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        guildService.guildDonateCold(load,Integer.valueOf(strings[1]));
    }

    private void guildShowReq(ChannelHandlerContext context, Message message) {
        PlayerBeCache load = playerLoginService.isLoad(context);
        if(Objects.isNull(load)){
            notificationManager.notifyByCtx(context,"你还未加载游戏角色！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //指令 申请人id
        String[] strings = CheckParametersUtil.checkParameter(context, message, 1);
        if(Objects.isNull(strings)){
            notificationManager.notifyByCtx(context,"输入的参数个数错误！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        guildService.guildShowReq(load);
    }

    private void guildQuit(ChannelHandlerContext context, Message message) {
        PlayerBeCache load = playerLoginService.isLoad(context);
        if(Objects.isNull(load)){
            notificationManager.notifyByCtx(context,"你还未加载游戏角色！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //指令 申请人id
        String[] strings = CheckParametersUtil.checkParameter(context, message, 1);
        if(Objects.isNull(strings)){
            notificationManager.notifyByCtx(context,"输入的参数个数错误！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        guildService.guildQuit(load);
    }

    private void guildGrant(ChannelHandlerContext context, Message message) {
        PlayerBeCache load = playerLoginService.isLoad(context);
        if(Objects.isNull(load)){
            notificationManager.notifyByCtx(context,"你还未加载游戏角色！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //指令 被授权人id  权力等级
        String[] strings = CheckParametersUtil.checkParameter(context, message, 3);
        if(Objects.isNull(strings)){
            notificationManager.notifyByCtx(context,"输入的参数个数错误！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        guildService.guildGrant(load,Integer.valueOf(strings[1]),Integer.valueOf(strings[2]));
    }

    private void guildTake(ChannelHandlerContext context, Message message) {

    }

    private void guildDonate(ChannelHandlerContext context, Message message) {
        PlayerBeCache load = playerLoginService.isLoad(context);
        if(Objects.isNull(load)){
            notificationManager.notifyByCtx(context,"你还未加载游戏角色！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //指令 申请人id
        String[] strings = CheckParametersUtil.checkParameter(context, message, 2);
        if(Objects.isNull(strings)){
            notificationManager.notifyByCtx(context,"输入的参数个数错误！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        guildService.guildDonate(load,Long.valueOf(strings[1]));
    }

    private void guildPermit(ChannelHandlerContext context, Message message) {
        PlayerBeCache load = playerLoginService.isLoad(context);
        if(Objects.isNull(load)){
            notificationManager.notifyByCtx(context,"你还未加载游戏角色！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //指令 申请人id
        String[] strings = CheckParametersUtil.checkParameter(context, message, 2);
        if(Objects.isNull(strings)){
            notificationManager.notifyByCtx(context,"输入的参数个数错误！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        guildService.guildPermit(load,Integer.valueOf(strings[1]));
    }

    private void guildJoin(ChannelHandlerContext context, Message message) {
        PlayerBeCache load = playerLoginService.isLoad(context);
        if(Objects.isNull(load)){
            notificationManager.notifyByCtx(context,"你还未加载游戏角色！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //指令 公会id
        String[] strings = CheckParametersUtil.checkParameter(context, message, 2);
        if(Objects.isNull(strings)){
            notificationManager.notifyByCtx(context,"输入的参数个数错误！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        guildService.guildJoin(load,Long.valueOf(strings[1]));
    }

    private void guildShow(ChannelHandlerContext context, Message message) {
        PlayerBeCache load = playerLoginService.isLoad(context);
        if(Objects.isNull(load)){
            notificationManager.notifyByCtx(context,"你还未加载游戏角色！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //指令 公会名称
        String[] strings = CheckParametersUtil.checkParameter(context, message, 1);
        if(Objects.isNull(strings)){
            notificationManager.notifyByCtx(context,"输入的参数个数错误！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        guildService.guildShow(load);
    }

    private void guildCreate(ChannelHandlerContext context, Message message) {
        PlayerBeCache load = playerLoginService.isLoad(context);
        if(Objects.isNull(load)){
            notificationManager.notifyByCtx(context,"你还未加载游戏角色！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //指令 公会名称
        String[] strings = CheckParametersUtil.checkParameter(context, message, 2);
        if(Objects.isNull(strings)){
            notificationManager.notifyByCtx(context,"输入的参数个数错误！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        guildService.guildCreat(load,strings[1]);

    }

}
