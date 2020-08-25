package com.example.gameservicedemo.event.model;

import com.example.gameservicedemo.event.Event;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.tools.bean.Tools;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/25/16:37
 * @Description:穿戴武器事件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class EquipmentEvent extends Event {

    private PlayerBeCache player;
    Tools thingInfo;
}
