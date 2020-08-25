package com.example.gameservicedemo.game.scene.bean;

import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.util.excel.EntityName;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Data;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/11/11:33
 * @Description: 场景实体，从Excel中配置
 */
@Data
public class Scene {

    @EntityName(column = "ID")
    private Integer id;

    @EntityName(column = "名字")
    private String name;

    @EntityName(column = "相邻场景")
    private String neighbors = "";

    @EntityName(column = "场景描述")
    private String describe;

    @EntityName(column = "地图类型")
    private String type;

    /**
     * 场景对象，由","分割
     */
    @EntityName(column = "场景对象")
    private String gameObjectIds;

    /**
     * 处于场景的玩家,key为player_id
     */
    private Map<Integer, PlayerBeCache> players = new ConcurrentHashMap<>();

    /**
     * 相邻的场景的id
     */
    private List<Integer> neighborScene = new ArrayList<>();

    /**
     * 处于场景中的NPC
     */
    private Map<Integer, NPC> npcs = new ConcurrentHashMap<>();

    /**
     * 处于场景中的怪物
     */
    private Map<Integer, Monster> monsters = new ConcurrentHashMap<>();

    /**
     * 展示场景
     *
     * @return
     */
    public String display() {
        return MessageFormat.format("id:{0}  name:{1} describe:{3}"
                , this.getId(), this.getName(), this.getDescribe());
    }

    private static ThreadFactory sceneThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("scene-loop-%d").setUncaughtExceptionHandler((t, e) -> e.printStackTrace()).build();
    /**
     * 通过一个场景一个线程处理器的方法保证每个场景的指令循序
     */
    ScheduledExecutorService singleThreadSchedule = Executors.newSingleThreadScheduledExecutor(sceneThreadFactory);
//ThreadPoolExecutor

}
