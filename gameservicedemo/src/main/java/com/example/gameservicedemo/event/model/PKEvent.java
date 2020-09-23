package com.example.gameservicedemo.event.model;

import com.example.gameservicedemo.event.Event;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/25/16:37
 * @Description: 对战事件
 */

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PKEvent extends Event {
    private PlayerBeCache player;
    /**
     * 后期可以再出一个常败将军
     */
    private boolean isWin;
}
