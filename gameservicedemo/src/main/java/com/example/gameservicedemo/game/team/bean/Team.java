package com.example.gameservicedemo.game.team.bean;

import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/26/11:17
 * @Description: 队伍实体类
 */
@Data
public class Team {
    /**
     * teamId
     */
    private Long id;
    /** 队长id */
    private Long captainId;
    private Map<Long, PlayerBeCache> teamPlayer=new ConcurrentHashMap<>();
    /** 小队默认是五人一队 */
    private Integer teamSize = 5;

    public Team(Integer teamSize) {
        this.teamSize = teamSize;
    }

    public Team() {
    }
}
