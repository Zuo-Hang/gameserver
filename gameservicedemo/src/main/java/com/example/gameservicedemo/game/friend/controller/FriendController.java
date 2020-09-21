package com.example.gameservicedemo.game.friend.controller;

import com.example.commondemo.base.Command;
import com.example.commondemo.base.RequestCode;
import com.example.commondemo.message.Message;
import com.example.gamedatademo.bean.Player;
import com.example.gameservicedemo.base.controller.ControllerManager;
import com.example.gameservicedemo.game.friend.service.FriendService;
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
 * @Date: 2020/09/21/20:30
 * @Description: 朋友处理器
 */
@Component
@Slf4j
public class FriendController {
    @Autowired
    NotificationManager notificationManager;
    @Autowired
    PlayerLoginService playerLoginService;
    @Autowired
    FriendService friendService;
    {
        ControllerManager.add(Command.FRIEND_LIST.getRequestCode(),this::friendList);
        ControllerManager.add(Command.FRIEND_ADD.getRequestCode(),this::friendAdd);
        ControllerManager.add(Command.FRIEND_AGREE.getRequestCode(),this::friendAgree);
        ControllerManager.add(Command.FRIEND_DELETE.getRequestCode(),this::friendDelete);
        ControllerManager.add(Command.PLAYER_SEARCH.getRequestCode(),this::playerSearch);
    }

    private void playerSearch(ChannelHandlerContext context, Message message) {
        PlayerBeCache player = playerLoginService.isLoad(context);
        if(Objects.isNull(player)){
            notificationManager.notifyByCtx(context,"你还未登录", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //指令 用户id
        String[] parameters = CheckParametersUtil.checkParameter(context, message, 2);
        if(Objects.isNull(parameters)){
            notificationManager.notifyPlayer(player,"你输入的参数个数不对",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        friendService.playerSearch(player,Integer.valueOf(parameters[1]));
    }

    private void friendDelete(ChannelHandlerContext context, Message message) {
        PlayerBeCache player = playerLoginService.isLoad(context);
        if(Objects.isNull(player)){
            notificationManager.notifyByCtx(context,"你还未登录", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //指令 好友id
        String[] parameters = CheckParametersUtil.checkParameter(context, message, 2);
        if(Objects.isNull(parameters)){
            notificationManager.notifyPlayer(player,"你输入的参数个数不对",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        friendService.friendDelete(player,Integer.valueOf(parameters[1]));
    }

    private void friendAgree(ChannelHandlerContext context, Message message) {
        PlayerBeCache player = playerLoginService.isLoad(context);
        if(Objects.isNull(player)){
            notificationManager.notifyByCtx(context,"你还未登录", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //指令 请求id
        String[] parameters = CheckParametersUtil.checkParameter(context, message, 2);
        if(Objects.isNull(parameters)){
            notificationManager.notifyPlayer(player,"你输入的参数个数不对",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        friendService.friendAgree(player,Long.valueOf(parameters[1]));
    }

    private void friendAdd(ChannelHandlerContext context, Message message) {
        PlayerBeCache player = playerLoginService.isLoad(context);
        if(Objects.isNull(player)){
            notificationManager.notifyByCtx(context,"你还未登录", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //指令 用户id
        String[] parameters = CheckParametersUtil.checkParameter(context, message, 2);
        if(Objects.isNull(parameters)){
            notificationManager.notifyPlayer(player,"你输入的参数个数不对",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        friendService.friendAdd(player,Integer.valueOf(parameters[1]));
    }

    private void friendList(ChannelHandlerContext context, Message message) {
        PlayerBeCache player = playerLoginService.isLoad(context);
        if(Objects.isNull(player)){
            notificationManager.notifyByCtx(context,"你还未登录", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //指令
        String[] parameters = CheckParametersUtil.checkParameter(context, message, 1);
        if(Objects.isNull(parameters)){
            notificationManager.notifyPlayer(player,"你输入的参数个数不对",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        friendService.friendList(player);
    }
}
