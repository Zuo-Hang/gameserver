package com.example.gameservicedemo.event.handler;

import com.example.gameservicedemo.event.EventBus;
import com.example.gameservicedemo.event.model.TaskEvent;
import com.example.gameservicedemo.game.task.bean.TaskProgressBeCache;
import com.example.gameservicedemo.manager.NotificationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.MessageFormat;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/25/16:37
 * @Description: 任务事件处理器
 */

@Component
@Slf4j
public class MissionHandler {

    {
        EventBus.subscribe(TaskEvent.class,this::notificationMission);
    }
    @Resource
    private NotificationManager notificationManager;
    /**
     *  通知任务
     * @param taskEvent   任务事件
     */
    private  void notificationMission(TaskEvent taskEvent) {
//        TaskProgressBeCache taskProgressBeCache = taskEvent.getTaskProgressBeCache();
//       if (QuestState.COMPLETE.getCode().equals(missionProgress.getQuestState())) {
//           notificationManager.notifyPlayer(missionEvent.getPlayer(), MessageFormat.format("任务{0}  达成 \n" +
//                           "{1}",
//                   missionEvent.getQuest().getName(),missionEvent.getQuest().getDescribe()));
//       }
//
//        if (QuestState.FINISH.getCode().equals(missionProgress.getQuestState())) {
//            notificationManager.notifyPlayer(missionEvent.getPlayer(),MessageFormat.format("完成任务{0}",
//                    missionEvent.getQuest().getName()));
//        }
//
//        if (QuestState.NEVER.getCode().equals(missionProgress.getQuestState())) {
//            notificationManager.notifyPlayer(missionEvent.getPlayer(), MessageFormat.format("成就 {0}  达成 \n" +
//                            "{1}",
//                    missionEvent.getQuest().getName(),missionEvent.getQuest().getDescribe()));
//        }


    }


}
