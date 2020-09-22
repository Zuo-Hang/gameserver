package com.example.gameservicedemo.event.handler;

import com.example.gameservicedemo.event.EventBus;
import com.example.gameservicedemo.event.model.TradeEvent;
import com.example.gameservicedemo.game.task.bean.TaskType;
import com.example.gameservicedemo.game.task.service.TaskService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/25/16:37
 * @Description: 交易事件处理器
 */
@Component
public class TradeEventHandler {
    {
        EventBus.subscribe(TradeEvent.class,this::firstTrade);
    }
    @Resource
    private TaskService taskService;

    private void firstTrade(TradeEvent tradeEvent) {
        // 分别检测交易发起者和交易被动者
        taskService.checkTaskProgressByNumber(TaskType.TRADE,tradeEvent.getInitiator(), 0,1);
        taskService.checkTaskProgressByNumber(TaskType.TRADE,tradeEvent.getAccepter(), 0,1);
    }
}
