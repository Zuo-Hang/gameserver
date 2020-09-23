package com.example.gameservicedemo.game.scene.service;

import com.example.commondemo.base.RequestCode;
import com.example.gamedatademo.bean.Player;
import com.example.gameservicedemo.game.player.service.PlayerDataService;
import com.example.gameservicedemo.game.player.service.PlayerLoginService;
import com.example.gameservicedemo.game.player.service.PlayerService;
import com.example.gameservicedemo.game.scene.bean.CommonSceneId;
import com.example.gameservicedemo.game.scene.bean.SceneType;
import com.example.gameservicedemo.game.scene.cache.SceneCache;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.scene.bean.Scene;
import com.example.gameservicedemo.manager.NotificationManager;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/06/11:17
 * @Description:
 */
@Service
@Slf4j
public class SceneService {
    @Autowired
    SceneCache sceneCache;
    @Autowired
    PlayerService playerService;
    @Autowired
    PlayerLoginService playerLoginService;
    @Autowired
    PlayerDataService playerDataService;
    @Autowired
    NotificationManager notificationManager;

    /**
     * 根据sceneId获取当前场景中的所有用户化身
     * @param sceneId
     * @return
     */
    public List<Player> getAllPlayer(Integer sceneId) {
        Scene scene = sceneCache.getScene(sceneId);
        Collection<PlayerBeCache> values = scene.getPlayers().values();
        ArrayList<Player> players = new ArrayList<>();
        for (PlayerBeCache playerBeCache : values) {
            if (playerBeCache.getNowAt().equals(sceneId)) {
                players.add(playerBeCache);
            }
        }
        return players;
    }

    /**
     * 获取场景
     * @param sceneId
     * @return
     */
    public Scene getScene(Integer sceneId){
        return sceneCache.getScene(sceneId);
    }

    /**
     * 初始化相邻场景
     * @param scene
     */
    public void initNeighborScene(Scene scene){
        String adjacentScenes = scene.getNeighbors();
        if(Objects.isNull(adjacentScenes)){
            log.info("{} 没有相邻场景",scene.getName());
            return;
        }
        String[] adjacentScenesId = adjacentScenes.split(",");
        for(String s:adjacentScenesId){
            scene.getNeighborScene().add(Integer.valueOf(s));
        }
    }

    /**
     * 获取可以移动到的位置
     *
     * @param context
     */
    public void canMove(ChannelHandlerContext context) {

        PlayerBeCache playerByCtx = playerLoginService.getPlayerByContext(context);
        Integer nowAt = playerByCtx.getNowAt();
        //缓存中获取场景信息
        List<Integer> neighborScene = getScene(nowAt).getNeighborScene();
        if (neighborScene.isEmpty()) {
            notificationManager.notifyByCtx(context, "这里已经是世界的尽头了", RequestCode.SUCCESS.getCode());
            return;
        }
        StringBuffer ret = new StringBuffer();
        ret.append("\n相邻场景如下：\n");
        for (Integer sceneId : neighborScene) {
            Scene scene = getScene(sceneId);
            String sceneInformation = scene.getId() + " " + scene.getName() + " " + scene.getDescribe() + "\n";
            ret.append("场景：" + sceneInformation);
        }
        notificationManager.notifyByCtx(context, ret, RequestCode.SUCCESS.getCode());
    }

    /**
     * 化身移动
     *
     * @param context 上下文
     * @param sceneId 将移动到的场景id
     */
    public void move(ChannelHandlerContext context, Integer sceneId) {
        //获取当前化身，并设置自身位置，更新数据库当中的场景信息
        PlayerBeCache playerByCtx = playerLoginService.getPlayerByContext(context);
        Integer nowAt = playerByCtx.getNowAt();
        //获取当前场景
        Scene sceneNow = getScene(nowAt);
        //进行判断从当前场景是否可以到达目标场景
        if (!sceneNow.getNeighborScene().contains(sceneId)) {
            notificationManager.notifyByCtx(context, "这里并不能到达地点" + sceneId, RequestCode.BAD_REQUEST.getCode());
            return;
        }
        Scene whileGo = getScene(sceneId);
        if (!Objects.isNull(whileGo)) {
            //移除旧场景中的化身
            sceneNow.getPlayers().remove(playerByCtx.getPlayerId());
            //更新player缓存中的玩家信息
            playerByCtx.setNowAt(sceneId);
            PlayerBeCache playerByCtx1 = playerLoginService.getPlayerByContext(context);
            log.info(playerByCtx1.toString());
            //在新场景中加入化身
            whileGo.getPlayers().put(playerByCtx.getPlayerId(), playerByCtx);
            notificationManager.notifyByCtx(context, "你已在场景：" + whileGo.getName(), RequestCode.ABOUT_SCENE.getCode());
        } else {
            notificationManager.notifyByCtx(context, "输入的场景名称错误", RequestCode.BAD_REQUEST.getCode());
        }
    }

    /**
     * 当化身进入场景（改变）时触发
     * @param player
     */
    public void initPlayerScene(PlayerBeCache player) {
        Scene scene = sceneCache.getScene(player.getNowAt());
        if (Objects.isNull(scene)) {
            scene = sceneCache.getScene(CommonSceneId.CEMETERY.getId());
        }
        // 如果玩家场景id显示在副本但是身上却没关联副本实例，返回墓地的场景
        if (scene.getType().equals(SceneType.INSTANCE_SCENE.getCode()) && Objects.isNull(player.getSceneNowAt())){
            Scene cemetery = sceneCache.getScene(CommonSceneId.CEMETERY.getId());
            player.setNowAt(CommonSceneId.CEMETERY.getId());
            player.setSceneNowAt(cemetery);
            cemetery.getPlayers().put(player.getId(),player);
            return;
        }
        scene.getPlayers().put(player.getId(),player);
        player.setSceneNowAt(scene);
        // 广播
        notificationManager.notifyScene(scene,
                MessageFormat.format("{0}进入{1}场景",player.getName(),scene.getName()),
                RequestCode.WARNING.getCode());
        playerDataService.showPlayerPosition(player);
    }

    /**
     *  移动到某个场景
     * @param player 玩家
     * @param targetScene 场景
     */
    public void moveToScene(PlayerBeCache player, Scene targetScene) {
        // 从旧场景移除
        Scene formScene = player.getSceneNowAt();
        formScene.getPlayers().remove(player.getId());
        player.setNowAt(targetScene.getId());
        // 宠物相关
        if (Objects.nonNull(player.getPet())) {
            player.getSceneNowAt().getMonsters().remove(player.getPet().getUuid());
            targetScene.getMonsters().put(player.getPet().getUuid(),player.getPet());
        }
        // 放入目的场景
        targetScene.getPlayers().put(player.getId(), player);
        player.setSceneNowAt(targetScene);
        // 进入场景广播
        if(!targetScene.equals(getScene(7))){
            notificationManager.notifyScene(targetScene,"有玩家进入",RequestCode.WARNING.getCode());
            notificationManager.notifyScene(formScene,"有玩家离开",RequestCode.WARNING.getCode());
        }
    }

    /**
     * 获取所有场景
     */
    public Collection<Scene> getAllScene(){
        return  sceneCache.getAllScene();
    }
}
