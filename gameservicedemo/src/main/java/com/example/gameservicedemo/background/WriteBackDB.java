package com.example.gameservicedemo.background;

import com.example.gamedatademo.bean.Player;
import com.example.gamedatademo.mapper.BagMapper;
import com.example.gamedatademo.mapper.PlayerMapper;
import com.example.gameservicedemo.bean.BagBeCache;
import com.example.gameservicedemo.bean.PlayerBeCache;
import com.example.gameservicedemo.bean.shop.Tools;
import com.example.gameservicedemo.bean.shop.ToolsProperty;
import com.example.gameservicedemo.cache.PlayerCache;
import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import lombok.Synchronized;
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
    @Autowired
    BagMapper bagMapper;

    /**
     * 每隔15秒将用户的数据回写到数据库
     */
    @Scheduled(fixedRate=1000*15)
    public void writeBackPlayer(){
        Map<ChannelHandlerContext, PlayerBeCache> allPlayerCache = playerCache.getAllPlayerCache();
        for( PlayerBeCache player: allPlayerCache.values()){
            Integer integer = playerMapper.updateByPlayerId(player);
            BagBeCache bagBeCache = player.getBagBeCache();
            Map<Integer, Tools> toolsMap = bagBeCache.getToolsMap();
            Gson gson = new Gson();
            String s = gson.toJson(toolsMap.values());
            bagBeCache.setItems(s);
            Integer integer1 = bagMapper.updateByBagId(bagBeCache);
            if(integer!=0){
                log.info("数据更新成功");
            }
        }
    }

    /**
     * 五秒回蓝回血机制,自动金币增长
     */
    @Scheduled(fixedRate = 1000*5)
    public void recoveryHpMp(){
        Map<ChannelHandlerContext, PlayerBeCache> allPlayerCache = playerCache.getAllPlayerCache();
        allPlayerCache.values().forEach(v->{
            //防止线程安全问题
            synchronized (v){
                //自动金币增长
                v.setMoney(v.getMoney()+10);
                //如果残血，自动回血
                if(v.getHp()<v.getMaxHp()){
                    //按自身的每五秒回血值回血
                    Integer shouldAdd=v.getHp()+v.getToolsInfluence().get(14).getValue();
                    if(shouldAdd>v.getMaxHp()){//不能超过自身最大血量
                        shouldAdd=v.getMaxHp();
                    }
                    v.setHp(shouldAdd);
                }
                //如果残蓝，回蓝
                if(v.getMp()<v.getMaxMp()){
                    //按自身的每五秒回血值回血
                    Integer shouldAdd=v.getMp()+v.getToolsInfluence().get(15).getValue();
                    if(shouldAdd>v.getMaxMp()){//不能超过自身最大魔法值
                        shouldAdd=v.getMaxMp();
                    }
                    v.setMp(shouldAdd);
                }
            }
        });
        log.info("-------每五秒自动恢复机制执行完毕----------");
    }
}
