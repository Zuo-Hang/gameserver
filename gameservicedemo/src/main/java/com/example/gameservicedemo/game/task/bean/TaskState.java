package com.example.gameservicedemo.game.task.bean;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/21/15:12
 * @Description: 任务状态
 */
public enum  TaskState {
    NOT_START( 1,"任务未接"),

    RUNNING(2,"进行中"),

    COMPLETE( 3,"任务完成"),

    FINISH( 4,"任务结束"),

    // 任务完成了之后不再触发
    NEVER(5,"不再触发");

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
