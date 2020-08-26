package com.example.gameservicedemo.manager;

import com.example.commondemo.base.RequestCode;
import com.example.commondemo.base.TcpProtocol;
import com.example.commondemo.code.GetCoder;
import com.example.commondemo.message.Message;
import com.example.gamedatademo.bean.Player;
import com.example.gameservicedemo.base.bean.Creature;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.cache.PlayerCache;
import com.example.gameservicedemo.game.scene.service.SceneService;
import com.example.gameservicedemo.game.team.bean.Team;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/30/15:15
 * @Description:通知管理器
 */
@Slf4j
@Component
public class NotificationManager {

    @Autowired
    SceneService sceneService;
    @Autowired
    PlayerCache playerCache;


    /**
     * 通过通道上下文来通知单个玩家
     *
     * @param ctx 上下文
     * @param e   信息
     * @param <E> 信息的类型
     */
    public <E> void notifyByCtx(ChannelHandlerContext ctx, E e, Integer code) {
        Message message = new Message();
        message.setMessage(e.toString() + "\n");
        message.setRequestCode(code);
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
     *
     * @param sceneId
     * @param e
     * @param <E>
     */
    public <E> void notifyScene(Integer sceneId, E e, Integer code) {
        List<Player> allPlayer = sceneService.getAllPlayer(sceneId);
        for (Player player : allPlayer) {
            notifyByCtx(playerCache.getCxtByPlayerId(player.getPlayerId()), e, code);
        }
    }

    /**
     * 通知单个玩家
     *
     * @param playerBeCache
     * @param e
     * @param <E>
     */
    public <E> void notifyPlayer(PlayerBeCache playerBeCache, E e, Integer code) {
        ChannelHandlerContext cxtByPlayerId = playerCache.getCxtByPlayerId(playerBeCache.getPlayerId());
        Optional.ofNullable(cxtByPlayerId).ifPresent(c -> notifyByCtx(c, e, code));
    }

    /**
     * 通知生物
     * @param creature
     * @param e
     * @param code
     * @param <E>
     */
    public <E> void notifyCreature(Creature creature, E e, Integer code) {
        if (creature instanceof PlayerBeCache) {
            notifyPlayer((PlayerBeCache) creature, e, code);
        }
    }

    public <E> void notifyTeam(Team team, E e,Integer code) {
        team.getTeamPlayer().values().forEach(player->{
            notifyPlayer(player, e, RequestCode.BAD_REQUEST.getCode());
        });
    }
}
