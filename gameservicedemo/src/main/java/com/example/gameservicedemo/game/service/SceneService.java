package com.example.gameservicedemo.game.service;

import com.example.gamedatademo.bean.Player;
import com.example.gameservicedemo.game.cache.PlayerCache;
import com.example.gameservicedemo.game.service.bean.PlayerBeCache;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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

    /**
     * 根据sceneId获取当前场景中的所有用户化身
     * @param sceneId
     * @return
     */
    public List<Player> getAllPlayer(Integer sceneId) {
        Map<ChannelHandlerContext, PlayerBeCache> allPlayerCache = playerCache.getAllPlayerCache();
        Collection<PlayerBeCache> values = allPlayerCache.values();
        ArrayList<Player> players = new ArrayList<>();
        for (PlayerBeCache playerBeCache : values) {
            if (playerBeCache.getNowAt().equals(sceneId)) {
                players.add(playerBeCache);
            }
        }
        return players;
    }
}
