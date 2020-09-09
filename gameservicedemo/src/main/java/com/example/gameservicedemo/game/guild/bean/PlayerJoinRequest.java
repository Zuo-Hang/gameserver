package com.example.gameservicedemo.game.guild.bean;

import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import lombok.Data;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/09/15:45
 * @Description:
 */
@Data
public class PlayerJoinRequest {
    private boolean isAgree;

    private Date date;

    PlayerBeCache player;

    public PlayerJoinRequest() {
    }

    public PlayerJoinRequest(boolean isAgree, Date date, PlayerBeCache player) {
        this.isAgree = isAgree;
        this.date = date;
        this.player = player;
    }
}
