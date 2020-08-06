package com.example.gameservicedemo.game.controller;


import com.example.commondemo.base.Command;
import com.example.commondemo.message.Message;
import com.example.gamedatademo.bean.User;
import com.example.gameservicedemo.base.ControllerManager;
import com.example.gameservicedemo.game.service.UserService;
import com.example.gameservicedemo.util.CheckParametersUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

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
    /**
     * 在类加载的时候将方法添加到map中。
     */
    {
        ControllerManager.add(Command.USER_CREATE.getRequestCode(),this::userCreate);
        ControllerManager.add(Command.USER_LOGIN.getRequestCode(),this::userLogin);
    }

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
}
