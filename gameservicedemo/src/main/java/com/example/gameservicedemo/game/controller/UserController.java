package com.example.gameservicedemo.game.controller;

import com.example.commondemo.command.BaseCommand;
import com.example.gameservicedemo.base.ControllerManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Controller;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/30/15:43
 * @Description:
 */
@Controller
public class UserController{
    /**
     * 在类加载的时候将方法添加到map中。
     */
    {
        ControllerManager.add(1,this::registerUser);
    }

    /**
     * 用户注册的控制器
     * @param ctx
     * @param command
     */
    public void registerUser(ChannelHandlerContext ctx, BaseCommand command){

    }
}
