package com.example.gameservicedemo.event.handler;

import com.example.gameservicedemo.event.EventBus;
import com.example.gameservicedemo.event.model.CollectThingEvent;
import com.example.gameservicedemo.game.bag.bean.BagBeCache;
import com.example.gameservicedemo.game.task.bean.TaskType;
import com.example.gameservicedemo.game.task.service.TaskService;
import com.example.gameservicedemo.game.tools.bean.Tools;
import com.example.gameservicedemo.game.tools.bean.ToolsLevel;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/25/16:37
 * @Description: 收集物品事件处理器
 */

@Component
public class CollectThingEventHandler {
    {
        EventBus.subscribe(CollectThingEvent.class,this::getMissionThing);
    }


    @Resource
    private TaskService taskService;


    /**
     *  获取到任务物品
     * @param collectThingEvent 收集物品事件
     */
    private  void getMissionThing(CollectThingEvent collectThingEvent) {
        //计算背包中有几件顶级装备
        Integer num=0;
        BagBeCache bagBeCache = collectThingEvent.getPlayer().getBagBeCache();
        for (Tools tools : bagBeCache.getToolsMap().values()) {
            if (tools.getLevel().equals(ToolsLevel.TOP.getCode())) {
                num++;
            }
        }
        //调用任务进度检测器
        taskService.checkTaskProgressByNumber(TaskType.COLLECT_TOOLS,collectThingEvent.getPlayer(),
                0,num);
    }
}
