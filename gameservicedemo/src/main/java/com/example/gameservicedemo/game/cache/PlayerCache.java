package com.example.gameservicedemo.game.cache;

import com.example.gamedatademo.bean.Player;
import com.example.gameservicedemo.game.service.bean.PlayerBeCache;
import com.example.gameservicedemo.manager.NotificationManager;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/05/17:16
 * @Description: 玩家化身的缓存
 */
@Slf4j
@Component
public class PlayerCache {

    /**
     * 以上下文为键，玩家信息为值的缓存
     */
    private static Cache<ChannelHandlerContext, PlayerBeCache> ctxPlayerCache = CacheBuilder.newBuilder()
            // 设置并发级别，最多8个线程同时写
            .concurrencyLevel(10)

            // 设置缓存容器的初始容量为100
            .initialCapacity(100)
            .maximumSize(5000)
            .recordStats()
            .removalListener(
                    notification -> System.out.println(notification.getKey() + "玩家被移除，原因是" + notification.getCause())
            ).build();

    /**
     * 以玩家化身id为键，上下文为值的缓存
     * 其目的是实现单点登录
     */
    private static Cache<Long, ChannelHandlerContext> IdCtxCache = CacheBuilder.newBuilder().build();

    /**
     *  键为channel id
     */

    public Player getPlayerByCtx(ChannelHandlerContext ctx) {
        return ctxPlayerCache.getIfPresent(ctx);
    }


    /**
     *  值为玩家
     */
    public void putCtxPlayer(ChannelHandlerContext ctx, PlayerBeCache playerBeCache) {
        //获取老的上下文信息
        ChannelHandlerContext old = getCxtByPlayerId(playerBeCache.getPlayerId());
        Optional.ofNullable(old).ifPresent(o -> {
                    ctxPlayerCache.invalidate(o);
                    if (!old.equals(ctx)) {
                        NotificationManager.notifyByCtx(old,"角色在其他敌方登陆，你已不能进行正常角色操作，除非重新登陆用户加载角色");
                    }
                }
        );

        ctxPlayerCache.put(ctx,playerBeCache);
    }


    /**
     *  通过 channel Id 清除玩家信息
     */
    public void  removePlayerByChannelId(String channelId) {
        ctxPlayerCache.invalidate(channelId);
    }


    /**
     * 玩家id来保存ChannelHandlerContext
     */
    public void savePlayerCtx(long playerId, ChannelHandlerContext cxt) {
        IdCtxCache.put(playerId, cxt);
    }


    /**
     *  根据玩家id获取ChannelHandlerContext
     * @param playerId 玩家id
     */
    public ChannelHandlerContext getCxtByPlayerId(long playerId) {
        return IdCtxCache.getIfPresent(playerId);
    }

    /**
     * 移除
     * @param playerId
     */
    public  void removePlayerCxt(long playerId) {
        IdCtxCache.invalidate(playerId);
    }

    /**
     * 获取所有的缓存信息
     * @return
     */
    public Map<ChannelHandlerContext, PlayerBeCache> getAllPlayerCache() {
        return ctxPlayerCache.asMap();
    }

}
