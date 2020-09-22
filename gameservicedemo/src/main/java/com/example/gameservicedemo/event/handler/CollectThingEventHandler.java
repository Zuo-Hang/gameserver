package com.example.gameservicedemo.event.handler;

import com.example.gameservicedemo.event.EventBus;
import com.example.gameservicedemo.event.model.CollectThingEvent;
import com.example.gameservicedemo.game.task.bean.TaskType;
import com.example.gameservicedemo.game.task.service.TaskService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

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
        EventBus.subscribe(CollectThingEvent.class,this::getEquipment);
        EventBus.subscribe(CollectThingEvent.class,this::getMissionThing);
    }


    @Resource
    private TaskService taskService;


    /**
     *  获取到任务物品
     * @param collectThingEvent 收集物品事件
     */
    private  void getMissionThing(CollectThingEvent collectThingEvent) {
        //---------------------------------------------------------------------------------
        Integer num=0;
        // 任务物品的获取加一
        taskService.checkTaskProgressByNumber(TaskType.COLLECT_TOOLS,collectThingEvent.getPlayer(),
                collectThingEvent.getThingInfo().getId(),num+1);
    }



    // 获取装备
    private  void getEquipment(CollectThingEvent collectThingEvent) {
//        // 筛选出装备，计算装备的等级事件
//        Optional<Integer> level = Optional.ofNullable(collectThingEvent.getThingInfo())
//                .filter(things -> things.getKind() == 1).map(ThingInfo::getLevel);
//        questService.checkMissionProgressByNumber(QuestType.COLLECT_THINGS,collectThingEvent.getPlayer(),
//                QuestCondition.FIRST_ACHIEVEMENT,level.orElse(0));
    }
}
