package com.example.commondemo.code;

import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.example.commondemo.base.CommandCode;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/28/16:11
 * @Description:对客户端发起的命令的解码处理器 1.获取数据包的长度
 * 2.获取服务编码
 * 3.获取数据
 */

@Slf4j
public class DecoderHandler extends ByteToMessageDecoder {
    /**最小的数据长度：开头标准位1字节
     *
     */
    private static int MIN_DATA_LEN = 4;
    private CommandCode commandCode=new CommandCode();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        if (in.readableBytes() >= 4) {
            log.debug("开始解码数据……");
            //标记读操作的指针
            in.markReaderIndex();
            //读取长度
            int len = in.readInt();
            if (len <= in.readableBytes()) {
                //读取服务码
                int serviceCode = in.readInt();
                byte[] data = new byte[len - 4];
                in.readBytes(data);
                try {
                    //根据serviceCode获取到类型
                    String s = commandCode.CLASS_MAP.get(serviceCode);
                    Class<?> aClass = Class.forName(s);
                    Object decode = ProtobufProxy.create(aClass).decode(data);
                    out.add(decode);
                } catch (Exception e) {

                }
                //如果out有值，且in仍然可读，将继续调用decode方法再次解码in中的内容，以此解决粘包问题

            } else {
                log.debug(String.format("数据长度不够，数据协议len长度为：%1$d,数据包实际可读内容为：%2$d。正在等待处理拆包……", len, in.readableBytes()));
                in.resetReaderIndex();
                /*
                 **结束解码，这种情况说明数据没有到齐，在父类ByteToMessageDecoder的callDecode中会对out和in进行判断
                 * 如果in里面还有可读内容即in.isReadable位true,cumulation中的内容会进行保留，，直到下一次数据到来，将两帧的数据合并起来，再解码。
                 * 以此解决拆包问题
                 */
                return;
            }
        } else {
            log.debug("数据长度不符合要求，期待最小长度是：" + MIN_DATA_LEN + " 字节");
            return;
        }
    }
}
