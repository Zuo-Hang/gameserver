package com.example.gameclientdemo.client.handler;

import com.example.commondemo.base.RequestCode;
import com.example.commondemo.message.Message;
import com.example.gameclientdemo.client.MainView;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/28/16:18
 * @Description:客户端处理器
 */
@Slf4j
public class ClientBusinessHandler extends ChannelInboundHandlerAdapter {
    /**
     * 连接成功后发送消息测试
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
    }

    /**
     * 读取服务端返回的消息
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof Message){
            String format = MessageFormat.format("服务器返回了:{0}", ((Message) msg).getMessage());
            log.info(format);
            MainView.OUTPUT.append(format + "\n");
            if(((Message) msg).getRequestCode()== RequestCode.ABOUT_PLAYER.getCode()){
                MainView.INFORMATION.setText(((Message) msg).getMessage());
            }
            if(((Message) msg).getRequestCode()== RequestCode.ABOUT_SCENE.getCode()){
                MainView.MAP.setText(((Message) msg).getMessage());
            }
        }
    }

}
