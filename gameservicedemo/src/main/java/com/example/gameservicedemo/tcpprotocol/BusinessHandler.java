package com.example.gameservicedemo.tcpprotocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

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
public class BusinessHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof List){
            log.info("这是一个List:"+(List)msg);
        }else if (msg instanceof Map){
            log.info("这是一个Map:"+(Map)msg);
        }else{
            log.info("这是一个对象："+msg.getClass().getName());
            log.info("这是一个对象："+msg);
        }
    }
}
