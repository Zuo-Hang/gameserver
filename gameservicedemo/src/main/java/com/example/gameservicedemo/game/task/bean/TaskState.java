package com.example.gameservicedemo.game.task.bean;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/21/15:12
 * @Description: 标记任务进度的状态
 */
public enum  TaskState {
    RUNNING(0,"进行中"),

    COMPLETE( 1,"任务已放弃"),

    FINISH( 2,"任务完成");

    Integer code;
    String describe;

    TaskState(Integer code, String describe) {
        this.code = code;
        this.describe = describe;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescribe() {
        return describe;
    }
}
