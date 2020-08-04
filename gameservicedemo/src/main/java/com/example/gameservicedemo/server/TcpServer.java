package com.example.gameservicedemo.server;

import com.example.gameservicedemo.server.channelInitializer.ServerSocketChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/28/10:20
 * @Description:
 */
@Slf4j
@Component
public class TcpServer {
    @Resource
    ServerSocketChannelInitializer serverSocketChannelInitializer;
    private  int port;
    public  void init(){
        log.info("正在启动tcp服务器……");
        //主线程组
        NioEventLoopGroup boss = new NioEventLoopGroup();
        //工作线程组
        NioEventLoopGroup work = new NioEventLoopGroup();
        try {
            //引导对象
            ServerBootstrap bootstrap = new ServerBootstrap();
            //配置工作线程组
            bootstrap.group(boss,work);
            //配置为NIO的socket通道
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ServerSocketChannelInitializer());
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
            //出现异常后，关闭线程组
            boss.shutdownGracefully();
            log.info("tcp服务器已经关闭");
        }
    }

    public void start() {
        new TcpServer(8779).init();

    }

    public TcpServer(int port) {
        this.port = port;
    }

    public TcpServer() {
    }
}
