package com.example.gameservicedemo.game.player.service;

import com.example.commondemo.base.RequestCode;
import com.example.gamedatademo.bean.Bag;
import com.example.gamedatademo.bean.Player;
import com.example.gamedatademo.mapper.BagMapper;
import com.example.gameservicedemo.background.WriteBackDB;
import com.example.gameservicedemo.game.copy.bean.GameCopyScene;
import com.example.gameservicedemo.game.copy.service.GameCopyService;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.cache.AllPlayerCache;
import com.example.gameservicedemo.game.player.cache.PlayerLoginCache;
import com.example.gameservicedemo.game.scene.bean.SceneType;
import com.example.gameservicedemo.game.scene.service.SceneService;
import com.example.gameservicedemo.game.tools.service.ToolsService;
import com.example.gameservicedemo.game.user.bean.UserBeCache;
import com.example.gameservicedemo.game.user.service.UserService;
import com.example.gameservicedemo.manager.NotificationManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    SceneService sceneService;
    @Autowired
    AllPlayerCache allPlayerCache;
    @Autowired
    BagMapper bagMapper;
    @Autowired
    PlayerLoginCache playerLoginCache;
    @Autowired
    GameCopyService gameCopyService;
    @Autowired
    ToolsService toolsService;
    @Autowired
    WriteBackDB writeBackDB;
    @Autowired
    PlayerDataService playerDataService;
    @Autowired
    NotificationManager notificationManager;

    /**
     * 创建玩家
     * 同时创建相关联的背包
     * @param context 上下文
     * @param playerName 新玩家名称
     * @param roleClass 新玩家角色类型
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
        Bag bag = new Bag();
        bag.setName(playerName+"的背包");
        bag.setSize(16);
        Integer insert1 = bagMapper.insert(bag);
        player.setBagId(bag.getId());
        writeBackDB.insertPlayer(player);
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
        Integer oldPlayerId = playerLoginCache.getPlayerIdByChannel(context.channel());
        //如果当前化身为待登录化身角色
        if (oldPlayerId != null && oldPlayerId.equals(playerId)) {
            notificationManager.notifyByCtx(context, "此账号为当前登录账号,操作无效", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //如果为切换化身，就退出之前化身
        if (oldPlayerId != null) {
            logoutScene(context);
        }
        //从缓存中获取角色对象
        PlayerBeCache player = allPlayerCache.getPlayerById(playerId);
        if(Objects.isNull(player)){
            notificationManager.notifyByCtx(context,"该用户不存在！请检查输入的玩家id",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //标记登录
        playerLoginCache.putCache(player.getId(),context.channel());
        //将玩家加入场景缓存当中
        player.setContext(context);
        Map<Integer, PlayerBeCache> players = sceneService.getScene(player.getNowAt()).getPlayers();
        players.put(player.getPlayerId(), player);
        player.setSceneNowAt(sceneService.getScene(player.getNowAt()));
        notificationManager.notifyPlayer(player, MessageFormat.format("角色登陆成功,当前角色：{0}。使用指令 `aoi` 可查看周围环境",
                player.getPlayerName()), RequestCode.SUCCESS.getCode());
        //-----------------------------------------太麻烦了
        playerDataService.showPlayerPosition(player);
        playerDataService.showPlayerInfo(player);
        playerDataService.showPlayerEqu(player);
        playerDataService.showPlayerBag(player);
        playerDataService.showSkill(player);
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
        PlayerBeCache playerByCtx = getPlayerByContext(context);
        //如果当前处于副本先退出副本
        if(playerByCtx.getSceneNowAt().getType().equals(SceneType.INSTANCE_SCENE.getCode())){
            gameCopyService.exitGameCopy(playerByCtx,(GameCopyScene) playerByCtx.getSceneNowAt());
        }
        //卸载装备
        playerByCtx.getEquipmentBar().values().forEach(v -> {
            toolsService.takeOffTools(playerByCtx, v);
        });
        //null处理
        Optional.ofNullable(playerByCtx).ifPresent(
                p -> {
                    Integer nowAt = playerByCtx.getNowAt();
                    notificationManager.notifyScene(sceneService.getScene(nowAt),
                            MessageFormat.format("玩家 {0} 正在退出", playerByCtx.getPlayerName())
                            , RequestCode.SUCCESS.getCode());
                    // 重点，从缓存中移除（缓存持久化、缓存删除）
                    //更新数据库
                    writeBackDB.delayWriteBackPlayer(playerByCtx);
                    //清除缓存
                    playerLoginCache.removeCache(context.channel());
                    //从场景缓存中移除
                    sceneService.getScene(playerByCtx.getNowAt()).getPlayers().remove(playerByCtx.getPlayerId());
                }
        );
        //重置客户端显示
        String string="用户未登录";
        notificationManager.notifyByCtx(context,string,RequestCode.ABOUT_SCENE.getCode());
        notificationManager.notifyByCtx(context,string,RequestCode.ABOUT_SKILL.getCode());
        notificationManager.notifyByCtx(context,string,RequestCode.ABOUT_PLAYER.getCode());
        notificationManager.notifyByCtx(context,string,RequestCode.ABOUT_EQU.getCode());
        notificationManager.notifyByCtx(context,string,RequestCode.ABOUT_BAG.getCode());
        //退登和重新登录是有问题的
    }

    /**
     * 通过context获取角色
     * 先判断是否登录，再从缓存中获取玩家对象
     * @param context
     * @return
     */
    public PlayerBeCache getPlayerByContext(ChannelHandlerContext context) {
        Integer playerIdByChannel = playerLoginCache.getPlayerIdByChannel(context.channel());
        if(Objects.isNull(playerIdByChannel)){
            notificationManager.notifyByCtx(context,"此操作之前未登录！",RequestCode.BAD_REQUEST.getCode());
            return null;
        }
        PlayerBeCache playerById = allPlayerCache.getPlayerById(playerIdByChannel);
        return playerById;
    }

    /**
     * 判断当前会话是否有角色加载
     *
     * @param context
     * @return
     */
    public PlayerBeCache isLoad(ChannelHandlerContext context) {
        PlayerBeCache playerByContext = getPlayerByContext(context);
        if (Objects.isNull(playerByContext)) {
            notificationManager.notifyByCtx(context, "你还未登录，请使用\"load\"登录角色", RequestCode.BAD_REQUEST.getCode());
            return null;
        }
        return playerByContext;
    }

    /**
     * 按照id获取玩家，不保证是登录后的玩家
     * @param playerId
     * @return
     */
    public PlayerBeCache getPlayerById(Integer playerId) {
        return allPlayerCache.getPlayerById(playerId);
    }

    /**
     * 获取当前登录的所有玩家id
     * @return
     */
    public Collection<Integer> getAllPlayerLoaded(){
        return playerLoginCache.getAllLoginPlayerId();
    }

    public Channel getChannelByPlayer(Player player){
        return playerLoginCache.getChannelByPlayerId(player.getPlayerId());
    }
}
