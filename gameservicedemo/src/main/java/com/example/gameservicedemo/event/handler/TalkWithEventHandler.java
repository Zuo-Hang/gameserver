package com.example.gameservicedemo.event.handler;

import com.example.gameservicedemo.event.EventBus;
import com.example.gameservicedemo.event.model.TalkWithEvent;
import com.example.gameservicedemo.game.task.bean.TaskType;
import com.example.gameservicedemo.game.task.service.TaskService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/25/16:37
 * @Description: 与NPC对话事件处理器
 */
@Component
public class TalkWithEventHandler {
    {
        EventBus.subscribe(TalkWithEvent.class,this::talkWithNPC);
    }
    @Resource
    private TaskService taskService;
    private  void talkWithNPC(TalkWithEvent event) {
        taskService.checkTaskProgressByNumber(TaskType.TALK_WITH,event.getPlayer(), event.getSceneObjectId(),null);
    }
}
