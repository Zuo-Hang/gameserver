package com.example.gameservicedemo.event.model;

import com.example.gameservicedemo.event.Event;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.tools.bean.Tools;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/25/16:37
 * @Description: 收集物品的事件，当背包中添加了某件物品时触发
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CollectThingEvent extends Event {
    private PlayerBeCache player;
    private Tools thingInfo;

    public CollectThingEvent(PlayerBeCache player, Tools thingInfo) {
        this.player = player;
        this.thingInfo = thingInfo;
    }
}
