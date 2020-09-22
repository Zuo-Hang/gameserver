package com.example.gameservicedemo.event.handler;

import com.example.gameservicedemo.event.EventBus;
import com.example.gameservicedemo.event.model.PKEvent;
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
 * @Description: 对战事件处理器
 */

@Component
@Slf4j
public class PKEventHandler {

    {
        EventBus.subscribe(PKEvent.class,this::firstPKWin);
    }


    @Resource
    private TaskService taskService;

    private  void firstPKWin(PKEvent event) {
        PlayerBeCache player = event.getPlayer();
        // 玩家pk胜利
        if (event.isWin()) {
            taskService.checkTaskProgressByNumber(TaskType.PK,player, 0,1);
        }
    }


}
