package com.example.gamedatademo.mapper;

import com.example.gamedatademo.bean.Role;
import com.example.gamedatademo.bean.Scene;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/04/21:53
 * @Description: 场景相关的 mybatis mapper接口
 */
public interface SceneMapper {
    /**
     * 添加新的场景
     * @param scene
     * @return
     */
    Integer insert(Scene scene);

    /**
     * 按照场景id查找场景
     * @param sceneId
     * @return
     */
    Scene selectBySceneId(Integer sceneId);

    /**
     * 按照场景名称进行查询
     * @param sceneName
     * @return
     */
    Scene selectBySceneName(String sceneName);

    /**
     * 按照场景id删除
     * @param sceneId
     * @return
     */
    Integer deleteBySceneId(Integer sceneId);

    /**
     * 按照场景id更新
     * @param scene
     * @return
     */
    Integer updateBySceneId(Scene scene);
}
