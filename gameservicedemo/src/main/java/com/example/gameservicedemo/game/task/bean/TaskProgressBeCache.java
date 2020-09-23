package com.example.gameservicedemo.game.task.bean;

import com.example.gamedatademo.bean.TaskProgress;
import com.example.gameservicedemo.background.WriteBackDB;
import lombok.Data;

import java.util.Date;

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
    boolean tag;

    public TaskProgressBeCache(WriteBackDB writeBackDB, Long id, Integer taskId, Integer taskState, Date beginTime, Integer nowAt) {
        this.writeBackDB = writeBackDB;
        setId(id);
        setTaskId(taskId);
        setTaskState(taskState);
        setBeginTime(beginTime);
        setNowAt(nowAt);
        setTag(true);
        writeBackDB.insertTaskProgress(this);
    }

    public TaskProgressBeCache() {
    }

    @Override
    public void setNowAt(Integer nowAt) {
        super.setNowAt(nowAt);
        if (tag) {
            getUpdate().add("nowAt");
            writeBackDB.updateTaskProgress(this);
        }

    }

    @Override
    public void setEndTime(Date endTime) {
        super.setEndTime(endTime);
        if (tag) {
            getUpdate().add("endTime");
            writeBackDB.updateTaskProgress(this);
        }
    }

    @Override
    public void setTaskState(Integer taskState){
        super.setTaskState(taskState);
        if (tag) {
            getUpdate().add("taskState");
            writeBackDB.updateTaskProgress(this);
        }
    }
}
