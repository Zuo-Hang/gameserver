package com.example.gameservicedemo.game.trade.cache;

import com.example.gameservicedemo.game.trade.bean.TradeBoard;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
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
    /**
     * 交易栏缓存(<交易id,交易板>)
     */
    private Cache<Long, TradeBoard> tradeBoardCache = CacheBuilder.newBuilder()
            // 设置360秒后交易栏被移除
            .expireAfterWrite(360, TimeUnit.SECONDS)
            .removalListener(
                    notification -> System.out.println(notification.getKey() + "交易栏被移除, 原因是" + notification.getCause())
            ).build();

    public void putCache(TradeBoard tradeBoard) {
        tradeBoardCache.put(tradeBoard.getId(), tradeBoard);
    }

    public TradeBoard getTradeBoardByPlayerId(Long tradeBoard) {
        return tradeBoardCache.getIfPresent(tradeBoard);
    }
}
