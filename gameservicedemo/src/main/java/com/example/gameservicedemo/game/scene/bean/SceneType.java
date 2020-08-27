package com.example.gameservicedemo.game.scene.bean;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/27/11:04
 * @Description:
 */
public enum SceneType {
    COMMON_SCENE(1,"普通场景"),
    INSTANCE_SCENE(2,"副本场景"),
    ARENA(3,"角斗场,适应于1V1")
    ;
    Integer code;
    String describe;
    SceneType(Integer code, String describe) {
        this.code = code;
        this.describe = describe;
    }
    public String getDescribe() {
        return describe;
    }
    public Integer getCode() {
        return code;
    }
}
