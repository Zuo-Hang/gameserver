package com.example.gameservicedemo.event.handler;
import com.example.commondemo.base.RequestCode;
import com.example.gameservicedemo.event.EventBus;
import com.example.gameservicedemo.event.model.MoneyEvent;
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
 * @Description: 金币事件处理器
 */

@Component
public class MoneyEventHandler {
    {
        EventBus.subscribe(MoneyEvent.class, this::moneyNumber);
        EventBus.subscribe(MoneyEvent.class, this::moneyChange);
    }
    @Resource
    private TaskService taskService;
    @Resource
    private NotificationManager notificationManager;

    private  void moneyNumber(MoneyEvent moneyEvent) {
        taskService.checkTaskProgressByNumber(TaskType.MONEY,moneyEvent.getPlayer(),
                0,moneyEvent.getPlayer().getMoney());
    }
    /**
     *  金币变化监听
     * @param moneyEvent 金币事件
     */
    private void moneyChange(MoneyEvent moneyEvent) {
        if (moneyEvent.getMoney() > moneyEvent.getPlayer().getMoney()) {
            moneyEvent.getPlayer().setMoney(0);
            notificationManager.notifyPlayer(moneyEvent.getPlayer(),"你身上没钱了", RequestCode.WARNING.getCode());
        }
        if (moneyEvent.getMoney() > 0) {
            notificationManager.notifyPlayer(moneyEvent.getPlayer(),
                    MessageFormat.format("你的金币增加了{0}",moneyEvent.getMoney()),RequestCode.WARNING.getCode());
        }

        if (moneyEvent.getMoney() < 0) {
            notificationManager.notifyPlayer(moneyEvent.getPlayer(),
                    MessageFormat.format("你的金币减少了{0}",moneyEvent.getMoney()),RequestCode.WARNING.getCode());
        }
    }
}
