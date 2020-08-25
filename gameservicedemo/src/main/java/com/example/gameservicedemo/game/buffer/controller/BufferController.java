package com.example.gameservicedemo.game.buffer.controller;

import com.example.commondemo.base.Command;
import com.example.commondemo.base.RequestCode;
import com.example.commondemo.message.Message;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.base.controller.ControllerManager;
import com.example.gameservicedemo.game.player.service.PlayerLoginService;
import com.example.gameservicedemo.manager.NotificationManager;
import com.example.gameservicedemo.game.buffer.service.BufferService;
import com.example.gameservicedemo.game.player.service.PlayerService;
import com.example.gameservicedemo.util.CheckParametersUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/17/17:50
 * @Description:
 */
@Component
@Slf4j
public class BufferController {
    @Autowired
    public BufferService bufferService;
    @Autowired
    public PlayerService playerService;
    @Autowired
    PlayerLoginService playerLoginService;
    @Autowired
    public NotificationManager notificationManager;

    {
        ControllerManager.add(Command.START_BUFFER.getRequestCode(), this::startBuffer);
    }


    //____________buf只应是使用了某项技能或装备后产生的效果，不应使用户直接使用__________________________真实情况下不应该有这个功能，只为测试使用
    /**
     *
     * @param context
     * @param message
     */
    public void startBuffer(ChannelHandlerContext context, Message message) {
        String[] strings = CheckParametersUtil.checkParameter(context, message, 2);
        Integer bufferId = Integer.valueOf(strings[1]);
        PlayerBeCache player =  playerLoginService.getPlayerByContext(context);
        boolean b = bufferService.startBuffer(player, bufferService.getBuffer(bufferId));
        if(!b){
            notificationManager.notifyPlayer(player,"不能使用这个buf", RequestCode.BAD_REQUEST.getCode());
        }
    }
}
