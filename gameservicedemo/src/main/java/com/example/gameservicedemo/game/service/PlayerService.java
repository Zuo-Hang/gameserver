package com.example.gameservicedemo.game.service;

import com.example.gamedatademo.bean.Player;
import com.example.gamedatademo.bean.Role;
import com.example.gamedatademo.bean.Scene;
import com.example.gamedatademo.mapper.PlayerMapper;
import com.example.gamedatademo.mapper.RoleMapper;
import com.example.gamedatademo.mapper.SceneMapper;
import com.example.gameservicedemo.cache.PlayerCache;
import com.example.gameservicedemo.cache.UserCache;
import com.example.gameservicedemo.game.service.bean.PlayerBeCache;
import com.example.gameservicedemo.game.service.bean.UserBeCache;
import com.example.gameservicedemo.manager.NotificationManager;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    SceneMapper sceneMapper;
    @Autowired
    RoleMapper roleMapper;
    @Autowired
    UserService userService;
    @Autowired
    PlayerCache playerCache;
    @Autowired
    NotificationManager notificationManager;
    @Autowired
    UserCache userCache;

    /**
     * 创建玩家
     *
     * @param context
     * @param playerName
     */
    public void playerCreat(ChannelHandlerContext context, String playerName) {
        UserBeCache userByCtx = userCache.getUserByCtx(context);
        int userId = userByCtx.getUserId();
        Player player = new Player();
        player.setPlayerName(playerName);
        player.setUserId(userId);
        player.setNowAt(1);
        Integer insert = playerMapper.insert(player);
        log.info("成功创建角色{}", player.toString());
        notificationManager.notifyByCtx(context, "你已成功创建角色：" + playerName + "，快使用 load 命令去登录吧");
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
            notificationManager.notifyByCtx(context, "此操作无效");
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
        playerCache.putCtxPlayer(context, playerBeCache);
        // 保存playerId跟ChannelHandlerContext之间的关系
        playerCache.savePlayerCtx(playerId, context);
        playerBeCache.setContext(context);
        result.append(playerBeCache.getPlayerName()).append(",角色登陆成功")
                .append("\n 你所在位置为: ")
                .append(playerBeCache.getNowAt()).append("\n");
        result.append("\n").append("使用指令 `aoi` 可查看周围环境");
        notificationManager.notifyByCtx(context, result.toString());
    }

    /**
     * 判断对应上下文中的用户是否拥有这个角色
     *
     * @param ctx      上下文
     * @param playerId 要判定的角色id
     * @return 用户是否拥有此角色
     */
    public boolean hasPlayer(ChannelHandlerContext ctx, Integer playerId) {
        UserBeCache user = userCache.getUserByCtx(ctx);
        List<Player> players = userService.findPlayers(user.getUserId());
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
                            MessageFormat.format("玩家 {0} 正在退出", playerByCtx.getPlayerName()));
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
                }
        );

    }

    /**
     * 获取周边环境
     *
     * @param context
     */
    public void aoi(ChannelHandlerContext context) {
        //获取当前场景的所有实体信息
        PlayerBeCache playerByCtx = playerCache.getPlayerByCtx(context);
        Integer nowAt = playerByCtx.getNowAt();
        Scene scene = sceneMapper.selectBySceneId(nowAt);
        String sceneObjects = scene.getSceneObjects();
        if("".equals(sceneObjects)){
            notificationManager.notifyByCtx(context, "这里空无一人！");
            return;
        }
        String[] sceneObjectsId = sceneObjects.split(",");
        StringBuffer ret = new StringBuffer();
        ret.append("\nnpc如下：");
        if (sceneObjectsId != null) {
            for (String objectsId : sceneObjectsId) {
                Role role = roleMapper.selectByRoleId(Integer.valueOf(objectsId));
                String live = role.getState() == 0 ? "死亡\n" : "存活\n";
                ret.append("角色：" + role.getRoleName() + " " + live);
            }
        }
        notificationManager.notifyByCtx(context, ret.toString());
    }

    /**
     * 获取可以移动到的位置
     *
     * @param context
     */
    public void canMove(ChannelHandlerContext context) {
        PlayerBeCache playerByCtx = playerCache.getPlayerByCtx(context);
        Integer nowAt = playerByCtx.getNowAt();
        Scene scene = sceneMapper.selectBySceneId(nowAt);
        String adjacentScenes = scene.getAdjacentScenes();
        String[] adjacentScenesId = adjacentScenes.split(",");
        StringBuffer ret = new StringBuffer();
        ret.append("\n相邻场景如下：");
        for (String objectsId : adjacentScenesId) {
            Scene scene1 = sceneMapper.selectBySceneId(Integer.valueOf(objectsId));
            String sceneInformation = scene1.getSceneName() + " " + scene1.getSceneDescribe();
            ret.append("场景：" + sceneInformation);
        }
        notificationManager.notifyByCtx(context, ret);
    }

    /**
     * 化身移动
     *
     * @param context   上下文
     * @param sceneName 将移动到的场景名称
     */
    public void move(ChannelHandlerContext context, String sceneName) {
        //先判断名称是否存在
        //获取当前化身，并设置自身位置，更新数据库当中的场景信息
        PlayerBeCache playerByCtx = playerCache.getPlayerByCtx(context);
        Integer nowAt = playerByCtx.getNowAt();
        Scene scene = sceneMapper.selectBySceneName(sceneName);
        if (!Objects.isNull(scene)) {
            //移除旧场景中的化身
            Scene scene1 = sceneMapper.selectBySceneId(nowAt);
            String[] split = scene1.getSceneObjects().split(",");
            StringBuffer stringBuffer1 = new StringBuffer();
            for (String string : split) {
                if (!string.equals(playerByCtx.getPlayerId().toString())) {
                    stringBuffer1.append(string + ",");
                }
            }
            scene1.setSceneObjects(stringBuffer1.toString());
            sceneMapper.updateBySceneId(scene1);
            playerByCtx.setNowAt(scene.getSceneId());
            PlayerBeCache playerByCtx1 = playerCache.getPlayerByCtx(context);
            log.info(playerByCtx1.toString());
            //在新场景中加入化身
            StringBuffer stringBuffer = new StringBuffer(scene.getSceneObjects());
            stringBuffer.append(playerByCtx.getPlayerId());
            scene.setSceneObjects(stringBuffer.toString() + ",");
            sceneMapper.updateBySceneId(scene);
            notificationManager.notifyByCtx(context, "你已在场景：" + scene.getSceneName());
        } else {
            notificationManager.notifyByCtx(context, "输入的场景名称错误");
        }
    }
}
