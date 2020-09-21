package com.example.gamedatademo.bean;

import lombok.Data;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/21/16:07
 * @Description: 需要持久化的任务进度信息
 */
@Data
public class TaskProgress {
    /**
     * 当前记录的id
     */
    Long id;
    /**
     * 此条记录对应哪类任务
     */
    Integer taskId;
    /**
     * 任务进行的状态
     */
    Integer taskState;
    /**
     * 接受任务的时间
     */
    Date beginTime;
    /**
     * 结束任务的时间
     */
    Date endTime;

    String progressJson;

    Set<String> update=new HashSet<>();
}
