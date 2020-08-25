package com.example.gameservicedemo.game.scene.service;

import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.service.PlayerLoginService;
import com.example.gameservicedemo.game.scene.bean.SceneObject;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    SceneObjectService sceneObjectService;
    /**
     * 与NPC进行交谈
     *
     * @param context 上下文
     * @param NPCId   NPCid
     */
    public void talkWithNPC(ChannelHandlerContext context, Integer NPCId) {
        PlayerBeCache playerByCtx = playerLoginService.getPlayerByContext(context);
        SceneObject sceneObject = sceneObjectService.getSceneObject(NPCId);
        sceneObjectService.talk(playerByCtx,sceneObject);
        //创建一个事件
    }
}
