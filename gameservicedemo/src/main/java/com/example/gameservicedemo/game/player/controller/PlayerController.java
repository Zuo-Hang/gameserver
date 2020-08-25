package com.example.gameservicedemo.game.player.controller;

import com.example.commondemo.base.Command;
import com.example.commondemo.base.RequestCode;
import com.example.commondemo.message.Message;
import com.example.gameservicedemo.base.controller.ControllerManager;
import com.example.gameservicedemo.game.player.service.PlayerDataService;
import com.example.gameservicedemo.game.player.service.PlayerLoginService;
import com.example.gameservicedemo.game.player.service.PlayerService;
import com.example.gameservicedemo.game.scene.service.SceneService;
import com.example.gameservicedemo.game.user.service.UserService;
import com.example.gameservicedemo.game.user.bean.UserBeCache;
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
    @Autowired
    PlayerLoginService playerLoginService;
    @Autowired
    PlayerDataService playerDataService;
    @Resource
    UserService userService;
    @Autowired
    SceneService sceneService;
    @Autowired
    NotificationManager notificationManager;

    {
        ControllerManager.add(Command.PLAYER_CREATE.getRequestCode(), this::playerCreate);
        ControllerManager.add(Command.PLAYER_LOGIN.getRequestCode(), this::playerLogin);
        ControllerManager.add(Command.PLAYER_EXIT.getRequestCode(), this::playerExit);
        ControllerManager.add(Command.AOI.getRequestCode(), this::aoi);
        ControllerManager.add(Command.CAM_MOVE.getRequestCode(), this::canMove);
        ControllerManager.add(Command.MOVE.getRequestCode(), this::Move);
        ControllerManager.add(Command.SEE_PLAYER_SKILL.getRequestCode(),this::seePlayerSkill);
        ControllerManager.add(Command.SEE_PLAYER_BAG.getRequestCode(),this::seePlayerBag);
        ControllerManager.add(Command.SEE_PLAYER_EQU.getRequestCode(),this::seePlayerEqu);
        ControllerManager.add(Command.SEE_PLAYER_ABILITY.getRequestCode(),this::seePlayerAbility);
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
        //指令 化身名称  角色类型
        String[] array = CheckParametersUtil.checkParameter(ctx, message, 3);
        playerLoginService.playerCreat(ctx, array[1],Integer.valueOf(array[2]));
    }

    //-------------------------------------------------------------------------下面所有的操作都要判断角色是否登录
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
        if (userService.isUserOnline(ctx) && playerLoginService.hasPlayer(ctx, id)) {
            log.info("playerLogin over");
            playerLoginService.playerLogin(ctx, id);
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
        playerLoginService.logoutScene(ctx);
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
        sceneService.canMove(ctx);
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
        sceneService.move(context, sceneId);

    }

    /**
     * 查看当前角色的技能状况
     * @param context
     * @param message
     */
    public void seePlayerSkill(ChannelHandlerContext context,Message message){
        CheckParametersUtil.checkParameter(context,message,1);
        playerDataService.seePlayerSkill(context);
    }

    /**
     * 查看背包当前状况
     * @param context
     * @param message
     */
    public void seePlayerBag(ChannelHandlerContext context,Message message){
        CheckParametersUtil.checkParameter(context,message,1);
        playerDataService.seePlayerBag(context);
    }

    /**
     * 查看当前装备栏信息
     * @param context
     * @param message
     */
    public void seePlayerEqu(ChannelHandlerContext context,Message message){
        CheckParametersUtil.checkParameter(context,message,1);
        playerDataService.seePlayerEquipmentBar(context);
    }

    /**
     * 查看角色属性
     * @param context
     * @param message
     */
    public void seePlayerAbility(ChannelHandlerContext context,Message message){
        CheckParametersUtil.checkParameter(context,message,1);
        playerDataService.seePlayerAbility(context);



    }
}
