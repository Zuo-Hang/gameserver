package com.example.gameservicedemo.event.handler;

import com.example.commondemo.base.RequestCode;
import com.example.gameservicedemo.event.EventBus;
import com.example.gameservicedemo.event.model.LevelEvent;
import com.example.gameservicedemo.game.task.bean.TaskType;
import com.example.gameservicedemo.game.task.service.TaskService;
import com.example.gameservicedemo.manager.NotificationManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.MessageFormat;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/25/16:37
 * @Description: 等级事件处理器
 */

@Component
public class LevelEventHandler {
    {
        EventBus.subscribe(LevelEvent.class, this::levelUp);
    }
    @Resource
    private TaskService taskService;
    @Resource
    private NotificationManager notificationManager;

    private  void levelUp(LevelEvent levelEvent) {
        notificationManager.notifyPlayer(levelEvent.getPlayer(), MessageFormat.format("恭喜你，您的等级升到了{0}级",
                levelEvent.getLevel()), RequestCode.WARNING.getCode());
        taskService.checkTaskProgressByNumber(TaskType.LEVEL,levelEvent.getPlayer(),
                0,levelEvent.getLevel());
    }
}
