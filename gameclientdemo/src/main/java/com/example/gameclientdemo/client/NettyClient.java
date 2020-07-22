package com.example.gameclientdemo.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @Author hanghang
 * @Date 2020/7/22 15:21
 * @Version 1.0
 */
public class NettyClient {
    public static void main(String[] args) throws Exception {
        EventLoopGroup group =new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>(){
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast("decoder", new StringDecoder());
                            p.addLast("encoder", new StringEncoder());
                            p.addLast(new ClientHandler());
                        }
                    });

            ChannelFuture future = b.connect("127.0.0.1", 8099).sync();
            future.channel().writeAndFlush("这里是客户端，请求连接服务端！");
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }

    }
}
