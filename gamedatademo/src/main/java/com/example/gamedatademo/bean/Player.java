package com.example.gamedatademo.bean;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/05/15:53
 * @Description:
 */
@Data
public class Player {
    /**
     * 化身id
     */
    private Integer playerId;
    /**
     * 化身名称
     */
    private String playerName;
    /**
     * 所属用户
     */
    private Integer userId;
    /**
     * 当前场景
     */
    private Integer nowAt;
}
