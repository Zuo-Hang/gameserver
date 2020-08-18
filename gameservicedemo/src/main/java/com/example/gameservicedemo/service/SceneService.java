package com.example.gameservicedemo.service;

import com.example.commondemo.base.RequestCode;
import com.example.gamedatademo.bean.Player;
import com.example.gameservicedemo.cache.SceneCache;
import com.example.gameservicedemo.bean.PlayerBeCache;
import com.example.gameservicedemo.bean.scene.Scene;
import com.example.gameservicedemo.manager.NotificationManager;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

        PlayerBeCache playerByCtx = playerService.getPlayerByContext(context);
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
        PlayerBeCache playerByCtx = playerService.getPlayerByContext(context);
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
            PlayerBeCache playerByCtx1 = playerService.getPlayerByContext(context);
            log.info(playerByCtx1.toString());
            //在新场景中加入化身
            whileGo.getPlayers().put(playerByCtx.getPlayerId(), playerByCtx);
            notificationManager.notifyByCtx(context, "你已在场景：" + whileGo.getName(), RequestCode.ABOUT_SCENE.getCode());
        } else {
            notificationManager.notifyByCtx(context, "输入的场景名称错误", RequestCode.BAD_REQUEST.getCode());
        }
    }
}
