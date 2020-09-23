package com.example.gameservicedemo.event.model;

import com.example.gameservicedemo.event.Event;
import com.example.gameservicedemo.game.copy.bean.GameCopyScene;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/25/16:37
 * @Description: 副本事件
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GameCopyEvent extends Event {
    PlayerBeCache player;
    GameCopyScene gameCopyScene;
}
