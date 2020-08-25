package com.example.gameservicedemo.game.scene.controller;

import com.example.commondemo.base.Command;
import com.example.commondemo.message.Message;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.base.controller.ControllerManager;
import com.example.gameservicedemo.game.player.service.PlayerLoginService;
import com.example.gameservicedemo.game.player.service.PlayerService;
import com.example.gameservicedemo.game.scene.service.NPCService;
import com.example.gameservicedemo.game.scene.service.SceneObjectService;
import com.example.gameservicedemo.util.CheckParametersUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/12/12:02
 * @Description: 场景对象的控制器
 */
@Slf4j
@Component
public class SceneObjectController {
    @Autowired
    SceneObjectService sceneObjectService;
    @Autowired
    PlayerLoginService playerLoginService;
    @Autowired
    NPCService npcService;
    @Autowired
    PlayerService playerService;


    {
        ControllerManager.add(Command.TALK_WITH_NPC.getRequestCode(),this::talkWithNPC);
    }
    /**
     * 与NPC进行交谈
     *
     * @param context 上下文对象
     * @param message 命令
     */
    public void talkWithNPC(ChannelHandlerContext context, Message message) {
        //判断是否登录
        String[] strings = CheckParametersUtil.checkParameter(context, message, 2);
        PlayerBeCache player = playerLoginService.getPlayerByContext(context);
        npcService.talkWithNPC(context, Integer.valueOf(strings[1]));
    }
}
