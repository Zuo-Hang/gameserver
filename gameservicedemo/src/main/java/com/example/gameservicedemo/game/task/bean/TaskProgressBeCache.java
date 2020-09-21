package com.example.gameservicedemo.game.task.bean;

import com.example.gamedatademo.bean.TaskProgress;
import com.example.gameservicedemo.background.WriteBackDB;
import com.google.gson.Gson;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/21/16:01
 * @Description: 任务的进度
 */
@Data
public class TaskProgressBeCache extends TaskProgress {
    WriteBackDB writeBackDB;
    Map<String,Progress> progressMap;

    public TaskProgressBeCache(WriteBackDB writeBackDB, Long id, Integer taskId, Integer taskState, Date beginTime) {
        this.writeBackDB = writeBackDB;
        setId(id);
        setTaskId(taskId);
        setTaskState(taskState);
        setBeginTime(beginTime);
    }

    public TaskProgressBeCache() {
    }

    /**
     * 需要改正--------------------------------------------------------------
     * @param progressMap
     */
    public void addProgressMap(Map<String,Progress> progressMap) {
        this.progressMap = progressMap;
        Gson gson = new Gson();
        setProgressJson(gson.toJson(this.progressMap));
    }
}
