package com.example.gameservicedemo.event.model;

import com.example.gameservicedemo.event.Event;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.scene.bean.Monster;
import com.example.gameservicedemo.game.scene.bean.Scene;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/25/16:37
 * @Description: 怪物死亡的事件
 */
@Data
@EqualsAndHashCode(callSuper=true)
@AllArgsConstructor
public class MonsterEventDeadEvent extends Event {
    private PlayerBeCache player;
    private Monster target;
    private Scene gameScene;
    //private Long damage;
}
