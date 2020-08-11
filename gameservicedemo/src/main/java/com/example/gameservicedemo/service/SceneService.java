package com.example.gameservicedemo.service;

import com.example.gamedatademo.bean.Player;
import com.example.gameservicedemo.cache.SceneCache;
import com.example.gameservicedemo.bean.PlayerBeCache;
import com.example.gameservicedemo.bean.scene.Scene;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.gameservicedemo.cache.PlayerCache;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/06/11:17
 * @Description:
 */
@Service
public class SceneService {
    @Autowired
    PlayerCache playerCache;
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
}
