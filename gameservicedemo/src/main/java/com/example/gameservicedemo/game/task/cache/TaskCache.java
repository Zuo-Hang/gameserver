package com.example.gameservicedemo.game.task.cache;

import com.example.gamedatademo.bean.TaskProgress;
import com.example.gameservicedemo.game.task.bean.Task;
import com.example.gameservicedemo.game.task.bean.TaskProgressBeCache;
import com.example.gameservicedemo.util.excel.subclassexcelutil.TaskExcelUtil;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    Map<Integer, Task> taskCache = new ConcurrentHashMap<>();

    Cache<Long, TaskProgressBeCache> taskProgressCache = CacheBuilder.newBuilder()
            .removalListener(
                    notification -> log.info(notification.getKey() + " 任务成就被移除，原因是" + notification.getCause())
            ).build();

    /**
     * 初始化所有任务对象
     */
//    @PostConstruct
//    public void init() {
//        TaskExcelUtil taskExcelUtil = new TaskExcelUtil("C:\\java_project\\mmodemo\\gameservicedemo\\src\\main\\resources\\game_configuration_excel\\task.xlsx");
//        Map<Integer, Task> map = taskExcelUtil.getMap();
//        map.values().forEach(task -> {
//            Gson gson = new Gson();
//            if(task.getConditionsMap().isEmpty()&& Strings.isNullOrEmpty(task.getCompletionConditions())){
//                Map<String, Integer> conditionsMap = gson.fromJson(task.getCompletionConditions(), new TypeToken<Map<String, Integer>>() {
//                }.getType());
//                task.setConditionsMap(conditionsMap);
//            }
//            log.info("任务{}的完成条件已加载",task.getId());
//            if(task.getRewardToolsMap().isEmpty()&& Strings.isNullOrEmpty(task.getRewardTools())){
//                Map<Integer, Integer> rewardToolsMap = gson.fromJson(task.getRewardTools(), new TypeToken<Map<Integer, Integer>>(){}.getType());
//                task.setRewardToolsMap(rewardToolsMap);
//            }
//            log.info("加载了id={}的任务奖励",task.getId());
//            taskCache.put(task.getId(), task);
//        });
//    }

public Map<Integer,Task> allTask(){
        return taskCache;
}

public Task getTaskById(Integer taskId){
        return taskCache.get(taskId);
}

public TaskProgressBeCache getTaskProgressById(Long id){
        //---------------------------------------需要写成懒加载
        return taskProgressCache.getIfPresent(id);
}


}
