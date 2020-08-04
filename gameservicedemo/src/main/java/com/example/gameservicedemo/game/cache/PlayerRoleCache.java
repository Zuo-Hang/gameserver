package com.example.gameservicedemo.game.cache;

import com.example.gamedatademo.bean.Role;
import com.example.gameservicedemo.base.BaseCache;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/04/12:07
 * @Description: 玩家角色的缓存
 */
@Slf4j
public class PlayerRoleCache implements BaseCache<Integer, List<Role>>{
    /**
     * 以 User id为key 对应 用户的角色列表为值的缓存
     */
    private static Cache<Integer, List<Role>> playerRoleListCache = CacheBuilder.newBuilder()
            // 设置写缓存后，三小时过期
            .expireAfterWrite(3, TimeUnit.HOURS)
            .removalListener(
                    notification -> log.info(notification.getKey() + "被移除, 原因是" + notification.getCause())
            ).build();

    @Override
    public List<Role> get(Integer userId) {
        return playerRoleListCache.getIfPresent(userId);
    }

    @Override
    public void put(Integer userId, List<Role> roleList) {
        playerRoleListCache.put(userId,roleList);
    }
}
