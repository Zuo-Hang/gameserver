package com.example.commondemo.code;

import com.example.commondemo.base.TcpProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/28/16:09
 * @Description:编码处理器
 */
@Slf4j
public class EncoderHandler extends MessageToByteEncoder {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (msg instanceof TcpProtocol){
            TcpProtocol protocol = (TcpProtocol) msg;
            out.writeInt(protocol.getLen());
            out.writeBytes(protocol.getData());
            log.debug("数据编码成功："+out);
        }else {
            log.info("不支持的数据协议："+msg.getClass()+"\t期待的数据协议类是："+ TcpProtocol.class);
        }
    }
}
