package com.example.gameservicedemo.game.task.service;

import com.example.commondemo.base.RequestCode;
import com.example.gameservicedemo.background.WriteBackDB;
import com.example.gameservicedemo.base.IdGenerator;
import com.example.gameservicedemo.game.bag.service.BagService;
import com.example.gameservicedemo.game.mail.bean.GameSystem;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.service.PlayerDataService;
import com.example.gameservicedemo.game.task.bean.*;
import com.example.gameservicedemo.game.task.cache.TaskCache;
import com.example.gameservicedemo.game.tools.bean.Tools;
import com.example.gameservicedemo.game.tools.service.ToolsService;
import com.example.gameservicedemo.manager.NotificationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Stream;

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
    ToolsService toolsService;
    @Autowired
    GameSystem gameSystem;
    @Autowired
    PlayerDataService playerDataService;
    @Autowired
    BagService bagService;
    @Autowired
    WriteBackDB writeBackDB;
    @Autowired
    NotificationManager notificationManager;

    /**
     * 查看系统所有的任务与成就
     *
     * @param player
     */
    public void taskAll(PlayerBeCache player) {
        StringBuilder sb = new StringBuilder();
        sb.append("所有的任务：\n");
        taskCache.allTask().values().stream().filter(mission -> mission.getKind().equals(TaskKindType.NOVICE.getCode()))
                .forEach(mission -> sb.append(MessageFormat.format("id:{0}  name:{1}  等级：{2}  描述：{3}\n",
                        mission.getId(), mission.getName(), mission.getLevel(), mission.getDescribe())));
        sb.append("\n").append("所有的成就：\n");
        taskCache.allTask().values().stream().filter(mission -> mission.getKind().equals(TaskKindType.ACHIEVEMENT.getCode()))
                .forEach(mission -> sb.append(MessageFormat.format("id:{0}  name:{1}  等级：{2}  描述：{3}\n",
                        mission.getId(), mission.getName(), mission.getLevel(), mission.getDescribe())));
        notificationManager.notifyPlayer(player, sb, RequestCode.SUCCESS.getCode());
    }

    /**
     * 接受一项任务
     *
     * @param player
     * @param taskId
     */
    public Long taskAccept(PlayerBeCache player, Integer taskId) {
        Task task = taskCache.getTaskById(taskId);
        if (Objects.isNull(task)) {
            notificationManager.notifyPlayer(player, "这项任务不存在，请检查输入的任务id", RequestCode.BAD_REQUEST.getCode());
            return null;
        }
        Long taskProgressId = player.getTaskProgressMap().get(taskId);
        //存在即正在进行中
        if (Objects.nonNull(taskProgressId)) {
            notificationManager.notifyPlayer(player, "该任务正在进行中，使用'task_show'查看当前正在进行的任务", RequestCode.BAD_REQUEST.getCode());
            return null;
        }
        for (Long id : player.getTaskAcquireList()) {
            TaskProgressBeCache taskProgressById = taskCache.getTaskProgressById(id);
            Integer kind = taskCache.getTaskById(taskProgressById.getTaskId()).getKind();
            if (kind.equals(TaskKindType.ACHIEVEMENT.getCode()) || kind.equals(TaskKindType.NOVICE.getCode())) {
                notificationManager.notifyPlayer(player, "该任务属于一次性任务并已经接收过了，不可重复接收", RequestCode.BAD_REQUEST.getCode());
                return null;
            }
        }
        //创建新的任务进度
        TaskProgressBeCache newTaskProgress = new TaskProgressBeCache(writeBackDB,
                IdGenerator.getAnId(),
                taskId,
                TaskState.RUNNING.getCode(),
                new Date(),
                0);
        player.addTaskProgressMap(newTaskProgress);
        taskCache.putInProgress(newTaskProgress);
        notificationManager.notifyPlayer(player, "已经接收了这个进度，可以使用'task_show'查看当前正在进行的任务", RequestCode.SUCCESS.getCode());
        return newTaskProgress.getId();
    }

    /**
     * 任务详情
     *
     * @param player
     * @param taskId
     */
    public void taskDescribe(PlayerBeCache player, Integer taskId) {
        Task task = taskCache.getTaskById(taskId);
        if (Objects.isNull(task)) {
            notificationManager.notifyPlayer(player, "该任务不存在，请检查输入的taskId", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //所奖励的装备
        StringBuilder tools = new StringBuilder();
        task.getRewardToolsMap().forEach((k, v) -> {
            Tools toolsById = toolsService.getToolsById(k);
            tools.append(MessageFormat.format("{0}*{1}  ", toolsById.getName(), v));
        });
        String format = MessageFormat.format("id:{0}\nname:{1}\n任务详情:{2}\n奖励:  金币:{3}  物品:{4}\n",
                task.getId(),
                task.getName(),
                task.getDescribe(),
                task.getRewardMoney(),
                tools
        );
        notificationManager.notifyPlayer(player, format, RequestCode.SUCCESS.getCode());
    }

    /**
     * 展示当前正在进行的任务
     *
     * @param player
     */
    public void taskShow(PlayerBeCache player) {
        StringBuilder string = new StringBuilder("待完成的任务如下：\n");
        if (player.getTaskProgressMap().isEmpty()) {
            notificationManager.notifyPlayer(player, "你目前还没有正在进行的任务！使用'accept_task'添加新的任务！", RequestCode.WARNING.getCode());
            return;
        }
        player.getTaskProgressMap().forEach((taskId, progressId) -> {
            Task task = taskCache.getTaskById(taskId);
            TaskProgressBeCache taskProgress = taskCache.getTaskProgressById(progressId);
            string.append(MessageFormat.format("taskId:{0}  taskName:{1}  起始时间:{2}  当前进度:{3}/{4}\n",
                    task.getId(),
                    task.getName(),
                    taskProgress.getBeginTime(),
                    taskProgress.getNowAt(),
                    task.getTaskCondition().getAim()));
        });
        notificationManager.notifyPlayer(player, string, RequestCode.SUCCESS.getCode());
    }

    /**
     * 展示已经取得的成就
     *
     * @param player
     */
    public void achievementShow(PlayerBeCache player) {
        StringBuilder string = new StringBuilder("所完成的任务如下：\n");
        if (player.getTaskAcquireList().isEmpty()) {
            notificationManager.notifyPlayer(player, "你还没有已经完成的任务！使用'task_show'查看当前正在进行的任务", RequestCode.WARNING.getCode());
            return;
        }
        player.getTaskAcquireList().forEach(id -> {
            TaskProgressBeCache taskProgress = taskCache.getTaskProgressById(id);
            Task task = taskCache.getTaskById(taskProgress.getTaskId());
            string.append(MessageFormat.format("taskId:{0}  taskName:{1}  起始时间:{2}  结束时间:{3}\n",
                    task.getId(), task.getName(), taskProgress.getBeginTime(), taskProgress.getEndTime()));
        });
        notificationManager.notifyPlayer(player, string, RequestCode.SUCCESS.getCode());
    }

    /**
     * 任务完成后进行奖励
     *
     * @param task
     */
    public void taskReward(PlayerBeCache player ,Task task) {
        player.setMoney(player.getMoney()+task.getRewardMoney());
        playerDataService.showPlayerInfo(player);
        task.getRewardToolsMap().forEach((id,num)->{
            Tools toolsById = toolsService.getToolsById(id);
            for(int i=0;i<num;i++){
                Tools tools = new Tools();
                BeanUtils.copyProperties(toolsById,tools);
                tools.setUuid(IdGenerator.getAnId());
                bagService.putInBag(player,tools);
            }
        });
        playerDataService.showPlayerBag(player);
        gameSystem.noticeSomeOne(player.getId(),"奖励相关","任务奖励已发放！",null);
    }

    /**
     * 获取相同类型的任务
     *
     * @param taskType 任务类型
     * @return 任务类型是否相同
     */
    private Stream<Task> getMissionByType(TaskType taskType) {
        return taskCache.allTask().values().stream().
                filter(
                        task -> task.getType().equals(taskType.getCode())
                );
    }

    /**
     * 进度检测器
     *
     * @param taskType 任务类型
     * @param player 玩家
     * @param targetId  与任务相关的目标id
     * @param number 达成的数量/改变的数量  为null代表在当前数值上+1  为数值则进行set操作
     */
    public void checkTaskProgressByNumber(TaskType taskType, PlayerBeCache player, Integer targetId, Integer number) {
        getMissionByType(taskType).filter(task->
            task.getTaskCondition().getCondition().equals(targetId)
        ).forEach(task->{
            //用来记录进度id
            Long progressId=null;
            //如果是已经完成的任务，并且是一次性的直接返回
            for (Long id : player.getTaskAcquireList()) {
                TaskProgressBeCache taskProgressById = taskCache.getTaskProgressById(id);
                if(task.getId().equals(taskProgressById.getTaskId())){
                    Integer kind = taskCache.getTaskById(taskProgressById.getTaskId()).getKind();
                    //只能完成一次的任务
                    if (kind.equals(TaskKindType.ACHIEVEMENT.getCode()) || kind.equals(TaskKindType.NOVICE.getCode())) {
                        log.info("该玩家已经完成了任务{}，故不再进行处理",task.getName());
                        return ;
                    }else{
                        //可以多次完成的任务
                        progressId = taskAccept(player, task.getId());
                    }
                }
            }
            //如果是正在进行的任务，则进行改变
            if(player.getTaskProgressMap().containsKey(task.getId())){
                //更改
                progressId=player.getTaskProgressMap().get(task.getId());
            }else{
                //如果是未接收过的任务，则创建任务
                progressId = taskAccept(player, task.getId());
            }
            if(Objects.nonNull(progressId)){
                //进行数量增加
                TaskProgressBeCache taskProgressById = taskCache.getTaskProgressById(progressId);
                if(Objects.isNull(number)){
                    taskProgressById.setNowAt(taskProgressById.getNowAt()+1);
                }else if(number>taskProgressById.getNowAt()){
                    taskProgressById.setNowAt(number);
                }
                //检查是否达成任务完成条件
                if(taskProgressById.getNowAt() >= task.getTaskCondition().getAim()){
                    taskFinish(player,taskProgressById);
                }
            }
        });
    }

    /**
     * 完成一项任务
     * @param taskProgress
     * @param taskProgress
     */
    public void taskFinish(PlayerBeCache player,TaskProgressBeCache taskProgress){
        Task task = taskCache.getTaskById(taskProgress.getTaskId());
        //通知player
        gameSystem.noticeSomeOne(player.getId(),"任务相关",MessageFormat.format("恭喜你完成了{0}任务",task.getName()),null);
        //移除
        taskProgress.setTaskState(TaskState.FINISH.getCode());
        taskProgress.setEndTime(new Date());
        player.deleteTaskProgressFromMap(taskProgress);
        player.addTaskAcquireList(taskProgress);
        //进行奖励
        taskReward(player,task);
    }

    /**
     * 放弃某个任务
     * @param player
     * @param taskId
     */
    public void taskGaveUp(PlayerBeCache player, Integer taskId) {
        Task task = taskCache.getTaskById(taskId);
        if (Objects.isNull(task)) {
            notificationManager.notifyPlayer(player, "该任务不存在，请检查输入的taskId", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        Long progressId = player.getTaskProgressMap().get(taskId);
        if(Objects.isNull(progressId)){
            notificationManager.notifyPlayer(player, "该任务不在你的待做任务中", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        TaskProgressBeCache taskProgressById = taskCache.getTaskProgressById(progressId);
        taskProgressById.setEndTime(new Date());
        taskProgressById.setTaskState(TaskState.COMPLETE.getCode());
        player.deleteTaskProgressFromMap(taskProgressById);
        notificationManager.notifyPlayer(player, "已经放弃该任务", RequestCode.WARNING.getCode());
    }
}
