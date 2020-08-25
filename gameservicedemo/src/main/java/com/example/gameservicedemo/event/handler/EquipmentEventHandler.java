//package com.example.gameservicedemo.event.handler;
//
//import com.example.gameservicedemo.event.EventBus;
//import com.example.gameservicedemo.event.model.EquipmentEvent;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.util.Optional;
//
///**
// * @author gonefuture  gonefuture@qq.com
// * time 2019/1/3 16:16
// * @version 1.00
// * Description: 装备事件处理器
// */
//@Component
//public class EquipmentEventHandler {
//
//    {
//        EventBus.subscribe(EquipmentEvent.class,this::equipmentLevel);
//    }
//
//    @Resource
//    private QuestService questService;
//
//
//    private  void equipmentLevel(EquipmentEvent equipmentEvent) {
//
//        Optional<Integer> level = equipmentEvent.getPlayer().getEquipmentBar().values().stream()
//                .map(Item::getThingInfo)
//                .map(ThingInfo::getLevel)
//                .reduce(Integer::sum);
//
//        questService.checkMissionProgressByNumber(QuestType.EQUIPMENT,equipmentEvent.getPlayer(), QuestCondition.
//                FIRST_ACHIEVEMENT,level.orElse(0));
//    }
//
//
//
//}
