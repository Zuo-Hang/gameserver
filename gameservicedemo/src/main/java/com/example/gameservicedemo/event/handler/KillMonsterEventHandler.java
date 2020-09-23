package com.example.gameservicedemo.event.handler;

import com.example.gameservicedemo.event.EventBus;
import com.example.gameservicedemo.event.model.MonsterEventDeadEvent;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.task.bean.TaskType;
import com.example.gameservicedemo.game.task.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/25/16:37
 * @Description: 击杀怪物事件处理器
 */


@Component
@Slf4j
public class KillMonsterEventHandler {
    {
        EventBus.subscribe(MonsterEventDeadEvent.class,this::killMonsterNumber);
    }
    @Resource
    private TaskService taskService;
    /**
     *  按杀死怪物标记任务进度的任务
     * @param deadEvent  怪物死亡事件
     */
    private void killMonsterNumber(MonsterEventDeadEvent deadEvent) {
        Integer id = deadEvent.getTarget().getId();
        PlayerBeCache player = deadEvent.getPlayer();
        taskService.checkTaskProgressByNumber(TaskType.KILL_MONSTER,player, id,null);
    }
}
