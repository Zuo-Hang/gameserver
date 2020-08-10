package com.example.commondemo.code;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.example.commondemo.message.Message;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/10/21:31
 * @Description: 由于创建Codec编解码器耗费时间，且可全局唯一所以使用单例模式实现
 */
public class GetCoder {
    /** 将instance声明为volatile型 */
    private volatile static Codec<Message> codec;
    public static Codec<Message> getCoder(){
        // 第一次检查
        if(codec==null){
            // 第一次检查为null再进行加锁，降低同步带来的性能开销
            synchronized (GetCoder.class){
                // 第二次检查
                if(codec==null){
                    // 多线程下将禁止2）和3）之间的重排序
                    codec= ProtobufProxy.create(Message.class);
                }
            }
        }
        return codec;
    }
}
