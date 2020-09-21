package com.example.gameservicedemo.game.task.controller;

import com.example.commondemo.base.Command;
import com.example.commondemo.base.RequestCode;
import com.example.commondemo.message.Message;
import com.example.gameservicedemo.base.controller.ControllerManager;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.service.PlayerLoginService;
import com.example.gameservicedemo.game.task.service.TaskService;
import com.example.gameservicedemo.manager.NotificationManager;
import com.example.gameservicedemo.util.CheckParametersUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/21/15:59
 * @Description: 任务控制器
 */
@Component
@Slf4j
public class TaskController {
    @Autowired
    PlayerLoginService playerLoginService;
    @Autowired
    NotificationManager notificationManager;
    @Autowired
    TaskService taskService;


    {
        ControllerManager.add(Command.TASK_SHOW.getRequestCode(), this::taskShow);
        ControllerManager.add(Command.ACHIEVEMENT_SHOW.getRequestCode(), this::achievementShow);
        ControllerManager.add(Command.TASK_ALL.getRequestCode(), this::taskAll);
        ControllerManager.add(Command.TASK_ACCEPT.getRequestCode(), this::taskAccept);
        ControllerManager.add(Command.TASK_DESCRIBE.getRequestCode(), this::taskDescribe);
        ControllerManager.add(Command.TASK_GIVE_UP.getRequestCode(), this::taskGaveUp);
        ControllerManager.add(Command.TASK_FINISH.getRequestCode(), this::taskFinish);
    }

    private void taskFinish(ChannelHandlerContext context, Message message) {
        PlayerBeCache player = playerLoginService.isLoad(context);
        if(Objects.isNull(player)){
            notificationManager.notifyByCtx(context,"你还未登录", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        String[] parameter = CheckParametersUtil.checkParameter(context, message, 1);
        if (Objects.isNull(parameter)){
            notificationManager.notifyPlayer(player,"你还未登录",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        taskService.taskFinish(player,Long.valueOf(parameter[1]));

    }

    private void taskGaveUp(ChannelHandlerContext context, Message message) {

    }

    private void taskDescribe(ChannelHandlerContext context, Message message) {
    }

    private void taskAccept(ChannelHandlerContext context, Message message) {
        PlayerBeCache player = playerLoginService.isLoad(context);
        if(Objects.isNull(player)){
            notificationManager.notifyByCtx(context,"你还未登录", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //指令 任务id
        String[] parameter = CheckParametersUtil.checkParameter(context, message, 2);
        if (Objects.isNull(parameter)){
            notificationManager.notifyPlayer(player,"你还未登录",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        taskService.taskAccept(player,Integer.valueOf(parameter[1]));
    }

    private void taskAll(ChannelHandlerContext context, Message message) {
        PlayerBeCache player = playerLoginService.isLoad(context);
        if(Objects.isNull(player)){
            notificationManager.notifyByCtx(context,"你还未登录", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        String[] parameter = CheckParametersUtil.checkParameter(context, message, 1);
        if (Objects.isNull(parameter)){
            notificationManager.notifyPlayer(player,"你还未登录",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        taskService.taskAll(player);
    }

    private void achievementShow(ChannelHandlerContext context, Message message) {
    }

    private void taskShow(ChannelHandlerContext context, Message message) {
    }
}
