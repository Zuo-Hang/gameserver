package com.example.gameclientdemo.client.handler;

import com.example.commondemo.entity.TcpProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/28/16:09
 * @Description:
 */
@Slf4j
public class EncoderHandler extends MessageToByteEncoder {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (msg instanceof TcpProtocol){
            TcpProtocol protocol = (TcpProtocol) msg;
            out.writeByte(protocol.getHeader());
            out.writeByte(protocol.getClassLen());
            out.writeInt(protocol.getLen());
            out.writeBytes(protocol.getClassName());
            out.writeBytes(protocol.getData());
            out.writeByte(protocol.getTail());
            log.debug("数据编码成功："+out);
        }else {
            log.info("不支持的数据协议："+msg.getClass()+"\t期待的数据协议类是："+ TcpProtocol.class);
        }
    }
}
