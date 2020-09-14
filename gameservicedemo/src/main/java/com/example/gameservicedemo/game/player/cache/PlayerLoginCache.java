package com.example.gameservicedemo.game.player.cache;

import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/14/11:59
 * @Description: 用来保存所有登录了的用户信息，将会话与用户绑定
 */
@Component
public class PlayerLoginCache {

    private Map<Channel, Integer> channelPlayer =new ConcurrentHashMap<>();

    private Map<Integer, Channel> playerIdChannel=new ConcurrentHashMap<>();

    public void putCache(Integer playerId, Channel channel){
        channelPlayer.put(channel,playerId);
        playerIdChannel.put(playerId,channel);
    }

    public Channel getChannelByPlayerId(Integer playerId){
        return playerIdChannel.get(playerId);
    }

    public Integer getPlayerIdByChannel(Channel channel){
        return channelPlayer.get(channel);
    }

    public Collection<Integer> getAllLoginPlayerId(){
        return channelPlayer.values();
    }

    public void removeCache(Channel channel){
        playerIdChannel.remove(channelPlayer.get(channel));
        channelPlayer.remove(channel);
    }

    public void removeCache(Integer playerId){
        channelPlayer.remove(playerIdChannel.get(playerId));
        playerIdChannel.remove(playerId);
    }
}
