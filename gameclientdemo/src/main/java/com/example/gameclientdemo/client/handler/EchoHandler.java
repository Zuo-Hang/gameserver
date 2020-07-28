package com.example.gameclientdemo.client.handler;

import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.example.commondemo.entity.ReturnUser;
import com.example.commondemo.entity.TcpProtocol;
import com.example.commondemo.entity.UserCreat;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/28/16:18
 * @Description:客户端处理器
 */
public class EchoHandler extends ChannelInboundHandlerAdapter {
    //连接成功后发送消息测试
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        UserCreat userCreat=new UserCreat();
        userCreat.setCommend("creat");
        userCreat.setNickName("hang hang");
        userCreat.setPassword("123456");
        userCreat.setPhoneNumber("15389425034");
        byte[] encode = ProtobufProxy.create(UserCreat.class).encode(userCreat);
        TcpProtocol protocol=new TcpProtocol();
        protocol.setClassLen((byte)userCreat.getClass().getName().getBytes().length);
        protocol.setLen(encode.length);
        protocol.setClassName(userCreat.getClass().getName().getBytes());
        protocol.setData(encode);
        ctx.write(protocol);//由于设置了编码器，这里直接传入自定义的对象
        ctx.flush();
        ReturnUser returnUser = new ReturnUser();
        returnUser.setId(1);
        returnUser.setName("kkkkk");
        ArrayList<String> objects = new ArrayList<>();
        objects.add("喜羊羊");
        objects.add("懒洋洋");
        returnUser.setStringList(objects);
        byte[] encode1 = ProtobufProxy.create(ReturnUser.class).encode(returnUser);
        TcpProtocol protocol1=new TcpProtocol();
        protocol1.setClassLen((byte)returnUser.getClass().getName().getBytes().length);
        protocol1.setLen(encode1.length);
        protocol1.setClassName(returnUser.getClass().getName().getBytes());
        protocol1.setData(encode1);
        ctx.write(protocol1);
        ctx.flush();
    }
}
