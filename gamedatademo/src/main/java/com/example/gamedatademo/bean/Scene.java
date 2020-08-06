package com.example.gamedatademo.bean;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/30/16:56
 * @Description:场景实体类
 */
@Data
public class Scene {
    /**
     * 场景ID
     */
    private int sceneId;
    /**
     * 场景名称
     */
    private String sceneName;
    /**
     * 场景描述信息
     */
    private String sceneDescribe;
    /**
     * 相邻场景，为Scene的id,由","分割
     */
    private String adjacentScenes;
    /**
     * 场景对象，为Role的id,由","分割
     */
    private String sceneObjects;

}
