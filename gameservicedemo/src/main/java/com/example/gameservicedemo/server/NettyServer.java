package com.example.gameservicedemo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @Author hanghang
 * @Date 2020/7/22 15:16
 * @Version 1.0
 */
public class NettyServer {
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        ServerBootstrap bootStrap = new ServerBootstrap();
        ChannelFuture cf;
        bootStrap.group(bossGroup,workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast("decoder", new StringDecoder());//需要编解码，否则无法解析
                        p.addLast("encoder", new StringEncoder());
                        p.addLast(new NettyServerHandler());
                    }
                });

        try {
            cf =  bootStrap.bind(8099).sync();//监听8099端口
            System.out.println("8099:binded...");
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally{
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
