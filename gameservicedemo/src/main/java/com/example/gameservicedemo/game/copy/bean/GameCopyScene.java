package com.example.gameservicedemo.game.copy.bean;

import com.example.gameservicedemo.game.scene.bean.Scene;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/26/11:14
 * @Description: 临时场景
 */
@Data
public class GameCopyScene extends Scene {
    // 保存玩家进入副本前的场景id
    private Map<Long,Integer> playerFrom = new ConcurrentHashMap<>();
    // Boss 列表
    private List<BOSS> bossList = new CopyOnWriteArrayList<>();
    // 当前守关Boss
    private BOSS guardBoss;
    // 是否已经挑战副本失败
    private volatile Boolean fail = false;
    //用来关闭场景任务
    private ScheduledFuture<?> attackTask;
}
