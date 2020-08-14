package com.example.gameservicedemo.controller.impl;


import com.example.commondemo.base.Command;
import com.example.commondemo.base.RequestCode;
import com.example.commondemo.message.Message;
import com.example.gamedatademo.bean.User;
import com.example.gameservicedemo.bean.RoleType;
import com.example.gameservicedemo.bean.UserBeCache;
import com.example.gameservicedemo.controller.ControllerManager;
import com.example.gameservicedemo.manager.NotificationManager;
import com.example.gameservicedemo.service.RoleTypeService;
import com.example.gameservicedemo.service.UserService;
import com.example.gameservicedemo.util.CheckParametersUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/30/15:43
 * @Description:
 */
@Slf4j
@Component
public class UserController{
    @Resource
    UserService userService;
    @Autowired
    RoleTypeService roleTypeService;
    @Autowired
    NotificationManager notificationManager;
    /**
     * 在类加载的时候将方法添加到map中。
     */
    {
        ControllerManager.add(Command.USER_CREATE.getRequestCode(),this::userCreate);
        ControllerManager.add(Command.USER_LOGIN.getRequestCode(),this::userLogin);
        ControllerManager.add(Command.USER_LOGOUT.getRequestCode(),this::logoutUser);
        ControllerManager.add(Command.RECONNECTION.getRequestCode(),this::reconnection);
        ControllerManager.add(Command.SEE_MY_PLAYER.getRequestCode(),this::seeMyPlayer);
        ControllerManager.add(Command.SEE_ROLE_TYPE.getRequestCode(),this::seeRoleType);
    }

    /**
     * 用户登录
     * @param ctx
     * @param message
     */
    private void userLogin(ChannelHandlerContext ctx, Message message) {
        //指令  账号  密码
        String[] array = CheckParametersUtil.checkParameter(ctx,message,3);
        Integer userId =  Integer.parseInt(array[1]);
        String password = array[2];
        log.info("UserController收到 userId={},password={}",userId,password);
        userService.userLogin(ctx,userId,password);
    }
    /**
     * 用户注册的控制器
     * @param ctx
     * @param message
     */
    private void userCreate(ChannelHandlerContext ctx, Message message){
        //指令 昵称 密码 电话号码
        String[] args = CheckParametersUtil.checkParameter(ctx,message,4);
        User user = new User();
        user.setNickName(args[1]);
        user.setPassword(args[2]);
        user.setPhoneNumber(args[3]);
        userService.registerUser(ctx,user);
    }

    /**
     * 用户退出登录
     * @param context
     * @param message
     */
    private void logoutUser(ChannelHandlerContext context,Message message){
        //指令 userId
        String[] strings = CheckParametersUtil.checkParameter(context, message, 1);
        //判断当前会话是否有用户登录
        if(userService.isUserOnline(context)){
            UserBeCache userByCxt = userService.getUserByCxt(context);
            userService.logoutUser(context,userByCxt.getUserId());
        }else{
            notificationManager.notifyByCtx(context, "对不起，你还未进行登录！", RequestCode.BAD_REQUEST.getCode());
        }
    }

    /**
     * 客户端断线重连
     * @param context
     * @param message
     */
    private void reconnection(ChannelHandlerContext context,Message message){
        String[] strings = CheckParametersUtil.checkParameter(context, message, 2);
        userService.reconnection(context,Integer.valueOf(strings[1]));
    }

    /**
     * 查看当前账户下的所有已创建角色
     * @param context
     * @param message
     */
    private void seeMyPlayer(ChannelHandlerContext context,Message message){
        userService.seeMyPlayer(context);
    }

    /**
     * 查看可以创建的角色类型种类
     * @param context
     * @param message
     */
    private void seeRoleType(ChannelHandlerContext context,Message message){
        String[] strings = CheckParametersUtil.checkParameter(context, message, 1);
        StringBuilder stringBuilder = new StringBuilder("所有的角色类型信息如下：\n");
        for(RoleType roleType:roleTypeService.getAllRoleType()){
            stringBuilder.append(
                    "类型id："+roleType.getId()+" "+
                    "类型名称："+roleType.getName()+"\n"
            );
        }
        notificationManager.notifyByCtx(context,stringBuilder.toString(),RequestCode.SUCCESS.getCode());
    }
}
