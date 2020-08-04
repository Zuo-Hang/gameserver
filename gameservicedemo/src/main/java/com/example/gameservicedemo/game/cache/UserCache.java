package com.example.gameservicedemo.game.cache;

import com.example.gamedatademo.bean.User;
import com.example.gameservicedemo.game.service.UserBeCache;
import com.example.gameservicedemo.manager.NotificationManager;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/04/11:38
 * @Description: 用户信息的缓存管理类
 */
@Slf4j
public class UserCache {
    /**
     * 以上下文为key，以用户实例为值的缓存实例
     */
    private static Cache<ChannelHandlerContext, UserBeCache> ctxUserCache = CacheBuilder.newBuilder()
            // 设置并发级别，最多8个线程同时写
            .concurrencyLevel(10)
            // 设置写缓存后，三小时过期
            .expireAfterWrite(3, TimeUnit.HOURS)
            // 设置缓存容器的初始容量为100
            .initialCapacity(100)
            .maximumSize(5000)
            //是否需要统计缓存
            .recordStats()
            .removalListener(
                    notification ->{log.info(notification.getKey() + "用户被移除, 原因是" + notification.getCause());}
            ).build();

    /**
     * 以user id 为键，ChannelHandlerContext上下文为值
     */
    private static Cache<Integer,ChannelHandlerContext> userIdCtxCache = CacheBuilder.newBuilder()
            // 设置写缓存后，三小时过期
            .expireAfterWrite(3, TimeUnit.HOURS)
            .build();

    /**
     *  将上下文和用户关联起来
     * @param ctx   上下文
     * @param user  用户
     */
    public static void putCtxUser(ChannelHandlerContext ctx, UserBeCache user) {
        ChannelHandlerContext old = getCtxByUserId(user.getUserId());
        // 移除之前客户端登陆的用户

        Optional.ofNullable(old).ifPresent( o -> {
                    ctxUserCache.invalidate(o);
                    if (!old.equals(ctx)) {
                        NotificationManager.notifyByCtx(old,"你在另一个登陆，除非你在此从新登陆");
                    }
                }
        );

        ctxUserCache.put(ctx, user);
    }


    /**
     * 以Player id 为键，ChannelHandlerContext上下文为值
     */
    public static void putUserIdCtx(Integer userId, ChannelHandlerContext ctx) {
        userIdCtxCache.put(userId,ctx);
    }

    /**
     * 根据上下文id获取用户信息
     * @param ctx 上下文
     * @return
     */
    public static UserBeCache getUserByCtx(ChannelHandlerContext ctx) {
        return ctxUserCache.getIfPresent(ctx);
    }
    /**
     *    通过User id 获取 ChannelHandlerContext
     */
    public static ChannelHandlerContext getCtxByUserId(Integer userId) {
        return userIdCtxCache.getIfPresent(userId);
    }
    /**
     *  通过用户id获取一个缓存中的用户
     * @param userId 用户id
     * @return 用户
     */
    public static UserBeCache getUserByUserId(Integer userId) {

        return Optional.ofNullable(getCtxByUserId(userId))
                .map(UserCache::getUserByCtx)
                .orElse(null);
    }
}
