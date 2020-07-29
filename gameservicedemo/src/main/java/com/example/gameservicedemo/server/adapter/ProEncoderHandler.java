package com.example.gameservicedemo.server.adapter;

import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.example.commondemo.entity.Message;
import com.example.commondemo.entity.TcpProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/29/16:54
 * @Description:
 */
@Slf4j
public class ProEncoderHandler extends MessageToByteEncoder {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if(o instanceof Message){
            Message message = (Message) o;
            byte[] encode = ProtobufProxy.create(Message.class).encode(message);
            byteBuf.writeBytes(encode);
            log.debug("数据编码成功：{}",byteBuf);
        }else {
            log.info("不支持的数据协议：{}\t期待的数据协议类是：{}",o.getClass(),Message.class);
        }
    }
}
