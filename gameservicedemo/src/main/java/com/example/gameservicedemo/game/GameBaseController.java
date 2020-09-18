package com.example.gameservicedemo.game;

import com.example.commondemo.base.Command;
import com.example.commondemo.base.RequestCode;
import com.example.commondemo.message.Message;
import com.example.gameservicedemo.base.controller.ControllerManager;
import com.example.gameservicedemo.manager.NotificationManager;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/18/14:28
 * @Description: 处理未知命令
 */
@Component
@Slf4j
public class GameBaseController {
    @Autowired
    NotificationManager notificationManager;

    {
        ControllerManager.add(Command.UNKNOWN.getRequestCode(),this::unknow);
    }

    private void unknow(ChannelHandlerContext context, Message message) {
        notificationManager.notifyByCtx(context,"你输入的指令不正确，使用'help'查看所有指令", RequestCode.BAD_REQUEST.getCode());
    }
}
