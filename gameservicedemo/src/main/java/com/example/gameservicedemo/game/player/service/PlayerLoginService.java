package com.example.gameservicedemo.game.player.service;

import com.example.commondemo.base.RequestCode;
import com.example.gamedatademo.bean.Bag;
import com.example.gamedatademo.bean.Player;
import com.example.gamedatademo.mapper.BagMapper;
import com.example.gamedatademo.mapper.PlayerMapper;
import com.example.gameservicedemo.game.bag.bean.BagBeCache;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.bean.RoleType;
import com.example.gameservicedemo.game.player.cache.PlayerCache;
import com.example.gameservicedemo.game.scene.service.SceneService;
import com.example.gameservicedemo.game.skill.bean.Skill;
import com.example.gameservicedemo.game.tools.bean.Tools;
import com.example.gameservicedemo.game.tools.bean.ToolsProperty;
import com.example.gameservicedemo.game.user.bean.UserBeCache;
import com.example.gameservicedemo.game.user.service.UserService;
import com.example.gameservicedemo.manager.NotificationManager;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/25/12:18
 * @Description: 与化身角色账号登录相关的服务
 */
@Slf4j
@Service
public class PlayerLoginService {
    @Autowired
    UserService userService;
    @Autowired
    PlayerMapper playerMapper;
    @Autowired
    PlayerCache playerCache;
    @Autowired
    SceneService sceneService;
    @Autowired
    BagMapper bagMapper;
    @Autowired
    RoleTypeService roleTypeService;
    @Autowired
    PlayerDataService playerDataService;
    @Autowired
    NotificationManager notificationManager;
    /**
     * 创建玩家
     * -------------------------------------------------同时创建相关联的背包
     * @param context
     * @param playerName
     */
    public void playerCreat(ChannelHandlerContext context, String playerName, Integer roleClass) {
        UserBeCache userByCtx = userService.getUserByCxt(context);
        int userId = userByCtx.getUserId();
        Player player = new Player();
        player.setPlayerName(playerName);
        player.setUserId(userId);
        player.setNowAt(1);
        player.setExp(0);
        player.setMoney(0);
        player.setState(1);
        player.setRoleClass(roleClass);
        Integer insert = playerMapper.insert(player);
        log.info("成功创建角色{}", player.toString());
        notificationManager.notifyByCtx(context, "你已成功创建角色：" + playerName + "，快使用 load 命令去登录吧", RequestCode.SUCCESS.getCode());
    }

