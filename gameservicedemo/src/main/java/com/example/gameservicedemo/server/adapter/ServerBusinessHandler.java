package com.example.gameservicedemo.server.adapter;

import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.example.commondemo.entity.Message;
import com.example.commondemo.entity.RequestCode;
import com.example.commondemo.entity.command.BaseCommand;
import com.example.gameservicedemo.base.BaseController;
import com.example.gameservicedemo.base.ControllerManager;
import com.example.gameservicedemo.base.ErrorController;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/28/16:13
 * @Description:服务端业务处理器
 */
@Slf4j
@Component
public class ServerBusinessHandler extends ChannelInboundHandlerAdapter {
    /**
     *  当客户端连上服务器的时候触发此函数
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端: " + ctx.channel().id() + " 加入连接");
        Message message = new Message(RequestCode.SUCCESS.getCode(),"服务器连接成功！");
        ctx.writeAndFlush(message);
    }

    /**
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        int serviceCode = ((BaseCommand) msg).getServiceCode();
        // 如果发送的是心跳，直接无视
        if(serviceCode==0){
            return;
        }
        log.info("这是一个{}对象",msg.getClass().getName());
        log.info("这个对象是{}",msg);
        BaseController controller = new ControllerManager().getController(serviceCode);
        //如果没有对应的controller
        if (controller == null) {
            new ErrorController().handle(ctx,(BaseCommand) msg);
        } else {
            new ControllerManager().execute(controller,ctx,(BaseCommand) msg);
        }
    }


    /**
     *  玩家意外退出时保存是数据
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)  {
        log.error("服务器内部发生错误");
//        NotificationManager.notifyByCtx(ctx,"出现了点小意外"+cause.getMessage());
//
//        // 将角色信息保存到数据库
//        playerQuitService.savePlayer(ctx);

        log.error("发生错误 {}", cause.getMessage());

        // 打印错误
        cause.printStackTrace();
    }

    /**
     * 当客户端断开连接的时候触发函数
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        ctx.writeAndFlush("正在断开连接");

//        // 将角色信息保存到数据库
//        playerQuitService.savePlayer(ctx);
//
//        // 清除缓存
//        playerQuitService.cleanPlayerCache(ctx);
        log.info("客户端: " + ctx.channel().id() + " 已经离线");

    }

}
