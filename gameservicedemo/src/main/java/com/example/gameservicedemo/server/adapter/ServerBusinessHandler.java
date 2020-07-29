package com.example.gameservicedemo.server.adapter;

import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.example.commondemo.entity.Message;
import com.example.commondemo.entity.RequestCode;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/28/16:13
 * @Description:
 */
@Slf4j
@Component
public class ServerBusinessHandler extends ChannelInboundHandlerAdapter {
    /**
     *  当客户端连上服务器的时候触发此函数
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端: " + ctx.channel().id() + " 加入连接");
        Message message = new Message(RequestCode.SUCCESS.getCode(),"服务器连接成功！");
        ctx.writeAndFlush(message);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            log.info("这是一个{}对象",msg.getClass().getName());
            log.info("这个对象是{}",msg);
    }




}