    /**
     * 玩家登录   判断触发登录的原因，做出相应的处理，包含缓存处理和给用户返回的信息处理
     *
     * @param context
     * @param playerId
     */
    public void playerLogin(ChannelHandlerContext context, Integer playerId) {
        //获取对应上下文的缓存角色
        Player playerByCtx = playerCache.getPlayerByCtx(context);
        //如果当前化身为待登录化身角色
        if (playerByCtx != null && playerByCtx.getPlayerId().equals(playerId)) {
            notificationManager.notifyByCtx(context, "此操作无效", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //如果为切换化身，就退出之前化身
        if (playerByCtx != null) {
            logoutScene(context);
        }
        //从数据库调出待登录化身的信息，登录
        Player player1 = playerMapper.selectByPlayerId(playerId);
        PlayerBeCache playerBeCache = new PlayerBeCache();
        BeanUtils.copyProperties(player1, playerBeCache);
        // 以channel id 为键储存玩家数据
        initPlayerInformation(playerBeCache);
        /** 需要初始化玩家信息*/
        playerCache.putCtxPlayer(context, playerBeCache);
        // 保存playerId跟ChannelHandlerContext之间的关系
        playerCache.savePlayerCtx(playerId, context);
        playerBeCache.setContext(context);
        //将玩家加入场景缓存当中
        Map<Integer, PlayerBeCache> players = sceneService.getScene(player1.getNowAt()).getPlayers();
        players.put(player1.getPlayerId(), playerBeCache);
        playerBeCache.setSceneNowAt(sceneService.getScene(playerBeCache.getNowAt()));
        String format = MessageFormat.format("角色登陆成功,当前角色：{0}。使用指令 `aoi` 可查看周围环境", playerBeCache.getPlayerName());
        notificationManager.notifyPlayer(playerBeCache,format,RequestCode.SUCCESS.getCode());
        playerDataService.showPlayerPosition(playerBeCache);
        playerDataService.showPlayerInfo(playerBeCache);
        playerDataService.showPlayerEqu(playerBeCache);
        playerDataService.showPlayerBag(playerBeCache);
        playerDataService.showSkill(playerBeCache);
    }

    /**
     * 在化身加入缓存之前对其进行初始化
     * 对于背包初始化
     * 初始化技能
     * 初始化影响列表：
     *
     * @param playerBeCache
     */
    public void initPlayerInformation(PlayerBeCache playerBeCache) {
        //获取角色类型
        Integer roleTypeId = playerBeCache.getRoleClass();
        RoleType roleTypeById = roleTypeService.getRoleTypeById(roleTypeId);
        //初始化技能
        Map<Integer, Skill> skillMap = roleTypeById.getSkillMap();
        Map<Integer, Skill> skillHaveMap = playerBeCache.getSkillHaveMap();
        skillMap.values().forEach(skill->{
            Skill newSkill = new Skill();
            BeanUtils.copyProperties(skill,newSkill);
            newSkill.setActiveTime(System.currentTimeMillis()-newSkill.getCd());
            skillHaveMap.put(newSkill.getId(),newSkill);
        });
        //根据角色类型将要放进缓存中的player属性初始化
        playerBeCache.setHp(roleTypeById.getBaseHp());
        playerBeCache.setMaxHp(roleTypeById.getBaseHp());
        playerBeCache.setMp(roleTypeById.getBaseMp());
        playerBeCache.setMaxMp(roleTypeById.getBaseMp());
        //加载增益属性---------------------------------------------------------------------------------------------
        Map<Integer, ToolsProperty> toolsInfluence1 = playerBeCache.getToolsInfluence();
        String attribute = roleTypeById.getGainAttribute();
        Gson gson1 = new Gson();
        ArrayList<ToolsProperty> ToolsPropertylist = gson1.fromJson(attribute, new TypeToken<ArrayList<ToolsProperty>>() {}.getType());
        if(!Objects.isNull(ToolsPropertylist)){
            ToolsPropertylist.forEach(v->{
                toolsInfluence1.put(v.getId(),v);
            });
        }
        //初始化背包-------------------------------------------------------------------------------------------------------
        Bag bag = bagMapper.selectByBagId(playerBeCache.getBagId());
        BagBeCache bagBeCache = new BagBeCache();
        bagBeCache.setPlayerId(playerBeCache.getPlayerId());
        BeanUtils.copyProperties(bag,bagBeCache);
        //获取背包中的物品信息并转化为对象
        String json = bagBeCache.getItems();
        Gson gson = new Gson();
        ArrayList<Tools> toolslist = gson.fromJson(json, new TypeToken<ArrayList<Tools>>() {}.getType());
        if(!Objects.isNull(toolslist)){
            toolslist.forEach(v->{
                //放入被缓存的背包中
                bagBeCache.getToolsMap().put(v.getId(),v);
            });
        }
        playerBeCache.setBagBeCache(bagBeCache);
        log.info("角色：{} 的背包初始化完毕！",playerBeCache.getName());
    }

    /**
     * 判断对应上下文中的用户是否拥有这个角色
     *
     * @param ctx      上下文
     * @param playerId 要判定的角色id
     * @return 用户是否拥有此角色
     */
    public boolean hasPlayer(ChannelHandlerContext ctx, Integer playerId) {
        UserBeCache user = userService.getUserByCxt(ctx);
        List<Player> players = userService.findPlayers(ctx, user.getUserId());
        for (Player player : players) {
            if (player.getPlayerId().equals(playerId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 当前玩家退出场景
     *
     * @param context
     */
    public void logoutScene(ChannelHandlerContext context) {
        //获取到当前化身
        PlayerBeCache playerByCtx = playerCache.getPlayerByCtx(context);
        //null处理
        Optional.ofNullable(playerByCtx).ifPresent(
                p -> {
                    Integer nowAt = playerByCtx.getNowAt();
                    notificationManager.notifyScene(sceneService.getScene(nowAt),
                            MessageFormat.format("玩家 {0} 正在退出", playerByCtx.getPlayerName())
                            , RequestCode.SUCCESS.getCode());
                    // 重点，从缓存中移除（缓存持久化、缓存删除）
                    //更新数据库
                    playerMapper.updateByPlayerId(playerByCtx);
                    //清除缓存
                    playerCache.removePlayerByChannelId(context.channel().id().asLongText());
                    playerCache.removePlayerCxt(playerByCtx.getPlayerId());
                    //从场景缓存中移除
                    sceneService.getScene(playerByCtx.getNowAt()).getPlayers().remove(playerByCtx.getPlayerId());
                }
        );

    }

    /**
     * 通过context获取角色
     *
     * @param context
     * @return
     */
    public PlayerBeCache getPlayerByContext(ChannelHandlerContext context) {
        return playerCache.getPlayerByCtx(context);
    }

    /**
     * 判断当前会话是否有角色加载
     * @param context
     * @return
     */
    public PlayerBeCache isLoad(ChannelHandlerContext context){
        PlayerBeCache playerByContext = getPlayerByContext(context);
        if(Objects.isNull(playerByContext)){
            notificationManager.notifyByCtx(context,"你还未登录，请使用\"load\"登录角色",RequestCode.BAD_REQUEST.getCode());
            return null;
        }
        return playerByContext;
    }

    public PlayerBeCache getPlayerById(Integer playerId){
        ChannelHandlerContext cxtByPlayerId = playerCache.getCxtByPlayerId(playerId);
        PlayerBeCache playerByCtx = playerCache.getPlayerByCtx(cxtByPlayerId);
        return playerByCtx;
    }
}
