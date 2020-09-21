package com.example.gameservicedemo.game.friend.cache;

import com.example.gameservicedemo.game.friend.bean.FriendAddRequest;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/21/20:57
 * @Description: 缓存好友请求
 */
@Slf4j
@Component
public class FriendCache {
    Cache<Long, FriendAddRequest> requestCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.DAYS)
            .removalListener(listener -> {
                log.info("{}缓存移除，原因：{}", listener.getKey(), listener.getCause());
            }).build();

    public void addRequest(FriendAddRequest friendAddRequest) {
        requestCache.put(friendAddRequest.getId(), friendAddRequest);
    }

    public void removeRequest(Long id) {
        requestCache.invalidate(id);
    }

    public FriendAddRequest getRequestById(Long id) {
        return requestCache.getIfPresent(id);
    }
}
