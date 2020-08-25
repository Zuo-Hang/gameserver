package com.example.gameservicedemo.base.controller;


import com.example.commondemo.base.RequestCode;
import com.example.commondemo.message.Message;
import com.example.gameservicedemo.manager.NotificationManager;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author gonefuture  gonefuture@qq.com
 * time 2018/9/27 9:33
 * @version 1.00
 * Description: 错误命令处理
 */
@Slf4j
@Component
public class ErrorController implements BaseController {
    @Autowired
    NotificationManager notificationManager;
    @Override
    public void handle(ChannelHandlerContext ctx, Message message) {
        log.debug("请求的服务不存在,命令码为 {}",message.getRequestCode());
        notificationManager.notifyByCtx(ctx,"你请求的服务不存在..", RequestCode.BAD_REQUEST.getCode());
    }
}
