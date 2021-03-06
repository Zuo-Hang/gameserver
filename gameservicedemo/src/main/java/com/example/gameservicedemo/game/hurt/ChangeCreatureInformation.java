package com.example.gameservicedemo.game.hurt;

import com.example.gameservicedemo.base.bean.Creature;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/07/21:55
 * @Description:
 */
public class ChangeCreatureInformation implements ChangeInformation{
    /**
     * Hp 可以为正或负，正代表血量增加，负代表受到伤害
     * @param creature
     * @param Hp
     */
    @Override
    public void changeHp(Creature creature, Integer Hp) {
        synchronized (creature){
            if(creature.getHp() + Hp>0&&creature.getHp() + Hp<=creature.getMaxHp()){
                creature.setHp(creature.getHp() + Hp);
            }else if(creature.getHp() + Hp<=0){
                creature.setHp(0);
            }else{
                creature.setHp(creature.getMaxHp());
            }
        }
    }

    /**
     * 传入的是新的值
     * @param creature
     * @param num
     */
    @Override
    public void changeMagicShield(Creature creature, Integer num) {
        synchronized (creature){
            creature.setMagicShield(num);
        }
    }

    @Override
    public void changeShield(Creature creature, Integer num) {
        synchronized (creature){
            creature.setShield(num);
        }
    }

    @Override
    public void changeTarget(Creature creature, Creature target) {
        synchronized (creature){
            creature.setTarget(target);
        }
    }
}
