package com.example.gameservicedemo.event.model;

import com.example.gameservicedemo.event.Event;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/25/16:37
 * @Description: 组队事件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class TeamEvent extends Event {

    private List<PlayerBeCache> teammate;


}
