package com.example.gameservicedemo.game.player.cache;

import com.example.gamedatademo.bean.Player;
import com.example.gamedatademo.mapper.PlayerMapper;
import com.example.gameservicedemo.background.WriteBackDB;
import com.example.gameservicedemo.game.bag.service.BagService;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.bean.RoleType;
import com.example.gameservicedemo.game.player.service.RoleTypeService;
import com.example.gameservicedemo.game.skill.bean.Skill;
import com.example.gameservicedemo.game.tools.bean.ToolsProperty;
import com.google.common.cache.CacheBuilder;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/14/10:28
 * @Description: 玩家化身缓存（与数据库交互，从数据库取出数据）
 */
@Slf4j
@Component
public class AllPlayerCache {
    @Autowired
    RoleTypeService roleTypeService;
    @Autowired
    WriteBackDB writeBackDB;
    @Autowired
    BagService bagService;
    @Autowired
    PlayerMapper playerMapper;
    /**
     * 用来缓存玩家化身信息，<playerId,playerBeCache>,在这里有数据库中一条记录唯一对应的一个实例。
     */
    com.google.common.cache.Cache<Integer, PlayerBeCache> cache = CacheBuilder.newBuilder()
            .concurrencyLevel(10)
            .initialCapacity(100)
            .maximumSize(5000)
            .expireAfterWrite(3, TimeUnit.HOURS)
            .recordStats()
            .removalListener(notification -> {
                log.info("{}玩家被移除，原因是{}", notification.getKey(), notification.getCause());
            }).build();


    public PlayerBeCache getPlayerById(Integer playerId) {
        //先从缓存中获取，缓存中不存在再从数据库获取。
        PlayerBeCache player = cache.getIfPresent(playerId);
        if (Objects.nonNull(player)) {
            return player;
        }
        PlayerBeCache playerBeCache = loadFromDb(playerId);
        if (Objects.nonNull(playerBeCache)) {
            //存入缓存，并返回这个对象
            cache.put(playerBeCache.getId(), playerBeCache);
            return playerBeCache;
        }
        //数据库也不存在就返回空
        return null;
    }


    public PlayerBeCache loadFromDb(Integer playerId) {
        Player player = playerMapper.selectByPlayerId(playerId);
        if (Objects.isNull(player)) {
            return null;
        }
        PlayerBeCache playerBeCache = new PlayerBeCache();
        playerBeCache.setWriteBackDB(writeBackDB);
        BeanUtils.copyProperties(player, playerBeCache);
        playerBeCache.setOver(true);
        //设置其他信息
        initPlayerInformation(playerBeCache);
        return playerBeCache;
    }

    public void initPlayerInformation(PlayerBeCache playerBeCache) {
        //获取角色类型
        Integer roleTypeId = playerBeCache.getRoleClass();
        RoleType roleTypeById = roleTypeService.getRoleTypeById(roleTypeId);
        //初始化技能
        Map<Integer, Skill> skillMap = roleTypeById.getSkillMap();
        Map<Integer, Skill> skillHaveMap = playerBeCache.getSkillHaveMap();
        skillMap.values().forEach(skill -> {
            Skill newSkill = new Skill();
            BeanUtils.copyProperties(skill, newSkill);
            newSkill.setActiveTime(System.currentTimeMillis() - newSkill.getCd());
            skillHaveMap.put(newSkill.getId(), newSkill);
        });
        //根据角色类型将要放进缓存中的player属性初始化
        playerBeCache.setHp(roleTypeById.getBaseHp());
        playerBeCache.setMaxHp(roleTypeById.getBaseHp());
        playerBeCache.setMp(roleTypeById.getBaseMp());
        playerBeCache.setMaxMp(roleTypeById.getBaseMp());
        //加载增益属性
        Map<Integer, ToolsProperty> toolsInfluence1 = playerBeCache.getToolsInfluence();
        String attribute = roleTypeById.getGainAttribute();
        Gson gson1 = new Gson();
        ArrayList<ToolsProperty> toolsPropertyList = gson1.fromJson(attribute, new TypeToken<ArrayList<ToolsProperty>>() {
        }.getType());
        if (!Objects.isNull(toolsPropertyList)) {
            toolsPropertyList.forEach(v -> {
                toolsInfluence1.put(v.getId(), v);
            });
        }
        //初始化背包
        bagService.initSomeOneBag(playerBeCache);
        //初始化好友列表
        if(Objects.nonNull(playerBeCache.getFriends())&&Strings.isNotEmpty(playerBeCache.getFriends())){
            ArrayList<Integer> list = gson1.fromJson(playerBeCache.getFriends(), new TypeToken<ArrayList<Integer>>() {
            }.getType());
            playerBeCache.setFriendList(list);
        }
        //初始化等级
        playerBeCache.setLevel(playerBeCache.getExp()/10);
        //初始化任务列表
        if(Strings.isNotEmpty(playerBeCache.getTaskProgressJson())){
            Map<Integer, Long> map = gson1.fromJson(playerBeCache.getTaskProgressJson(), new TypeToken<Map<Integer, Long>>() {
            }.getType());
            playerBeCache.setTaskProgressMap(map);
        }
    }

}
