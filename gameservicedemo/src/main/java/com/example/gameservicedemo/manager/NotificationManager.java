package com.example.gameservicedemo.manager;

import com.example.commondemo.entity.Message;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/30/15:15
 * @Description:通知管理器
 */
@Slf4j
@Service
public class NotificationManager {
    /**
     *  通过通道上下文来通知玩家
     * @param ctx 上下文
     * @param e 信息
     * @param <E> 信息的类型
     */
    public static  <E> void notifyByCtx(ChannelHandlerContext ctx, E e) {
        Message message = new Message();
        message.setMessage(e.toString()+"\n");
        ctx.writeAndFlush(message);
    }
}
