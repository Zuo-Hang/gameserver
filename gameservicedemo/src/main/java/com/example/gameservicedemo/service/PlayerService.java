package com.example.gameservicedemo.service;

import com.example.commondemo.base.RequestCode;
import com.example.gamedatademo.bean.Player;
import com.example.gamedatademo.mapper.PlayerMapper;
import com.example.gameservicedemo.bean.Skill;
import com.example.gameservicedemo.bean.scene.Scene;
import com.example.gameservicedemo.cache.PlayerCache;
import com.example.gameservicedemo.bean.scene.NPC;
import com.example.gameservicedemo.bean.PlayerBeCache;
import com.example.gameservicedemo.bean.UserBeCache;
import com.example.gameservicedemo.manager.NotificationManager;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/05/16:46
 * @Description:
 */
@Slf4j
@Service
public class PlayerService {
    @Autowired
    PlayerMapper playerMapper;
    @Autowired
    UserService userService;
    @Autowired
    PlayerCache playerCache;
    @Autowired
    SceneService sceneService;
    @Autowired
    RoleTypeService roleTypeService;
    @Autowired
    NotificationManager notificationManager;

    /**
     * 创建玩家
     *
     * @param context
     * @param playerName
     */
    public void playerCreat(ChannelHandlerContext context, String playerName,Integer roleClass) {
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
        StringBuilder result = new StringBuilder();
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
        result.append(playerBeCache.getPlayerName()).append(",角色登陆成功")
                .append("\n 你所在位置为: ")
                .append(playerBeCache.getNowAt()).append("\n");
        result.append("使用指令 `aoi` 可查看周围环境");
        notificationManager.notifyByCtx(context, result.toString(), RequestCode.ABOUT_PLAYER.getCode());
    }

    /**
     * 在化身加入缓存之前对其进行初始化
     * @param playerBeCache
     */
    public void initPlayerInformation(PlayerBeCache playerBeCache){
//        //获取角色类型
//        Integer roleClass = playerBeCache.getRoleClass();
//        final Map<Integer, Skill> skillMap = roleTypeCache.getRoleType(roleClass).getSkillMap();
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
        List<Player> players = userService.findPlayers(ctx,user.getUserId());
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
                    notificationManager.notifyScene(nowAt,
                            MessageFormat.format("玩家 {0} 正在退出", playerByCtx.getPlayerName())
                            , RequestCode.SUCCESS.getCode());
                    // 重点，从缓存中移除（缓存持久化、缓存删除）
                    Player player = new Player();
                    player.setUserId(playerByCtx.getUserId());
                    player.setNowAt(playerByCtx.getNowAt());
                    player.setPlayerName(playerByCtx.getPlayerName());
                    player.setPlayerId(playerByCtx.getPlayerId());
                    //更新数据库
                    playerMapper.updateByPlayerId(player);
                    //清除缓存
                    playerCache.removePlayerByChannelId(context.channel().id().asLongText());
                    playerCache.removePlayerCxt(playerByCtx.getPlayerId());
                    //从场景缓存中移除
                    sceneService.getScene(player.getNowAt()).getPlayers().remove(player.getPlayerId());
                }
        );

    }

    /**
     * 获取周边环境(展示同一场景内的NPC)
     *
     * @param context
     */
    public void aoi(ChannelHandlerContext context) {
        //获取当前场景的所有实体信息
        PlayerBeCache playerByCtx = playerCache.getPlayerByCtx(context);
        Integer nowAt = playerByCtx.getNowAt();
        Map<Integer, NPC> npcs = sceneService.getScene(nowAt).getNpcs();
        Collection<NPC> values = npcs.values();
        if (values == null) {
            notificationManager.notifyByCtx(context, "这里空无一人！", RequestCode.SUCCESS.getCode());
            return;
        }
        StringBuffer ret = new StringBuffer();
        ret.append("\nnpc如下：\n");
        if (values != null) {
            for (NPC objectsId : values) {
                ret.append(objectsId.displayData()+"\n");
            }
        }
        notificationManager.notifyByCtx(context, ret.toString(), RequestCode.SUCCESS.getCode());
    }

    /**
     * 获取可以移动到的位置
     *
     * @param context
     */
    public void canMove(ChannelHandlerContext context) {
        PlayerBeCache playerByCtx = playerCache.getPlayerByCtx(context);
        Integer nowAt = playerByCtx.getNowAt();
        //缓存中获取场景信息
        List<Integer> neighborScene = sceneService.getScene(nowAt).getNeighborScene();
        if(neighborScene.isEmpty()){
            notificationManager.notifyByCtx(context, "这里已经是世界的尽头了", RequestCode.SUCCESS.getCode());
            return;
        }
        StringBuffer ret = new StringBuffer();
        ret.append("\n相邻场景如下：\n");
        for (Integer sceneId : neighborScene) {
            Scene scene = sceneService.getScene(sceneId);
            String sceneInformation = scene.getId() + " " + scene.getName() + " " + scene.getDescribe()+"\n";
            ret.append("场景：" + sceneInformation);
        }
        notificationManager.notifyByCtx(context, ret, RequestCode.SUCCESS.getCode());
    }

    /**
     * 化身移动
     *
     * @param context 上下文
     * @param sceneId 将移动到的场景id
     */
    public void move(ChannelHandlerContext context, Integer sceneId) {
        //获取当前化身，并设置自身位置，更新数据库当中的场景信息
        PlayerBeCache playerByCtx = playerCache.getPlayerByCtx(context);
        Integer nowAt = playerByCtx.getNowAt();
        //获取当前场景
        Scene sceneNow = sceneService.getScene(nowAt);
        //进行判断从当前场景是否可以到达目标场景
        if(!sceneNow.getNeighborScene().contains(sceneId)){
            notificationManager.notifyByCtx(context,"这里并不能到达地点"+sceneId,RequestCode.BAD_REQUEST.getCode());
            return;
        }
        Scene whileGo = sceneService.getScene(sceneId);
        if (!Objects.isNull(whileGo)) {
            //移除旧场景中的化身
            sceneNow.getPlayers().remove(playerByCtx.getPlayerId());
            //更新player缓存中的玩家信息
            playerByCtx.setNowAt(sceneId);
            PlayerBeCache playerByCtx1 = playerCache.getPlayerByCtx(context);
            log.info(playerByCtx1.toString());
            //在新场景中加入化身
            whileGo.getPlayers().put(playerByCtx.getPlayerId(), playerByCtx);
            notificationManager.notifyByCtx(context, "你已在场景：" + whileGo.getName(), RequestCode.ABOUT_SCENE.getCode());
        } else {
            notificationManager.notifyByCtx(context, "输入的场景名称错误", RequestCode.BAD_REQUEST.getCode());
        }
    }

    /**
     * 通过context获取角色
     * @param context
     * @return
     */
    public PlayerBeCache getPlayerByContext(ChannelHandlerContext context) {
        return playerCache.getPlayerByCtx(context);
    }

    /**
     * 查看当前角色的技能状况
     * @param context
     */
    public void seePlayerSkill(ChannelHandlerContext context){
        PlayerBeCache player = getPlayerByContext(context);
        //获取对应类型的所有技能
        Map<Integer, Skill> skillMap = roleTypeService.getRoleTypeById(player.getRoleClass()).getSkillMap();
    }
}
