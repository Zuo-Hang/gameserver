package com.example.gameservicedemo.cache;

import com.example.gameservicedemo.bean.scene.SceneObject;
import com.example.gameservicedemo.util.excel.subclassexcelutil.SceneObjectExcelUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
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
    /** 缓存不过期 */
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
        for (SceneObject sceneObject : sceneObjectMap.values()) {
            sceneObjectCache.put(sceneObject.getId(), sceneObject);
        }
        log.info("游戏对象资源加载完毕");
    }

    /**
     * 在缓存中获取对象
     * @param gameObjectId
     * @return
     */
    public SceneObject get(Integer gameObjectId) {
        return sceneObjectCache.getIfPresent(gameObjectId);
    }

    /**
     * 向缓存中存放对象
     * @param gameObjectId
     * @param value
     */
    public void put(Integer gameObjectId, SceneObject value) {
        sceneObjectCache.put(gameObjectId,value);
    }


    /**
     * 获取对应的map
     * @return
     */
    public Map<Integer,SceneObject> list() {
        return sceneObjectCache.asMap();
    }
}
