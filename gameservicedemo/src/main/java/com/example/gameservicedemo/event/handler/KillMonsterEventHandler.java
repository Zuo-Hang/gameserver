//package com.example.gameservicedemo.event.handler;
//
//import com.example.gameservicedemo.event.EventBus;
//import com.example.gameservicedemo.event.model.MonsterEventDeadEvent;
//import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//
//
///**
// * @author gonefuture  gonefuture@qq.com
// * time 2018/12/27 18:28
// * @version 1.00
// * Description: mmorpg
// */
//
//
//@Component
//@Slf4j
//public class KillMonsterEventHandler {
//
//    {
//        EventBus.subscribe(MonsterEventDeadEvent.class,this::killMonsterNumber);
//    }
//
//
////    @Resource
////    private QuestManager missionManager;
//
//    @Resource
//    private QuestService questService;
//
//
//    /**
//     *  按杀死怪物标记任务进度的任务
//     * @param deadEvent  怪物死亡事件
//     */
//    private void killMonsterNumber(MonsterEventDeadEvent deadEvent) {
//        Long monsterId = deadEvent.getTarget().getId();
//        PlayerBeCache player = deadEvent.getPlayer();
//
//        questService.checkMissionProgress(QuestType.KILL_MONSTER,player,String.valueOf(monsterId));
//
//    }
//
//
//
//
//
//
//
//
//
//
//}
