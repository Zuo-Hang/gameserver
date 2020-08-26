package com.example.gameservicedemo.game.team.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/26/14:39
 * @Description: 组队请求实体
 */
@Data
@AllArgsConstructor
public class TeamRequest {
    Long id;
    Long proposePlayerId;
    Long acceptPlayerId;
    Long teamId;

    public TeamRequest() {
    }
}
