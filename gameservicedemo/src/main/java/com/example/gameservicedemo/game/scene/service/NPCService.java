package com.example.gameservicedemo.game.scene.service;

import com.example.commondemo.base.RequestCode;
import com.example.gameservicedemo.event.EventBus;
import com.example.gameservicedemo.event.model.TalkWithEvent;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.service.PlayerLoginService;
import com.example.gameservicedemo.game.scene.bean.NPC;
import com.example.gameservicedemo.manager.NotificationManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/25/15:47
 * @Description: 与NPC相关的服务  交谈、查看任务、反击
 */
@Service
public class NPCService {
    @Autowired
    PlayerLoginService playerLoginService;
    @Autowired
    NotificationManager notificationManager;
    @Autowired
    SceneObjectService sceneObjectService;

    /**
     * 与NPC进行交谈
     *
     * @param context 上下文
     * @param npcUuid npcUuid
     */
    public void talkWithNPC(ChannelHandlerContext context, Long npcUuid) {
        PlayerBeCache playerByCtx = playerLoginService.isLoad(context);
        if(Objects.isNull(playerByCtx)){
            notificationManager.notifyPlayer(playerByCtx, "你还未加载游戏化身！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        NPC npc = playerByCtx.getSceneNowAt().getNpcs().get(npcUuid);
        if (Objects.isNull(npc)) {
            notificationManager.notifyPlayer(playerByCtx, "输入的npcUUID错误！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        sceneObjectService.talk(playerByCtx, npc);
        //发布一个事件
        EventBus.publish(new TalkWithEvent(playerByCtx,npc.getId()));
    }
}
