package com.example.gameservicedemo.base;

import com.example.commondemo.entity.command.BaseCommand;
import com.example.gameservicedemo.manager.NotificationManager;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

/**
 * @author gonefuture  gonefuture@qq.com
 * time 2018/9/27 9:33
 * @version 1.00
 * Description: 错误命令处理
 */
@Slf4j
@Component
public class ErrorController implements BaseController {
    @Override
    public void handle(ChannelHandlerContext ctx, BaseCommand command) {
        log.debug("请求的服务不存在,命令码为 {}",command.getServiceCode());
        NotificationManager.notifyByCtx(ctx,"你请求的服务不存在..");
    }
}
