package com.example.gameservicedemo.game.user.service;

import com.example.commondemo.base.RequestCode;
import com.example.gamedatademo.bean.Player;
import com.example.gamedatademo.bean.User;
import com.example.gamedatademo.mapper.PlayerMapper;
import com.example.gamedatademo.mapper.UserMapper;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.cache.PlayerCache;
import com.example.gameservicedemo.game.player.service.PlayerLoginService;
import com.example.gameservicedemo.game.player.service.PlayerService;
import com.example.gameservicedemo.game.user.cache.UserCache;
import com.example.gameservicedemo.game.user.bean.UserBeCache;
import com.example.gameservicedemo.manager.NotificationManager;
import com.example.gameservicedemo.game.player.service.RoleTypeService;
import com.example.gameservicedemo.game.scene.service.SceneService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/30/15:47
 * @Description:
 */
@Slf4j
@Service
public class UserService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    PlayerMapper playerMapper;
    @Autowired
    UserCache userCache;
    @Autowired
    PlayerCache playerCache;
    @Autowired
    PlayerService playerService;
    @Autowired
    RoleTypeService roleTypeService;
    @Autowired
    SceneService sceneService;
    @Autowired
    PlayerLoginService playerLoginService;
    @Autowired
    NotificationManager notificationManager;

    /**
     * 注册用户
     *
     * @param user
     * @return
     */
    public void registerUser(ChannelHandlerContext ctx, User user) {
        String password = user.getPassword();
        String md5Str = DigestUtils.md5DigestAsHex(password.getBytes());
        user.setPassword(md5Str);
        //信息校验
        Integer insert = userMapper.insert(user);
        log.info(user.toString());
        //通知用户
        String format = MessageFormat.format("你已成功注册！你的登录账号是：{1}，快使用\" login 账号 密码 \"去登录吧", user.getUserId());
        notificationManager.notifyByCtx(ctx, format, RequestCode.SUCCESS.getCode());
    }

    /**
     * 用户登录
     *
     * @param ctx      上下文
     * @param userId   用户id
     * @param password 密码
     */
    public void userLogin(ChannelHandlerContext ctx, Integer userId, String password) {
        //先查看缓存，再查找数据库，在返回之前将从数据库取出的数据放入缓存。
        //先查看数据库中是否存在此用户
        UserBeCache userByUserId = userCache.getUserByUserId(userId);
        //对象即可能是 null 也可能是非 null，使用 ofNullable() 方法：
        UserBeCache userBeCache1 = Optional.ofNullable(userByUserId)
                //orElseGet这个方法会在有值的时候返回值，如果没有值，它会执行作为参数传入的 Supplier(供应者) 函数式接口，并将返回其执行结果：
                .orElseGet(() -> {
                            //查询数据库
                            log.info("userId={}", userId.toString());
                            User user = userMapper.selectByUserId(userId);
                            log.info(user.toString());
                            log.info(user.toString());
                            return Optional.ofNullable(user)
                                    //map() 对值应用(调用)作为参数的函数，然后将返回的值包装在 Optional 中
                                    .map(
                                            tU -> {
                                                UserBeCache userBeCache = new UserBeCache();
                                                log.info(userBeCache.toString());
                                                BeanUtils.copyProperties(user, userBeCache);
                                                return userBeCache;
                                            }
                                    ).orElse(null);
                        }
                );
        if (Objects.isNull(userBeCache1)) {
            notificationManager.notifyByCtx(ctx, "用户id不存在", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //将密码加密，//对密码加密，后匹配数据库中的密码
        String md5Str = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!userBeCache1.getPassword().equals(md5Str)) {
            notificationManager.notifyByCtx(ctx, "密码或用户名错误", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        userCache.putCtxUser(ctx, userBeCache1);
        userCache.putUserIdCtx(userBeCache1.getUserId(), ctx);
        userBeCache1.setContext(ctx);
        notificationManager.notifyByCtx(ctx, MessageFormat.format(
                "用户登录成功，可以使用指令 load 加载当前账户下的角色吧", userBeCache1.getNickName()
        ), RequestCode.SUCCESS.getCode());
    }

    /**
     * 用户退出登录
     *
     * @param userId 用户id
     */
    public void logoutUser(ChannelHandlerContext context, Integer userId) {
        /** 先判断用户是否登录 */
        if (!isLogin(userId)) {
            notificationManager.notifyByCtx(context, "对不起，此用户并没有进行登录！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //先将player退出，
        PlayerBeCache playerByContext = playerLoginService.getPlayerByContext(context);
        Optional.ofNullable(playerByContext).ifPresent(p -> playerLoginService.logoutScene(context));
        //从用户的缓存中移除，
        userCache.removeUserByChannelId(context.channel().id().asLongText());
        userCache.removeCtxByUserId(userId);
        notificationManager.notifyByCtx(context, "退出成功，期待再次登录", RequestCode.SUCCESS.getCode());
        //关闭连接
        //context.close();
    }

    /**
     * 判断指定用户是否登录
     *
     * @param userId
     * @return
     */
    public boolean isLogin(Integer userId) {
        //如果没有与userId相对应的上下文则证明该用户并没有登录
        return !Objects.isNull(userCache.getCtxByUserId(userId));
    }

    /**
     * 通过连接上下文找到用户
     */
    public UserBeCache getUserByCxt(ChannelHandlerContext ctx) {
        return userCache.getUserByCtx(ctx);
    }

    /**
     * 判断当前会话是否有用户登录
     *
     * @param ctx
     * @return
     */
    public boolean isUserOnline(ChannelHandlerContext ctx) {
        return userCache.getUserByCtx(ctx) != null;
    }

    /**
     * 获取用户id下的所有角色-----------------------------未作null处理
     *
     * @param userId
     * @return
     */
    public List<Player> findPlayers(ChannelHandlerContext context, Integer userId) {
        //校验用户是否存在
        User user = userMapper.selectByUserId(userId);
        List<Player> players = null;
        if(Objects.isNull(user)){
            notificationManager.notifyByCtx(context, "此用户不存在！", RequestCode.BAD_REQUEST.getCode());
            return null;
        }
        players = playerMapper.selectByUserId(userId);
        return players;
    }

    /**
     * 断线重连
     * 用户掉线的处理：
     * 首先在服务端需要进行cache的有：<userId,context>、<context,user>、<playerId,context>、<context,player>。
     * 客户端登录后只需保存userId，就可以实现断线重连。
     * 服务端接收到重连请求后，根据userId在<userId,context>中查找到旧的context，并更新新的contex。再根据旧的contex查找
     * 到player移除并重新添加。根据playerId将<playerId,context>中的contex进行更新。即实现了断线重连。
     * @param context 新的context
     * @param userId 掉线的用户id
     */
    public void reconnection(ChannelHandlerContext context, Integer userId) {
        ChannelHandlerContext ctxByUserId = userCache.getCtxByUserId(userId);
        if(Objects.isNull(ctxByUserId)){
            //证明此用户并未在线
            notificationManager.notifyByCtx(context,"此用户之前并未在线，你可以使用\"login\"命令进行登录",RequestCode.BAD_REQUEST.getCode());
        }else{
            UserBeCache userByCtx = userCache.getUserByCtx(ctxByUserId);
            userCache.removeUserByChannelId(ctxByUserId.channel().id().asLongText());
            userCache.putCtxUser(context,userByCtx);
            userCache.putUserIdCtx(userId,context);
            log.info("用户缓存更新完毕");
            PlayerBeCache playerByCtx = playerCache.getPlayerByChannel(ctxByUserId.channel());
            if(Objects.isNull(playerByCtx)){
                log.info("此用户断线前并未加载角色");
            }else{
                playerCache.removePlayerByChannelId(playerCache.getCxtByPlayerId(playerByCtx.getId()).channel());
                playerCache.putCtxPlayer(context.channel(),playerByCtx);
                playerCache.savePlayerCtx(playerByCtx.getPlayerId(),context);
                log.info("加载的角色缓存更新完毕！");
            }
            notificationManager.notifyByCtx(context,"你的信息已经重新加载完毕，继续你的操作吧！",RequestCode.SUCCESS.getCode());
        }
    }



    /**
     * 查看当前账户下的所有角色
     *
     * @param context
     */
    public void seeMyPlayer(ChannelHandlerContext context) {
        UserBeCache userByCtx = userCache.getUserByCtx(context);
        if (!Objects.isNull(userByCtx)) {
            List<Player> players = findPlayers(context, userByCtx.getUserId());
            StringBuilder stringBuilder = new StringBuilder("您的角色信息如下：\n");
            if (players == null) {
//-----------------------------------------------------------------------------------
            } else {
                for (Player player : players) {
                    stringBuilder.append("角色id："+
                            player.getPlayerId() + " 角色名："
                                    + player.getPlayerName() + " "
                                    + roleTypeService.getRoleTypeById(player.getRoleClass()).getName() + " 当前位置："
                                    + sceneService.getScene(player.getNowAt()).getName()+"\n"
                    );
                }
                notificationManager.notifyByCtx(context, stringBuilder.toString()+"你可以使用\"load\"加载角色开始游戏", RequestCode.SUCCESS.getCode());
            }
        } else {
            notificationManager.notifyByCtx(context, "您还未登录！请使用 \" login\"进行登录后再进行此操作", RequestCode.BAD_REQUEST.getCode());
        }

    }

}
