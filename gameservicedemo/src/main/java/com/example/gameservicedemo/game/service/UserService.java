package com.example.gameservicedemo.game.service;

import com.example.gamedatademo.bean.User;
import com.example.gamedatademo.mapper.UserMapper;
import com.example.gameservicedemo.game.cache.UserCache;
import com.example.gameservicedemo.game.service.bean.UserBeCache;
import com.example.gameservicedemo.manager.NotificationManager;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/30/15:47
 * @Description:
 */
@Slf4j
@Service
public class UserService {

    @Autowired
    UserMapper userMapper;
    /**
     * 注册用户
     * @param user
     * @return
     */
    public void registerUser(ChannelHandlerContext ctx, User user){
        String password = user.getPassword();
        String md5Str = DigestUtils.md5DigestAsHex(password.getBytes());
        user.setPassword(md5Str);
        //信息校验
        Integer insert = userMapper.insert(user);
        log.info(user.toString());
        //通知用户
        NotificationManager.notifyByCtx(ctx,"你已成功注册！你的登录账号是："+user.getUserId()+"，快使用userLogin命令去登录吧");
    }

    /**
     * 用户登录
     * @param ctx 上下文
     * @param userId 用户id
     * @param password 密码
     */
    public void userLogin (ChannelHandlerContext ctx,Integer userId,String password){
        //先查看缓存，再查找数据库，在返回之前将从数据库取出的数据放入缓存。
        //先查看数据库中是否存在此用户
        UserBeCache userByUserId = UserCache.getUserByUserId(userId);
        //对象即可能是 null 也可能是非 null，使用 ofNullable() 方法：
        UserBeCache userBeCache1 = Optional.ofNullable(userByUserId)
        //orElseGet这个方法会在有值的时候返回值，如果没有值，它会执行作为参数传入的 Supplier(供应者) 函数式接口，并将返回其执行结果：
                .orElseGet(() -> {
                            //查询数据库
                    log.info("userId={}",userId.toString());
                            User user = userMapper.selectByUserId(userId);
                            log.info(user.toString());
                            log.info(user.toString());
                            return Optional.ofNullable(user)
                            //map() 对值应用(调用)作为参数的函数，然后将返回的值包装在 Optional 中
                                    .map(
                                            tU -> {
                                                UserBeCache userBeCache = new UserBeCache();
                                                log.info(userBeCache.toString());
                                                BeanUtils.copyProperties(user, userBeCache);
                                                return userBeCache;
                                            }
                                    ).orElse(null);
                        }
                );
        if(Objects.isNull(userBeCache1)){
            NotificationManager.notifyByCtx(ctx,"用户id不存在");
            return ;
        }
        //将密码加密，//对密码加密，后匹配数据库中的密码
        String md5Str = DigestUtils.md5DigestAsHex(password.getBytes());
        if(!userBeCache1.getPassword().equals(md5Str)){
            NotificationManager.notifyByCtx(ctx,"密码或用户名错误");
            return ;
        }
        UserCache.putCtxUser(ctx,userBeCache1);
        UserCache.putUserIdCtx(userBeCache1.getUserId(),ctx);
        userBeCache1.setContext(ctx);
        NotificationManager.notifyByCtx(ctx, MessageFormat.format(
                "用户登录成功，可以使用指令 list_roles 加载当前账户的角色列表",userBeCache1.getNickName()
        ));
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
    /**
     *  通过连接上下文找到用户
     */
    public UserBeCache getUserByCxt(ChannelHandlerContext ctx) {
        return UserCache.getUserByCtx(ctx);
    }
}
