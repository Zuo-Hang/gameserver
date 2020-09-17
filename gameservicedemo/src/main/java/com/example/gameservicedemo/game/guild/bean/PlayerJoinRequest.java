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

    Integer playerId;

    public PlayerJoinRequest() {
    }

    public PlayerJoinRequest(Date date, Integer playerId) {
        this.isAgree = false;
        this.date = date;
        this.playerId = playerId;
    }
}
