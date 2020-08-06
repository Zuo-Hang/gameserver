package com.example.gameservicedemo.game.service;

import com.example.gamedatademo.bean.Player;
import com.example.gamedatademo.mapper.PlayerMapper;
import com.example.gameservicedemo.game.cache.UserCache;
import com.example.gameservicedemo.game.service.bean.UserBeCache;
import com.example.gameservicedemo.manager.NotificationManager;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/05/16:46
 * @Description:
 */
@Slf4j
@Service
public class PlayerService {
    @Autowired
    PlayerMapper playerMapper;
    public void playerCreat(ChannelHandlerContext context,String playerName){
        UserBeCache userByCtx = UserCache.getUserByCtx(context);
        //int userId = userByCtx.getUserId();
        Player player = new Player();
        player.setPlayerName(playerName);
        player.setUserId(2);
        player.setNowAt(1);
        Integer insert = playerMapper.insert(player);
        log.info("成功创建角色{}",player.toString());
        NotificationManager.notifyByCtx(context,"你已成功创建角色："+playerName+"，快使用playerLogin命令去登录吧");
    }

    public void playerLogin(ChannelHandlerContext context,Integer playerId){

    }
}
