package com.example.gameservicedemo.background;

import com.example.gamedatademo.bean.Player;
import com.example.gamedatademo.mapper.PlayerMapper;
import com.example.gameservicedemo.bean.PlayerBeCache;
import com.example.gameservicedemo.cache.PlayerCache;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/12/16:50
 * @Description:
 */
@Component
@Slf4j
public class WriteBackDB {
    @Autowired
    PlayerMapper playerMapper;
    @Autowired
    PlayerCache playerCache;

    /**
     * 每隔15秒将用户的数据回写到数据库
     */
    @Scheduled(fixedRate=1000*15)
    public void writeBackPlayer(){
        Map<ChannelHandlerContext, PlayerBeCache> allPlayerCache = playerCache.getAllPlayerCache();
        for( Player player: allPlayerCache.values()){
            Integer integer = playerMapper.updateByPlayerId(player);
            if(integer!=0){
                log.info("数据更新成功");
            }
        }
    }

}
