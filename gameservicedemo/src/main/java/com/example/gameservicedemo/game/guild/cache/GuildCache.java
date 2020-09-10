package com.example.gameservicedemo.game.guild.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.gamedatademo.bean.Guild;
import com.example.gamedatademo.mapper.GuildMapper;
import com.example.gameservicedemo.game.bag.bean.Item;
import com.example.gameservicedemo.game.guild.bean.GuildBeCache;
import com.example.gameservicedemo.game.guild.bean.PlayerJoinRequest;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.tools.bean.Tools;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;

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
    GuildMapper guildMapper;
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
        GuildBeCache guildBeCache = loadFromDB(guildId);
        if (Objects.nonNull(guildBeCache)) {
            putInCache(guildBeCache);
            return guildBeCache;
        }
        return null;
    }

    public GuildBeCache loadFromDB(Long guildId) {
        Guild guild = guildMapper.selectByGuildId(guildId);
        if (Objects.isNull(guild)) {
            return null;
        }
        GuildBeCache guildBeCache = new GuildBeCache();
        BeanUtils.copyProperties(guild, guildBeCache);
        loadMember(guildBeCache);
        loadWarehouse(guildBeCache);
        loadJoinRequest(guildBeCache);
        return guildBeCache;
    }

    /**
     * 加载公会成员
     *
     * @param guild 公会
     */
    private static void loadMember(GuildBeCache guild) {
        if (Strings.isNullOrEmpty(guild.getWarehouse())) {
            Map<Integer, PlayerBeCache> memberMap = JSON.parseObject(guild.getMember(),
                    new TypeReference<Map<Integer, PlayerBeCache>>() {
                    });
            guild.setMemberMap(memberMap);
        }
    }

    /**
     * 加载公会仓库
     *
     * @param guild 公会
     */
    private static void loadWarehouse(GuildBeCache guild) {
        if (Strings.isNullOrEmpty(guild.getWarehouse())) {
            Map<Long, Tools> wareHouseMap = JSON.parseObject(guild.getWarehouse(),
                    new TypeReference<Map<Long, Tools>>() {
                    });
            guild.setWarehouseMap(wareHouseMap);
        }
    }

    /**
     * 加载玩家入会申请
     *
     * @param guild 公会
     */
    private static void loadJoinRequest(GuildBeCache guild) {
        if (Strings.isNullOrEmpty(guild.getJoinRequest())) {
            Map<Integer, PlayerJoinRequest> playerJoinRequestMap = JSON.parseObject(guild.getJoinRequest(),
                    new TypeReference<Map<Integer, PlayerJoinRequest>>() {
                    });
            log.debug("playerJoinRequestList {}", playerJoinRequestMap);
            Optional.ofNullable(playerJoinRequestMap).ifPresent(guild::setPlayerJoinRequestMap);
        }
    }

    /**
     * 持久化玩家任务成就进程的线程池，由于持久化不需要保证循序，所以直接用多线程的线程池。
     * 核心线程数 为 服务器核心*2+1
     * 最大线程数是核心线程数的2倍
     */
    private ThreadFactory guildThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("guildPersistence-pool-%d").build();
    private ExecutorService threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2 + 1, (Runtime.getRuntime().availableProcessors() * 2 + 1) * 2,
            1000, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), guildThreadFactory);

    /**
     * 持久化插入公会
     *
     * @param guild 公会
     */
    public void insertGuild(GuildBeCache guild) {
        threadPool.execute(() -> {
            guild.setMember(JSON.toJSONString(guild.getMemberMap()));
            guild.setWarehouse(JSON.toJSONString(guild.getWarehouseMap()));
            guild.setJoinRequest(JSON.toJSONString(guild.getPlayerJoinRequestMap()));
            guildMapper.insert(guild);
        });
    }

    /**
     * 数据库更新公会
     *
     * @param guild 公会
     */
    public void updateGuild(GuildBeCache guild) {
        threadPool.execute(() -> {
            guild.setMember(JSON.toJSONString(guild.getMemberMap()));
            guild.setWarehouse(JSON.toJSONString(guild.getWarehouseMap()));
            guild.setJoinRequest(JSON.toJSONString(guild.getPlayerJoinRequestMap()));
            guildMapper.updateByGuildId(guild);
        });
    }

}
