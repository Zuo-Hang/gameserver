package com.example.gameservicedemo.event.handler;

import com.example.gameservicedemo.event.EventBus;
import com.example.gameservicedemo.event.model.GuildEvent;
import com.example.gameservicedemo.game.task.bean.TaskType;
import com.example.gameservicedemo.game.task.service.TaskService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/25/16:37
 * @Description: 公会事件处理器
 */

@Component
public class GuildEventHandler {
    {
        EventBus.subscribe(GuildEvent.class,this::joinGuild);
    }
    @Resource
    private TaskService taskService;
    private  void joinGuild(GuildEvent guildEvent) {
        taskService.checkTaskProgressByNumber(TaskType.GUILD,guildEvent.getPlayer(), 0,null);
    }
}
