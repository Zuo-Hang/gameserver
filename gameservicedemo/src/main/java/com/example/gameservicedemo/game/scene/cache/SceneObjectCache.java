package com.example.gameservicedemo.game.scene.cache;

import com.example.gameservicedemo.game.scene.bean.SceneObject;
import com.example.gameservicedemo.game.scene.bean.SceneObjectType;
import com.example.gameservicedemo.util.excel.subclassexcelutil.SceneObjectExcelUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/11/21:26
 * @Description: 加载Excel中的SceneObject 生成实例并进行缓存
 */
@Slf4j
@Component
public class SceneObjectCache {
    @Autowired
    NPCCache npcCache;
    @Autowired
    MonsterCache monsterCache;

    /**
     * 缓存不过期
     */
    private Cache<Integer, SceneObject> sceneObjectCache = CacheBuilder.newBuilder()
            .removalListener(
                    notification -> System.out.println(notification.getKey() + "场景对象被移除, 原因是" + notification.getCause())
            ).build();

    /**
     * 在项目启动时读取Excel，并生成对象进行缓存
     */
    @PostConstruct
    public void init() {
        SceneObjectExcelUtil sceneObjectExcelUtil = new SceneObjectExcelUtil("C:\\java_project\\mmodemo\\gameservicedemo\\src\\main\\resources\\game_configuration_excel\\sceneObject.xlsx");
        Map<Integer, SceneObject> sceneObjectMap = sceneObjectExcelUtil.getMap();
        sceneObjectMap.values().forEach(sceneObject -> {
            sceneObjectCache.put(sceneObject.getId(), sceneObject);
            initCacheByType(sceneObject);
        });
        log.info("游戏对象资源加载完毕");
    }

    //这些的初始化不应该放在这里，初始化应该放在场景的加载里面。然后场景配置中应当配置这些NPC、怪物的数量和种类
    private void initCacheByType(SceneObject sceneObject) {
        Integer roleType = sceneObject.getRoleType();
        if (SceneObjectType.NPC.getType().equals(roleType)) {
            npcCache.putCahce(sceneObject);
        } else if (SceneObjectType.WILD_MONSTER.getType().equals(roleType)) {
            monsterCache.putCahce(sceneObject);
        }
    }

    /**
     * 在缓存中获取对象
     *
     * @param gameObjectId
     * @return
     */
    public SceneObject get(Integer gameObjectId) {
        return sceneObjectCache.getIfPresent(gameObjectId);
    }

    /**
     * 向缓存中存放对象
     *
     * @param gameObjectId
     * @param value
     */
    public void put(Integer gameObjectId, SceneObject value) {
        sceneObjectCache.put(gameObjectId, value);
    }


    /**
     * 获取对应的map
     *
     * @return
     */
    public Map<Integer, SceneObject> list() {
        return sceneObjectCache.asMap();
    }
}
