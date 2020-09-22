package com.example.gameservicedemo.event.handler;

import com.example.gameservicedemo.event.EventBus;
import com.example.gameservicedemo.event.model.GameCopyEvent;
import com.example.gameservicedemo.game.task.bean.TaskType;
import com.example.gameservicedemo.game.task.service.TaskService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/25/16:37
 * @Description: 副本事件处理器
 */

@Component
public class InstanceEventHandler {
    {
        EventBus.subscribe(GameCopyEvent.class,this::passInstance);
    }
    @Resource
    private TaskService taskService;
    private  void passInstance(GameCopyEvent gameCopyEvent) {
        taskService.checkTaskProgressByNumber(
                TaskType.GAME_COPY,
                gameCopyEvent.getPlayer(),
                gameCopyEvent.getGameCopyScene().getId(),
                1);
    }
}
