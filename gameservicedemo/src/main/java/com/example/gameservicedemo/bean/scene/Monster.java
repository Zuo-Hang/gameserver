package com.example.gameservicedemo.bean.scene;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.text.MessageFormat;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/11/15:00
 * @Description: 怪物
 */

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(exclude = {"target"})
public class Monster  extends SceneObject {

    /** 攻击速率，默认10000毫秒 */
    private Integer attackSpeed = 10000;
    private long attackTime = System.currentTimeMillis();


    /** 当前攻击目标 */
    Creature target;

    /**
     * 向用户展示数据
     * @return
     */
    public String displayData() {
        return MessageFormat.format("id:{0}  name:{1}  hp:{2}  mp:{3}  {4}"
                ,this.getId(),this.getName(), this.getHp(), this.getMp(), this.getState()==-1?"死亡":"存活" );
    }
}
