package com.example.gameservicedemo.tcpprotocol;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;


/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/28/10:20
 * @Description:
 */
@Slf4j
public class TcpServer {
    private  int port;
    public  void init(){
        log.info("正在启动tcp服务器……");
        NioEventLoopGroup boss = new NioEventLoopGroup();//主线程组
        NioEventLoopGroup work = new NioEventLoopGroup();//工作线程组
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();//引导对象
            bootstrap.group(boss,work);//配置工作线程组
            bootstrap.channel(NioServerSocketChannel.class);//配置为NIO的socket通道
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {//绑定通道参数
                    ch.pipeline().addLast("logging",new LoggingHandler("DEBUG"));//设置log监听器，并且日志级别为debug，方便观察运行流程
                    ch.pipeline().addLast("encode",new EncoderHandler());//编码器。发送消息时候用过
                    ch.pipeline().addLast("decode",new DecoderHandler());//解码器，接收消息时候用
                    ch.pipeline().addLast("handler",new BusinessHandler());//业务处理类，最终的消息会在这个handler中进行业务处理
                }
            });
            //缓冲区
            bootstrap.option(ChannelOption.SO_BACKLOG,1024);
            //ChannelOption对象设置TCP套接字的参数，非必须步骤
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE,true);
            //使用了Future来启动线程，并绑定了端口
            ChannelFuture future = bootstrap.bind(port).sync();
            log.info("启动tcp服务器启动成功，正在监听端口:"+port);
            future.channel().closeFuture().sync();//以异步的方式关闭端口

        }catch (InterruptedException e) {
            log.info("启动出现异常："+e);
        }finally {
            work.shutdownGracefully();
            boss.shutdownGracefully();//出现异常后，关闭线程组
            log.info("tcp服务器已经关闭");
        }
    }

    public static void main(String[] args) {
        new TcpServer(8779).init();
    }
    public TcpServer(int port) {
        this.port = port;
    }
}
