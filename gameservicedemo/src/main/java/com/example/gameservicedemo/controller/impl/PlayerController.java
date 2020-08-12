package com.example.gameservicedemo.controller.impl;

import com.example.commondemo.base.Command;
import com.example.commondemo.base.RequestCode;
import com.example.commondemo.message.Message;
import com.example.gameservicedemo.controller.ControllerManager;
import com.example.gameservicedemo.service.PlayerService;
import com.example.gameservicedemo.service.UserService;
import com.example.gameservicedemo.bean.UserBeCache;
import com.example.gameservicedemo.manager.NotificationManager;
import com.example.gameservicedemo.util.CheckParametersUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    NotificationManager notificationManager;

    {
        ControllerManager.add(Command.PLAYER_CREATE.getRequestCode(), this::playerCreate);
        ControllerManager.add(Command.PLAYER_LOGIN.getRequestCode(), this::playerLogin);
        ControllerManager.add(Command.PLAYER_EXIT.getRequestCode(), this::playerExit);
        ControllerManager.add(Command.AOI.getRequestCode(), this::aoi);
        ControllerManager.add(Command.CAM_MOVE.getRequestCode(), this::canMove);
        ControllerManager.add(Command.MOVE.getRequestCode(), this::Move);
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
            notificationManager.notifyByCtx(ctx, "创建角色前需要登陆账号", RequestCode.NOT_LOGIN.getCode());
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
        int id = Integer.valueOf(array[1]);
        //判断当前用户是否在线、当前用户是否拥有待登录角色、登录成功后应当返回当前角色的状态（位置）
        if (userService.isUserOnline(ctx) && playerService.hasPlayer(ctx, id)) {
            log.info("playerLogin over");
            playerService.playerLogin(ctx, id);
        } else {
            notificationManager.notifyByCtx(ctx, "输入的id错误",RequestCode.BAD_REQUEST.getCode());
        }
    }

    /**
     * 玩家退出
     *
     * @param ctx
     * @param message
     */
    public void playerExit(ChannelHandlerContext ctx, Message message) {
        //判断是否已登录，已加载化身
        //……
        playerService.logoutScene(ctx);
    }

    /**
     * AOI(Area Of Interest)，即感兴趣区域
     *
     * @param ctx
     * @param message
     */
    public void aoi(ChannelHandlerContext ctx, Message message) {
        playerService.aoi(ctx);
    }

    /**
     * 获取可移动的范围
     *
     * @param ctx
     * @param message
     */
    public void canMove(ChannelHandlerContext ctx, Message message) {
        playerService.canMove(ctx);
    }

    /**
     * 玩家移动
     *
     * @param context
     * @param message
     */
    public void Move(ChannelHandlerContext context, Message message) {
        String[] strings = CheckParametersUtil.checkParameter(context, message, 2);
        Integer sceneId = Integer.valueOf(strings[1]);
        playerService.move(context, sceneId);

    }
}
