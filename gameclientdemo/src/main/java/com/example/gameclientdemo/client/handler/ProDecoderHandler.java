package com.example.gameclientdemo.client.handler;

import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.example.commondemo.entity.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/29/16:11
 * @Description:
 */
public class ProDecoderHandler extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        //System.out.println("服务端: ");
        byte [] data=new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(data);
        Message decode = ProtobufProxy.create(Message.class).decode(data);
        list.add(decode);
    }
}
