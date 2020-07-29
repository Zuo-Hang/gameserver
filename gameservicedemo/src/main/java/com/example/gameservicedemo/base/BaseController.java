package com.example.gameservicedemo.base;

import com.example.commondemo.entity.command.BaseCommand;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/29/18:06
 * @Description:控制器
 */
public interface BaseController {
    /**
     * 接收派发的数据，处理业务
     * @param ctx
     * @param command
     */
    void handle(ChannelHandlerContext ctx, BaseCommand command);
}
