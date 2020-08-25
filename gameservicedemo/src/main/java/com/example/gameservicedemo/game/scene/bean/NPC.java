package com.example.gameservicedemo.game.scene.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.text.MessageFormat;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/11/15:00
 * @Description: 场景中的NPC
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NPC extends SceneObject {
    Long uuid;
    /**
     * 向用户展示数据
     * @return
     */
    public String displayData() {
        return MessageFormat.format("id:{0}  name:{1}  hp:{2}  mp:{3}  {4}"
                ,this.getId(),this.getName(), this.getHp(), this.getMp(), this.getState()==-1?"死亡":"存活");
    }
}
