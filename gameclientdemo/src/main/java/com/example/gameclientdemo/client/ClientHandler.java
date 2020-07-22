package com.example.gameclientdemo.client;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @Author hanghang
 * @Date 2020/7/22 15:22
 * @Version 1.0
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client01Handler Active");
        /*若把这一句注释掉将无法将event传递给下一个ClientHandler,例如例子中p.addLast(new Client01Handler())后面紧跟着p.addLast(new Client02Handler())
         后面的Client02Handler里的方法就不会被触发。
        */
        ctx.fireChannelActive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("服务端: "+msg);
        InputStreamReader is = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(is);
        String result ="result";
        try{
            result = br.readLine();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        ctx.write(result);//给服务端回复
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}