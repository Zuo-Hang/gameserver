package com.example.gameservicedemo.server.channelInitializer;

import com.example.commondemo.code.DecoderHandler;
import com.example.commondemo.code.EncoderHandler;
import com.example.gameservicedemo.server.adapter.ServerBusinessHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/29/15:44
 * @Description:
 */
@Component
public class ServerSocketChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Resource
    ServerBusinessHandler businessHandler;
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {//绑定通道参数
        //设置log监听器，并且日志级别为debug，方便观察运行流程
        ch.pipeline().addLast("logging",new LoggingHandler("DEBUG"));
        //编码器。发送消息时候用过
        ch.pipeline().addLast("encode",new EncoderHandler());
        //解码器，接收消息时候用
        ch.pipeline().addLast("decode",new DecoderHandler());
        //业务处理类，最终的消息会在这个handler中进行业务处理
        ch.pipeline().addLast("handler",new ServerBusinessHandler());
    }
}
