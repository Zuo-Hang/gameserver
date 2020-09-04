package com.example.gameservicedemo.game.hurt;

import com.example.gameservicedemo.base.bean.Creature;
import com.example.gameservicedemo.game.scene.bean.Scene;
import com.example.gameservicedemo.game.skill.service.GetHurtNum;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/04/16:54
 * @Description:
 */
public interface MakeHurt {
    /**
     * {@code makeHurt} 造成伤害，执行此方法进行数值更改，并进行通知
     * @param murderer 伤害发起者
     * @param target 伤害承受者
     * @param scene 发生的场景
     * @param hurtNum 伤害数值
     */
    void makeHurt(Creature murderer, Creature target, Scene scene, GetHurtNum hurtNum);
}
