package com.example.gameservicedemo.manager;

import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.example.commondemo.base.TcpProtocol;
import com.example.commondemo.message.Message;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

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
    public static  <E> void notifyByCtx(ChannelHandlerContext ctx, E e){
        Message message = new Message();
        message.setMessage(e.toString()+"\n");
        message.setRequestCode(900);
        byte[] encode = new byte[0];
        try {
            encode = ProtobufProxy.create(Message.class).encode(message);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        TcpProtocol protocol = new TcpProtocol();
        protocol.setServiceCode(200);
        protocol.setData(encode);
        protocol.setLen(encode.length+4);
        ctx.writeAndFlush(protocol);
    }
}
