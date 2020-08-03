package com.example.gameservicedemo.game.service;

import com.example.gamedatademo.bean.User;
import com.example.gamedatademo.mapper.UserMapper;
import com.example.gameservicedemo.manager.NotificationManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/30/15:47
 * @Description:
 */
@Service
public class UserService {
    UserMapper userMapper;
    /**
     * 注册用户
     * @param user
     * @return
     */
    public void registerUser(ChannelHandlerContext ctx, User user){
        //信息校验
        Integer insert = userMapper.insert(user);
        //通知用户
        NotificationManager.notifyByCtx(ctx,"你已成功注册！你的登录账号是："+insert);
    }

    /**
     * 用户登录
     * @param ctx 上下文
     * @param userId 用户id
     * @param password 密码
     */
    public void userLogin (ChannelHandlerContext ctx,Integer userId,String password){

    }
    /**
     * 用户注销
     * @param userId    用户id
     */
    public void logoutUser(long userId) {
//
//        ChannelHandlerContext ctx = UserCacheManger.getCtxByUserId(userId);
//        if (ctx != null) {
//            UserCacheManger.removeUserByChannelId(ctx.channel().id().asLongText());
//            // 关闭连接
//            ctx.close();
//        }

    }
}
