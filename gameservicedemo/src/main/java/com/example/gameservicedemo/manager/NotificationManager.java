package com.example.gameservicedemo.manager;

import com.example.commondemo.base.RequestCode;
import com.example.commondemo.base.TcpProtocol;
import com.example.commondemo.code.GetCoder;
import com.example.commondemo.message.Message;
import com.example.gamedatademo.bean.Player;
import com.example.gameservicedemo.cache.PlayerCache;
import com.example.gameservicedemo.game.service.SceneService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/30/15:15
 * @Description:通知管理器
 */
@Slf4j
@Service
public class NotificationManager {

    @Autowired
    SceneService sceneService;
    @Autowired
    PlayerCache playerCache;

    /**
     *  通过通道上下文来通知单个玩家
     * @param ctx 上下文
     * @param e 信息
     * @param <E> 信息的类型
     */
    public  <E> void notifyByCtx(ChannelHandlerContext ctx, E e){
        Message message = new Message();
        message.setMessage(e.toString()+"\n");
        message.setRequestCode(RequestCode.NOT_SUPPORTED_OPERATION.getCode());
        byte[] encode = new byte[0];
        try {
            encode = GetCoder.getCoder().encode(message);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        TcpProtocol protocol = new TcpProtocol();
        protocol.setData(encode);
        protocol.setLen(encode.length);
        ctx.writeAndFlush(protocol);
    }

    /**
     * 通知场景内的所有玩家
     * @param sceneId
     * @param e
     * @param <E>
     */
    public  <E> void notifyScene(Integer sceneId,E e){
        List<Player> allPlayer = sceneService.getAllPlayer(sceneId);
        for (Player player:allPlayer){
            notifyByCtx(playerCache.getCxtByPlayerId(player.getPlayerId()),e);
        }
    }
}
