package com.example.gameservicedemo.service;

import com.example.gamedatademo.bean.Player;
import com.example.gameservicedemo.cache.SceneCache;
import com.example.gameservicedemo.bean.PlayerBeCache;
import com.example.gameservicedemo.bean.scene.Scene;
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
}
