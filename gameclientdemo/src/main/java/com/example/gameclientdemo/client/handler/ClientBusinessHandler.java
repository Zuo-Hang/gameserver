package com.example.gameclientdemo.client.handler;

import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.example.commondemo.base.Command;
import com.example.commondemo.message.Message;
import com.example.commondemo.base.TcpProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

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
//        Message message = new Message();
//        message.setRequestCode(Command.AOI.getRequestCode());
//        message.setMessage("creatUser "+"1");
//        byte[] encode = ProtobufProxy.create(Message.class).encode(message);
//        TcpProtocol protocol=new TcpProtocol();
//        protocol.setData(encode);
//        protocol.setLen(encode.length);
//        //由于设置了编码器，这里直接传入自定义的对象
//        ctx.write(protocol);
//        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof Message){
            log.info("服务器返回了:{}",((Message) msg).getMessage());
        }
    }

}
