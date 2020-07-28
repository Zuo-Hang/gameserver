package com.example.gameservicedemo.tcpprotocol;

import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
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
 * @Description:
 * 1.解析并验证协议开头标志位0x58
 * 2.解析出类名长度len1和数据组长度len2
 * 3.根据剩余可读位数和len1+len2+1大小处理粘包/拆包
 * 4.读取出类名className
 * 5.读取实际的数据字节组data
 * 6.解析并验证结束标志位
 * 7.根据className去反编译出data，获得传输的实际java实体
 */

@Slf4j
public class DecoderHandler extends ByteToMessageDecoder {
    //最小的数据长度：开头标准位1字节
    private static int MIN_DATA_LEN=6+1+1+1;
    //数据解码协议的开始标志
    private static byte PROTOCOL_HEADER=0x58;
    //数据解码协议的结束标志
    private static byte PROTOCOL_TAIL=0x63;
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        if (in.readableBytes()>MIN_DATA_LEN){
            log.debug("开始解码数据……");
            //标记读操作的指针
            in.markReaderIndex();
            byte header=in.readByte();
            if (header==PROTOCOL_HEADER){
                log.debug("数据开头格式正确");
                //读取类名长度
                int typeLen=in.readByte()&255;
                //读取数据长度
                int dataLen=in.readInt();

                if (typeLen+dataLen<in.readableBytes()){

                    byte [] fullClassName=new byte[typeLen];
                    byte [] data=new byte[dataLen];
                    in.readBytes(fullClassName);
                    in.readBytes(data);
                    //读取结束标志
                    byte tail=in.readByte();
                    try {
                        //获取到类型
                        Class<?> aClass = Class.forName(new String(fullClassName));
                        Object decode = ProtobufProxy.create(aClass).decode(data);
                        out.add(decode);
                    }catch (Exception e){}

                            //如果out有值，且in仍然可读，将继续调用decode方法再次解码in中的内容，以此解决粘包问题

                }else{
                    log.debug(String.format("数据长度不够，数据协议len长度为：%1$d,数据包实际可读内容为：%2$d正在等待处理拆包……",dataLen+typeLen,in.readableBytes()));
                    in.resetReaderIndex();
                    /*
                     **结束解码，这种情况说明数据没有到齐，在父类ByteToMessageDecoder的callDecode中会对out和in进行判断
                     * 如果in里面还有可读内容即in.isReadable位true,cumulation中的内容会进行保留，，直到下一次数据到来，将两帧的数据合并起来，再解码。
                     * 以此解决拆包问题
                     */
                    return;
                }
            }else {
                log.debug("开头不对，可能不是期待的客服端发送的数，将自动略过这一个字节");
            }
        }else {
            log.debug("数据长度不符合要求，期待最小长度是："+MIN_DATA_LEN+" 字节");
            return;
        }

    }
}
