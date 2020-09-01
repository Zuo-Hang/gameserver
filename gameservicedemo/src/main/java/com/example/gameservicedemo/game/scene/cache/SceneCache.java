package com.example.gameservicedemo.game.scene.cache;

import com.example.gameservicedemo.game.scene.bean.Scene;
import com.example.gameservicedemo.game.scene.service.SceneObjectService;
import com.example.gameservicedemo.game.scene.service.SceneService;
import com.example.gameservicedemo.util.excel.subclassexcelutil.SceneExcelUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/10/21:43
 * @Description: 场景缓存类
 */

@Slf4j
@Component
public class SceneCache {
    @Autowired
    SceneObjectService sceneObjectService;
    @Autowired
    SceneService sceneService;

    /** 缓存不过期 */
    private Cache<Integer, Scene> sceneCache = CacheBuilder.newBuilder()
            .removalListener(
                    notification -> System.out.println(notification.getKey() + "场景被移除, 原因是" + notification.getCause())
            ).build();

    /**
     * 在启动时加载Excel中的场景资源
     * 若未配置的情况下为null
     */
    @PostConstruct
    private void init() {
        SceneExcelUtil sceneExcelUtil = new SceneExcelUtil("C:\\java_project\\mmodemo\\gameservicedemo\\src\\main\\resources\\game_configuration_excel\\scene.xlsx");
        Map<Integer, Scene> map = sceneExcelUtil.getMap();
        for (Scene  gameScene: map.values()) {
            //初始化场景对象，NPC，怪物
            Scene scene = sceneObjectService.initSceneObject(gameScene);
            sceneService.initNeighborScene(scene);
            //存入缓存
            sceneCache.put(scene.getId(), scene);
        }
        log.info("场景资源加载进缓存完毕");
    }

    /**
     * 获取场景
     * @param key
     * @return
     */
    public Scene getScene(Integer key) {
        return sceneCache.getIfPresent(key);
    }

    public Collection<Scene> getAllScene(){
        return sceneCache.asMap().values();
    }
}
