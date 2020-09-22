package com.example.gameservicedemo.event.model;


import com.example.gameservicedemo.event.Event;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.task.bean.Task;
import com.example.gameservicedemo.game.task.bean.TaskProgressBeCache;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/25/16:37
 * @Description: 任务事件
 */

@Data
@EqualsAndHashCode(callSuper=true)
public class TaskEvent extends Event {
    private PlayerBeCache player;
    private Task task;
    private TaskProgressBeCache taskProgressBeCache;


    public TaskEvent(PlayerBeCache player, Task task, TaskProgressBeCache taskProgressBeCache) {
        this.player = player;
        this.task = task;
        this.taskProgressBeCache = taskProgressBeCache;
    }
}
