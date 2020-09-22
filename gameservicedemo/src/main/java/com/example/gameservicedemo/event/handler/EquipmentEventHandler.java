package com.example.gameservicedemo.event.handler;

import com.example.gameservicedemo.event.EventBus;
import com.example.gameservicedemo.event.model.EquipmentEvent;
import com.example.gameservicedemo.game.task.bean.Condition;
import com.example.gameservicedemo.game.task.bean.TaskType;
import com.example.gameservicedemo.game.task.service.TaskService;
import com.example.gameservicedemo.game.tools.bean.Tools;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Optional;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/25/16:37
 * @Description: 装备等级事件处理器
 */
@Component
public class EquipmentEventHandler {

    {
        //订阅事件
        EventBus.subscribe(EquipmentEvent.class, this::equipmentLevel);
    }

    @Resource
    private TaskService taskService;

    /**
     * 穿戴的装备等级总和达到XXX
     *
     * @param equipmentEvent
     */
    private void equipmentLevel(EquipmentEvent equipmentEvent) {
        Integer sum = 0;
        Collection<Tools> allTools = equipmentEvent.getPlayer().getEquipmentBar().values();
        for (Tools tools : allTools) {
            sum += tools.getLevel();
        }
        taskService.checkTaskProgressByNumber(TaskType.EQUIPMENT, equipmentEvent.getPlayer(), 0, sum);
    }


}
