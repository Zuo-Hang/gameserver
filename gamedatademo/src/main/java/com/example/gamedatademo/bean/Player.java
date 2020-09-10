package com.example.gamedatademo.bean;

import lombok.Data;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

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
    /**
     * 经验值
     */
    private Integer exp;
    /**
     * 状态
     */
    private Integer state;
    /**
     * 金币
     */
    private Integer money;
    /**
     * 参加的公会
     */
    private Long guildId;
    /**
     * 角色的类型
     */
    private Integer roleClass;
    /**
     * 公会类型
     */
    private Integer guildRoleType;
    /**
     * 好友
     */
    private String friends;
    /**
     * 该角色对应的背包
     */
    private Integer bagId;

    private Set<Integer> update=new ConcurrentSkipListSet<>();

}
