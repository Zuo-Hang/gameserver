package com.example.gameservicedemo.game.controller;

import com.example.commondemo.base.Command;
import com.example.commondemo.message.Message;
import com.example.gameservicedemo.base.ControllerManager;
import com.example.gameservicedemo.game.service.PlayerService;
import com.example.gameservicedemo.game.service.UserService;
import com.example.gameservicedemo.game.service.bean.UserBeCache;
import com.example.gameservicedemo.manager.NotificationManager;
import com.example.gameservicedemo.util.CheckParametersUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/05/16:01
 * @Description: 化身控制器
 */
@Slf4j
@Component
public class PlayerController {
    @Resource
    PlayerService playerService;
    @Resource
    UserService userService;

    {
        ControllerManager.add(Command.PLAYER_CREATE.getRequestCode(), this::playerCreate);
        ControllerManager.add(Command.PLAYER_LOGIN.getRequestCode(), this::playerLogin);
    }

    /**
     * 创建新的玩家化身
     *
     * @param ctx
     * @param message
     */
    public void playerCreate(ChannelHandlerContext ctx, Message message) {
        UserBeCache userByCxt = userService.getUserByCxt(ctx);
        if (Objects.isNull(userByCxt)) {
            NotificationManager.notifyByCtx(ctx, "创建角色前需要登陆账号");
            return;
        }
        //指令 化身名称
        String[] array = CheckParametersUtil.checkParameter(ctx, message, 2);
        playerService.playerCreat(ctx, array[1]);
    }

    /**
     * 登录玩家
     *
     * @param ctx
     * @param message
     */
    public void playerLogin(ChannelHandlerContext ctx, Message message) {
        //指令 化身id
        String[] array = CheckParametersUtil.checkParameter(ctx, message, 2);
        try {
            int id = Integer.parseInt(array[1]);
            //判断当前用户是否在线、当前用户是否拥有待登录角色、登录成功后应当返回当前角色的状态（位置）
            playerService.playerLogin(ctx, id);
        } catch (Exception e) {
            NotificationManager.notifyByCtx(ctx, "输入的id错误");
        }
    }
}
