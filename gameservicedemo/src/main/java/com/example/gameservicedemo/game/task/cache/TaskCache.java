package com.example.gameservicedemo.game.task.cache;

import com.example.gamedatademo.bean.TaskProgress;
import com.example.gamedatademo.mapper.TaskProgressMapper;
import com.example.gameservicedemo.background.WriteBackDB;
import com.example.gameservicedemo.game.task.bean.Task;
import com.example.gameservicedemo.game.task.bean.TaskCondition;
import com.example.gameservicedemo.game.task.bean.TaskProgressBeCache;
import com.example.gameservicedemo.util.excel.subclassexcelutil.TaskExcelUtil;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/21/12:18
 * @Description: 任务缓存，缓存所有的系统任务
 */
@Component
@Slf4j
public class TaskCache {

    @Autowired
    TaskProgressMapper taskProgressMapper;
    @Autowired
    WriteBackDB writeBackDB;

    Map<Integer, Task> taskCache = new ConcurrentHashMap<>();

    Cache<Long, TaskProgressBeCache> taskProgressCache = CacheBuilder.newBuilder()
            .expireAfterWrite(3, TimeUnit.HOURS)
            .removalListener(
                    notification -> log.info(notification.getKey() + " 任务成就被移除，原因是" + notification.getCause())
            ).build();

    /**
     * 初始化所有任务对象
     */
    @PostConstruct
    public void init() {
        TaskExcelUtil taskExcelUtil = new TaskExcelUtil("C:\\java_project\\mmodemo\\gameservicedemo\\src\\main\\resources\\game_configuration_excel\\task.xlsx");
        Map<Integer, Task> map = taskExcelUtil.getMap();
        map.values().forEach(task -> {
            Gson gson = new Gson();
            if (!Strings.isNullOrEmpty(task.getCompletionConditions())) {
                TaskCondition conditionsMap = gson.fromJson(task.getCompletionConditions(), TaskCondition.class);
                task.setTaskCondition(conditionsMap);
            }
            log.info("任务{}的完成条件已加载", task.getId());
            if (!Strings.isNullOrEmpty(task.getRewardTools())) {
                Map<Integer, Integer> rewardToolsMap = gson.fromJson(task.getRewardTools(), new TypeToken<Map<Integer, Integer>>() {
                }.getType());
                task.setRewardToolsMap(rewardToolsMap);
            }
            log.info("加载了id={}的任务奖励", task.getId());
            taskCache.put(task.getId(), task);
        });
        return;
    }

    public Map<Integer, Task> allTask() {
        return taskCache;
    }

    public Task getTaskById(Integer taskId) {
        return taskCache.get(taskId);
    }

    public TaskProgressBeCache getTaskProgressById(Long id) {
        //---------------------------------------需要写成懒加载
        TaskProgressBeCache present = taskProgressCache.getIfPresent(id);
        if(Objects.isNull(present)){
            TaskProgress taskProgress = taskProgressMapper.selectByTaskProgressId(id);
            if(Objects.isNull(taskProgress)){
                return null;
            }
            present =new TaskProgressBeCache();
            BeanUtils.copyProperties(taskProgress,present);
            present.setTag(true);
            present.setWriteBackDB(writeBackDB);
            taskProgressCache.put(present.getId(),present);
        }
        return present;
    }

    public void putInProgress(TaskProgressBeCache taskProgressBeCache){
        taskProgressCache.put(taskProgressBeCache.getId(),taskProgressBeCache);
    }

}
