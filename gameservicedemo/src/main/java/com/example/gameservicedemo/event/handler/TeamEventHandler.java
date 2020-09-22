package com.example.gameservicedemo.event.handler;

import com.example.gameservicedemo.event.EventBus;
import com.example.gameservicedemo.event.model.TeamEvent;
import com.example.gameservicedemo.game.task.bean.TaskType;
import com.example.gameservicedemo.game.task.service.TaskService;
import com.example.gameservicedemo.manager.NotificationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/25/16:37
 * @Description: 组队事件处理器
 */
@Component
@Slf4j
public class TeamEventHandler {
    {
        EventBus.subscribe(TeamEvent.class,this::firstTeam);
    }
    @Resource
    private NotificationManager notificationManager;
    @Resource
    private TaskService taskService;
    private  void firstTeam(TeamEvent teamEvent) {
        // 检测队伍是否是第一次组队
        teamEvent.getTeammate().forEach(
                p -> taskService.checkTaskProgressByNumber(TaskType.TEAM,p, 0,1)
        );
    }
}
