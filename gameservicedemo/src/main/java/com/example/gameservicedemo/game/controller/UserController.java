package com.example.gameservicedemo.game.controller;


import com.example.commondemo.message.Message;
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
        ControllerManager.add(1,this::registerUser);
        ControllerManager.add(1001,this::userLogin);
    }

    /**
     * 用户注册的控制器
     * @param ctx
     * @param message
     */
    public void registerUser(ChannelHandlerContext ctx, Message message){

    }

    private void userLogin(ChannelHandlerContext ctx, Message message) {
        String[] array = CheckParametersUtil.checkParameter(ctx,message,3);
        Integer userId =  Integer.parseInt(array[1]);
        String password = array[2];
        log.info("UserController收到 userId={},password={}",userId,password);
        userService.userLogin(ctx,userId,password);
    }
}
