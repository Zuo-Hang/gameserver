package com.example.gameservicedemo.game.chat.controller;

import com.example.commondemo.base.Command;
import com.example.commondemo.message.Message;
import com.example.gameservicedemo.base.controller.ControllerManager;
import com.example.gameservicedemo.game.chat.service.ChatService;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.service.PlayerLoginService;
import com.example.gameservicedemo.util.CheckParametersUtil;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/01/17:13
 * @Description: 聊天控制器
 */
@Component
public class ChatController {
    @Autowired
    PlayerLoginService playerLoginService;
    @Autowired
    ChatService chatService;

    {
        ControllerManager.add(Command.WHISPER.getRequestCode(),this::whisper);
        ControllerManager.add(Command.PUBLIC_CHAT.getRequestCode(),this::publicChat);
    }

    private void publicChat(ChannelHandlerContext context, Message message) {
        String[] strings = CheckParametersUtil.checkParameter(context, message, 2);
        if(Objects.isNull(strings)){
            return;
        }
        PlayerBeCache load = playerLoginService.isLoad(context);
        if(Objects.isNull(load)){
            return;
        }
        chatService.publicChat(load,strings[1]);

    }

    private void whisper(ChannelHandlerContext context, Message message) {
        String[] strings = CheckParametersUtil.checkParameter(context, message, 3);
        if(Objects.isNull(strings)){
            return;
        }
        PlayerBeCache load = playerLoginService.isLoad(context);
        if(Objects.isNull(load)){
            return;
        }
        chatService.whisper(load,Integer.valueOf(strings[1]),strings[2]);
    }
}
