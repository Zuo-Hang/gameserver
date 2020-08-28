package com.example.gameservicedemo.game.bag.service;

import com.example.commondemo.base.RequestCode;
import com.example.gameservicedemo.game.bag.bean.BagBeCache;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.service.PlayerDataService;
import com.example.gameservicedemo.game.tools.bean.Tools;
import com.example.gameservicedemo.manager.NotificationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/25/17:55
 * @Description:
 */
@Component
public class BagService {
    @Autowired
    NotificationManager notificationManager;
    @Autowired
    PlayerDataService playerDataService;

    /**
     * 从背包中获取当前物品（有无）
     * 判断物品是否可以叠加
     * 有且不可叠加->不可购买
     *     ->可叠加并且已叠加数量可叠加最大数量则叠加
     * 无->判断背包是否还有位置
     * 放入背包
     * @param player
     * @param newTools
     * @return
     */
    public boolean putInBag(PlayerBeCache player, Tools newTools){
        //判断背包中是否存在此类型道具
        BagBeCache bagBeCache = player.getBagBeCache();
        Tools tools = bagBeCache.getToolsMap().get(newTools.getId());
        if(!Objects.isNull(tools)){
            if(tools.getCount().equals(tools.getRepeat())){

                notificationManager.notifyPlayer(player,"此装备你已叠加到最大值，不能放入背包", RequestCode.WARNING.getCode());
                return false;
            }
            //以叠加的方式放入背包
            tools.setCount(tools.getCount()+1);

        }else{
            //判断背包中是否有足够的足够的位置存放
            if(bagBeCache.getToolsMap().size()>=bagBeCache.getSize()){
                notificationManager.notifyPlayer(player,"你的背包已满，要购买背包中不存在类型的装备必须卖掉某些装备",RequestCode.WARNING.getCode());
                return false;
            }
            bagBeCache.getToolsMap().put(newTools.getId(),newTools);
        }
        playerDataService.showPlayerBag(player);
        return true;
    }

    /**
     * 从背包中批量移除
     * @param player
     * @param newTools
     * @param count
     * @return
     */
    public boolean removeFromBag(PlayerBeCache player, Tools newTools,Integer count){
        return false;
    }

}
