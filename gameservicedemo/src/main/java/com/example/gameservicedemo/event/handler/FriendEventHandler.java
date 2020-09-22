package com.example.gameservicedemo.event.handler;

import com.example.gameservicedemo.event.EventBus;
import com.example.gameservicedemo.event.model.FriendEvent;
import com.example.gameservicedemo.game.task.bean.TaskType;
import com.example.gameservicedemo.game.task.service.TaskService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/25/16:37
 * @Description: 好友事件处理器
 */
@Component
public class FriendEventHandler {
    {
        EventBus.subscribe(FriendEvent.class,this::addFriend);
    }
    @Resource
    private TaskService taskService;

    private  void addFriend(FriendEvent guildEvent) {
        taskService.checkTaskProgressByNumber(TaskType.FRIEND,guildEvent.getPlayer(), 0,1);
    }
}
