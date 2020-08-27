package com.example.gameservicedemo.game.scene.bean;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/27/14:52
 * @Description:
 */
public enum CommonSceneId {


    BEGIN_SCENE(1),

    CEMETERY(3)

    ;

    private Integer id;


    CommonSceneId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

}
