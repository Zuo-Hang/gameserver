package com.example.gameservicedemo.game.guild.cache;

import com.example.gamedatademo.bean.Guild;
import com.example.gamedatademo.mapper.GuildMapper;
import com.example.gameservicedemo.background.WriteBackDB;
import com.example.gameservicedemo.game.guild.bean.GuildBeCache;
import com.example.gameservicedemo.game.guild.bean.PlayerJoinRequest;
import com.example.gameservicedemo.game.tools.bean.Tools;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/09/15:57
 * @Description: 管理公会的缓存和与数据库进行交互
 */
@Slf4j
@Component
public class GuildCache {
    @Autowired
    WriteBackDB writeBackDB;
    @Autowired
    GuildMapper guildMapper;

    private Gson gson = new Gson();

    private static Cache<Long, GuildBeCache> guildCache = CacheBuilder.newBuilder()
            .removalListener(
                    notification -> log.info(notification.getKey() + "公会被移除，原因是" + notification.getCause())
            ).build();

    public void putInCache(GuildBeCache guild) {
        guildCache.put(guild.getId(), guild);
    }

    /**
     * 获得缓存，在这里使用懒加载，即现在缓存中获取，要是缓存中不存在即从数据库加载并存入缓存。要是数据库中还是不存在则返回null
     *
     * @param guildId
     * @return
     */
    public GuildBeCache getGuildByGuildId(Long guildId) {
        GuildBeCache cache = guildCache.getIfPresent(guildId);
        if (Objects.nonNull(cache)) {
            return cache;
        }
//        GuildBeCache guildBeCache = loadFromDB(guildId);
//        if (Objects.nonNull(guildBeCache)) {
//            putInCache(guildBeCache);
//            return guildBeCache;
//        }
        return null;
    }

    @PostConstruct
    public void init() {
        List<Guild> guilds = guildMapper.selectAll();
        guilds.forEach(guild -> {
            GuildBeCache guildBeCache = new GuildBeCache();
            BeanUtils.copyProperties(guild, guildBeCache);
            guildBeCache.setWriteBackDB(writeBackDB);
            loadMember(guildBeCache);
            loadWarehouse(guildBeCache);
            loadJoinRequest(guildBeCache);
            putInCache(guildBeCache);
        });
    }

    public GuildBeCache loadFromDB(Long guildId) {
        Guild guild = guildMapper.selectByGuildId(guildId);
        if (Objects.isNull(guild)) {
            return null;
        }
        GuildBeCache guildBeCache = new GuildBeCache();
        BeanUtils.copyProperties(guild, guildBeCache);
        guildBeCache.setWriteBackDB(writeBackDB);
        loadMember(guildBeCache);
        loadWarehouse(guildBeCache);
        loadJoinRequest(guildBeCache);
        return guildBeCache;
    }

    /**
     * 加载公会成员(从json获取到玩家的id，然后转化为列表)
     *
     * @param guild 公会
     */
    private void loadMember(GuildBeCache guild) {
        if (!Strings.isNullOrEmpty(guild.getMember())) {
            CopyOnWriteArrayList<Integer> ids = gson.fromJson(guild.getMember(), new TypeToken<CopyOnWriteArrayList<Integer>>() {
            }.getType());
            guild.setMemberIdList(ids);
        }
    }

    /**
     * 加载公会仓库
     *
     * @param guild 公会
     */
    private void loadWarehouse(GuildBeCache guild) {
        if (!Strings.isNullOrEmpty(guild.getWarehouse())) {
            Map<Long, Tools> wareHouseMap = gson.fromJson(guild.getWarehouse(), new TypeToken<Map<Long, Tools>>() {
            }.getType());
            guild.setWarehouseMap(wareHouseMap);
        }
    }

    /**
     * 加载玩家入会申请
     *
     * @param guild 公会
     */
    private void loadJoinRequest(GuildBeCache guild) {
        if (Strings.isNullOrEmpty(guild.getJoinRequest())) {
            Map<Integer, PlayerJoinRequest> playerJoinRequestMap = gson.fromJson(guild.getJoinRequest(),
                    new TypeToken<Map<Integer, PlayerJoinRequest>>() {
                    }.getType());
            log.debug("playerJoinRequestList {}", playerJoinRequestMap);
            Optional.ofNullable(playerJoinRequestMap).ifPresent(guild::setPlayerJoinRequestMap);
        }
    }

    public ConcurrentMap<Long, GuildBeCache> getAll() {
        return guildCache.asMap();
    }
}
