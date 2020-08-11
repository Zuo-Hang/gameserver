package com.example.gameservicedemo.service;

import com.example.gameservicedemo.bean.scene.*;
import com.example.gameservicedemo.cache.SceneObjectCache;
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

    /**
     * 在缓存中获取一个指定场景对象
     * @param gameObjectId
     * @return
     */
    public SceneObject getGameObject(Integer gameObjectId) {
        return sceneObjectCache.get(gameObjectId);
    }
    /**
     * 初始化场景当中的场景对象
     * @param scene 需要被初始化的场景
     * @return 初始化后的场景
     */
    public Scene initSceneObject(Scene scene){
        String gameObjectIds = scene.getGameObjectIds();
        Arrays.stream(gameObjectIds.split(","))
                .map(Integer::valueOf)
                .map( this::getGameObject)
                .forEach( sceneObject -> {
                            if ( sceneObject.getRoleType().equals(SceneObjectType.NPC.getType())) {
                                NPC npc = new NPC();
                                BeanUtils.copyProperties(sceneObject,npc);
                                scene.getNpcs().put(sceneObject.getId(), npc);
                            }
                            if (sceneObject.getRoleType().equals(SceneObjectType.WILD_MONSTER.getType()) ) {
                                Monster monster = new Monster();
                                BeanUtils.copyProperties(sceneObject,monster);
                                scene.getMonsters().put(sceneObject.getId(), monster);
                            }
                        }
                );
        return scene;
    }
}
