package com.example.gameservicedemo.game.task.bean;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/22/16:06
 * @Description: 任务达成条件
 */
@Data
public class TaskCondition {
    /**
     * 条件
     */
    Integer condition;
    /**
     * 目标
     */
    Integer aim;
}
