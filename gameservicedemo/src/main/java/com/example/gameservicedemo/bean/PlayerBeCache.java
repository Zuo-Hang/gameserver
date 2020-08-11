package com.example.gameservicedemo.bean;

import com.example.gamedatademo.bean.Player;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/05/17:13
 * @Description: 应当被缓存的化身信息
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class PlayerBeCache extends Player {
    private ChannelHandlerContext context;
}
