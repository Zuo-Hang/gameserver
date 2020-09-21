package com.example.gameservicedemo.game.task.service;

import com.example.commondemo.base.RequestCode;
import com.example.gameservicedemo.background.WriteBackDB;
import com.example.gameservicedemo.base.IdGenerator;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.task.bean.Progress;
import com.example.gameservicedemo.game.task.bean.Task;
import com.example.gameservicedemo.game.task.bean.TaskProgressBeCache;
import com.example.gameservicedemo.game.task.bean.TaskState;
import com.example.gameservicedemo.game.task.cache.TaskCache;
import com.example.gameservicedemo.manager.NotificationManager;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/21/15:59
 * @Description:
 */
@Service
@Slf4j
public class TaskService {
    @Autowired
    TaskCache taskCache;
    @Autowired
    WriteBackDB writeBackDB;
    @Autowired
    NotificationManager notificationManager;

    public void taskFinish(PlayerBeCache player, Long taskProgressId) {

    }

    /**
     * 查看系统所有的任务与成就
     *
     * @param player
     */
    public void taskAll(PlayerBeCache player) {
        StringBuilder sb = new StringBuilder();
        sb.append("所有的任务：\n");
        taskCache.allTask().values().stream().filter(mission -> mission.getType() != 4)
                .forEach(mission -> sb.append(MessageFormat.format("{0} {1} 等级：{2} 描述：{3}\n"
                        , mission.getName(), mission.getLevel(), mission.getDescribe())));
        sb.append("\n").append("所有的成就：\n");
        taskCache.allTask().values().stream().filter(mission -> mission.getType() == 4)
                .forEach(mission -> sb.append(MessageFormat.format("{0} {1} 等级：{2} 描述：{3}\n"
                        , mission.getName(), mission.getLevel(), mission.getDescribe())));
        notificationManager.notifyPlayer(player, sb, RequestCode.SUCCESS.getCode());
    }

    /**
     * 接受一项任务
     *
     * @param player
     * @param taskId
     */
    public void taskAccept(PlayerBeCache player, Integer taskId) {
        Task task = taskCache.getTaskById(taskId);
        if (Objects.isNull(task)) {
            notificationManager.notifyPlayer(player, "这项任务不存在，请检查输入的任务id", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        Long taskProgressId = player.getTask().get(taskId);
        TaskProgressBeCache taskProgress = taskCache.getTaskProgressById(taskProgressId);
        if (Objects.nonNull(taskProgressId) && taskProgress.getTaskState().equals(TaskState.RUNNING.getCode())) {
            notificationManager.notifyPlayer(player, "该任务正在进行中，使用''查看当前正在进行的任务", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        if (Objects.nonNull(taskProgressId) && taskProgress.getTaskState().equals(TaskState.NEVER.getCode())) {
            notificationManager.notifyPlayer(player, "该任务属于一次性任务并已经接收过了，不可重复接收", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //创建新的任务进度
        TaskProgressBeCache newTaskProgress = new TaskProgressBeCache(writeBackDB,
                IdGenerator.getAnId(),
                taskId,
                TaskState.RUNNING.getCode(),
                new Date());
        task.getConditionsMap().forEach((id, aim) -> {
            newTaskProgress.getProgressMap().put(id, new Progress(aim));
        });
        Gson gson = new Gson();
        newTaskProgress.setProgressJson(gson.toJson(newTaskProgress.getProgressMap()));
        //--------------------------------------------持久化进度
        notificationManager.notifyPlayer(player, "已经接收了这个进度，可以使用''查看当前正在进行的任务", RequestCode.SUCCESS.getCode());
    }
}
