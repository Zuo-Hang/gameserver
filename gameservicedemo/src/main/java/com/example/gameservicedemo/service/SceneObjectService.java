package com.example.gameservicedemo.service;

import com.example.commondemo.base.RequestCode;
import com.example.gamedatademo.bean.Player;
import com.example.gameservicedemo.bean.PlayerBeCache;
import com.example.gameservicedemo.bean.scene.*;
import com.example.gameservicedemo.cache.PlayerCache;
import com.example.gameservicedemo.cache.SceneObjectCache;
import com.example.gameservicedemo.manager.NotificationManager;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/11/21:52
 * @Description: 主要进行对场景对象的处理
 */
@Service
@Slf4j
public class SceneObjectService {
    @Autowired
    SceneObjectCache sceneObjectCache;
    @Autowired
    PlayerCache playerCache;
    @Autowired
    NotificationManager notificationManager;

    /**
     * 在缓存中获取一个指定场景对象
     *
     * @param gameObjectId
     * @return
     */
    public SceneObject getGameObject(Integer gameObjectId) {
        return sceneObjectCache.get(gameObjectId);
    }

    /**
     * 初始化场景当中的场景对象
     *
     * @param scene 需要被初始化的场景
     * @return 初始化后的场景
     */
    public Scene initSceneObject(Scene scene) {
        String gameObjectIds = scene.getGameObjectIds();
        Arrays.stream(gameObjectIds.split(","))
                .map(Integer::valueOf)
                .map(this::getGameObject)
                .forEach(sceneObject -> {
                            if (sceneObject.getRoleType().equals(SceneObjectType.NPC.getType())) {
                                NPC npc = new NPC();
                                BeanUtils.copyProperties(sceneObject, npc);
                                scene.getNpcs().put(sceneObject.getId(), npc);
                            }
                            if (sceneObject.getRoleType().equals(SceneObjectType.WILD_MONSTER.getType())) {
                                Monster monster = new Monster();
                                BeanUtils.copyProperties(sceneObject, monster);
                                scene.getMonsters().put(sceneObject.getId(), monster);
                            }
                        }
                );
        return scene;
    }

    /**
     * 与NPC进行交谈
     *
     * @param context 上下文
     * @param NPCId   NPCid
     */
    public void talkWithNPC(ChannelHandlerContext context, Integer NPCId) {
        PlayerBeCache playerByCtx = playerCache.getPlayerByCtx(context);
        SceneObject sceneObject = sceneObjectCache.get(NPCId);
        talk(playerByCtx,sceneObject);

        //创建一个事件
    }

    /**
     * 与场景对象进行交谈
     *
     * @param playerBeCache 玩家化身对象
     * @param sceneObject   场景对象
     */
    public void talk(PlayerBeCache playerBeCache, SceneObject sceneObject) {
        notificationManager.notifyPlayer(playerBeCache, sceneObject.getTalk(), RequestCode.SUCCESS.getCode());
    }
}
