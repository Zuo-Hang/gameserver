package com.example.gameservicedemo.game.hurt;

import com.example.gameservicedemo.base.bean.Creature;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/04/16:54
 * @Description: 线程安全的更改活物的一些属性
 */
public interface ChangeInformation {

    void changeHp(Creature creature , Integer Hp);

    void changeMagicShield(Creature creature , Integer num);

    void changeShield(Creature creature , Integer num);

    void changeTarget(Creature creature , Creature target);

}
