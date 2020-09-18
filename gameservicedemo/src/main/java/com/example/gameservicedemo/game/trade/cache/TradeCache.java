package com.example.gameservicedemo.game.trade.cache;

import com.example.gameservicedemo.game.bag.service.BagService;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.service.PlayerDataService;
import com.example.gameservicedemo.game.player.service.PlayerLoginService;
import com.example.gameservicedemo.game.trade.bean.TradeBoard;
import com.example.gameservicedemo.game.trade.bean.TradeState;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/11/12:19
 * @Description: 缓存所有的交易信息
 */
@Component
@Slf4j
public class TradeCache {
    @Autowired
    PlayerLoginService playerLoginService;
    @Autowired
    PlayerDataService playerDataService;
    @Autowired
    BagService bagService;
    /**
     * 交易栏缓存(<交易id,交易板>)
     */
    private Cache<Long, TradeBoard> tradeBoardCache = CacheBuilder.newBuilder()
            .expireAfterWrite(360, TimeUnit.SECONDS)
            .removalListener(notification -> {
                TradeBoard tradeBoard = (TradeBoard)notification.getValue();
                //如果正处于交易进行中，返还交易中的物品
                if(TradeState.TRADING.getCode().equals(tradeBoard.getState())){
                    tradeBoard.getMoneyMap().forEach((playerId,count)->{
                        PlayerBeCache playerById = playerLoginService.getPlayerById(playerId);
                        playerById.setMoney(playerById.getMoney()+count);//应该用线程安全的方式
                        playerDataService.showPlayerInfo(playerById);
                    });
                    tradeBoard.getPlayerTools().forEach((playerId,toolsMap)->{
                        toolsMap.values().forEach(tools -> {
                            PlayerBeCache playerById = playerLoginService.getPlayerById(playerId);
                            bagService.putInBag(playerById,tools);
                            playerDataService.showPlayerBag(playerById);
                        });
                    });
                }
                System.out.println(notification.getKey() + "交易栏被移除, 原因是" + notification.getCause());
            })
            .build();

    public void putCache(TradeBoard tradeBoard) {
        tradeBoardCache.put(tradeBoard.getId(), tradeBoard);
    }

    public TradeBoard getTradeBoardByPlayerId(Long tradeBoard) {
        return tradeBoardCache.getIfPresent(tradeBoard);
    }
}
