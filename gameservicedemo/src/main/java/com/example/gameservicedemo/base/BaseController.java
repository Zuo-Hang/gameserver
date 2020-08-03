package com.example.gameservicedemo.base;


import com.example.commondemo.message.Message;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/29/18:06
 * @Description:控制器  下面具体的Controller实现，可以将方法传入map 。实现方法与编码的对应。
 */
public interface BaseController {
    /**
     * 接收派发的数据，处理业务
     * @param ctx
     * @param message
     */
    void handle(ChannelHandlerContext ctx, Message message);
}
