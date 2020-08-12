package com.example.gameservicedemo.util;

import com.example.commondemo.base.RequestCode;
import com.example.commondemo.message.Message;
import com.example.gameservicedemo.manager.NotificationManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/04/9:59
 * @Description:对于客户端传过来的命令的参数个数进行校验
 */
@Component
public class CheckParametersUtil {
    @Autowired
    static NotificationManager notificationManager;
    public static String[] checkParameter(ChannelHandlerContext ctx, Message message, int parameterNumber) {
        String[] args = null;
        //按照正则的方式拆分参数字符串
        args = message.getMessage().split("\\s+");
        if (args.length != parameterNumber) {
            notificationManager.notifyByCtx(ctx,"您输入的参数数目不正确，请重新输入", RequestCode.BAD_REQUEST.getCode());
        }
        return args;
    }
}
