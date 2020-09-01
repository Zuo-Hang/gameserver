package com.example.gameservicedemo.game.scene.service;

import com.example.commondemo.base.RequestCode;
import com.example.gameservicedemo.base.IdGenerator;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.cache.PlayerCache;
import com.example.gameservicedemo.game.scene.bean.*;
import com.example.gameservicedemo.game.scene.cache.SceneObjectCache;
import com.example.gameservicedemo.manager.NotificationManager;
import com.example.gameservicedemo.util.SnowFlake;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    public SceneObject getSceneObject(Integer gameObjectId) {
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
                .map(this::getSceneObject)
                .forEach(sceneObject -> {
                    //以从副本拷贝一个新对象的方式初始化场景内的实体对象
                            if (sceneObject.getRoleType().equals(SceneObjectType.NPC.getType())) {
                                NPC npc = new NPC();
                                BeanUtils.copyProperties(sceneObject, npc);
                                npc.setUuid(IdGenerator.getAnId());
                                scene.getNpcs().put(npc.getUuid(), npc);
                            }
                            if (sceneObject.getRoleType().equals(SceneObjectType.WILD_MONSTER.getType())) {
                                Monster monster = new Monster();
                                BeanUtils.copyProperties(sceneObject, monster);
                                monster.setUuid(IdGenerator.getAnId());
                                scene.getMonsters().put(monster.getUuid(), monster);
                            }
                        }
                );
        return scene;
    }

    /**
     * 与场景对象进行交谈
     *
     * @param playerBeCache 玩家化身对象
     * @param sceneObject   场景对象
     */
    public void talk(PlayerBeCache playerBeCache, SceneObject sceneObject) {
        String string= "\n"+sceneObject.getName()+"："+sceneObject.getTalk();
        notificationManager.notifyPlayer(playerBeCache,string , RequestCode.SUCCESS.getCode());
    }

    /**
     *  场景对象死亡后处理
     * @param sceneObject 场景对象死亡后处理
     * @return
     */
    public boolean sceneObjectAfterDead(SceneObject sceneObject) {
        if (sceneObject.getHp() <= 0) {
            // 重要，设置死亡时间
            sceneObject.setDeadTime(System.currentTimeMillis());
            sceneObject.setHp(0);
            sceneObject.setState(-1);
            // 重要，清空对象当前目标
            sceneObject.setTarget(null);
            return true;
        } else{
            return false;
        }
    }
}
