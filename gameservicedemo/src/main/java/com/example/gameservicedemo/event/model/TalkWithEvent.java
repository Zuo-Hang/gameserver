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
 * @Description:
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TalkWithEvent extends Event {
    private PlayerBeCache player;
    private Integer sceneObjectId;
}
