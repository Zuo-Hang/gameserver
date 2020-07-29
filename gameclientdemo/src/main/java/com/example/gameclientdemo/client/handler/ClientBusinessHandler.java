package com.example.gameclientdemo.client.handler;

import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
//import com.example.commondemo.entity.ReturnUser;
import com.example.commondemo.entity.Message;
import com.example.commondemo.entity.TcpProtocol;
import com.example.commondemo.entity.command.UserCreat;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

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
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        UserCreat userCreat=new UserCreat();
        userCreat.setNickName("hang hang");
        userCreat.setPassword("123456");
        userCreat.setPhoneNumber("15389425034");
        byte[] encode = ProtobufProxy.create(UserCreat.class).encode(userCreat);
        TcpProtocol protocol=new TcpProtocol();
        protocol.setClassLen((byte)userCreat.getClass().getName().getBytes().length);
        protocol.setLen(encode.length);
        protocol.setClassName(userCreat.getClass().getName().getBytes());
        protocol.setData(encode);
        //由于设置了编码器，这里直接传入自定义的对象
        ctx.write(protocol);
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof Message){
            log.info("服务器返回了:{}",((Message) msg).getMessage());
        }
    }

}
