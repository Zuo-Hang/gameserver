package com.example.gameservicedemo.game.task.bean;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/21/16:04
 * @Description: 某项任务的目标和当前任务的进度
 */
public class Progress {
    Integer aim;
    Integer now;

    public Progress(Integer aim) {
        this.aim = aim;
    }

    public Progress() {
    }
}
