package com.example.gameservicedemo.game.scene.bean;

import com.example.gameservicedemo.base.bean.Creature;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/25/16:10
 * @Description:
 */
@Data
public class Pet extends Monster{
    private Long petId;

    /** 宠物主人 */
    private Creature master;
}
