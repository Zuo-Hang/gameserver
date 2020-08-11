package com.example.gameservicedemo.bean;

import com.example.gamedatademo.bean.User;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/04/15:43
 * @Description: 需要被缓存的用户实例
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class UserBeCache extends User {
    private ChannelHandlerContext context;
}
