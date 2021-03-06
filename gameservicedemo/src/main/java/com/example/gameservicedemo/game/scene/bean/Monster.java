package com.example.gameservicedemo.game.scene.bean;

import com.example.gameservicedemo.base.bean.Creature;
import com.example.gameservicedemo.game.skill.bean.Skill;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.text.MessageFormat;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/11/15:00
 * @Description: 怪物
 */

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"target"})
public class Monster extends SceneObject implements Creature {
    //攻击速度
    private Integer attackSpeed = 1000;
    private long attackTime = System.currentTimeMillis();

    public String displayData() {
        return MessageFormat.format("uuid:{0}  name:{1}  hp:{2}  mp:{3} "
                , this.getUuid(), this.getName(), this.getHp(), this.getState() == -1 ? "死亡" : "存活");
    }
}
