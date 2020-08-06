package com.example.gamedatademo.mapper;

import com.example.gamedatademo.bean.Player;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/05/16:29
 * @Description: 化身相关的 mybatis mapper接口
 */
public interface PlayerMapper {
    /**
     * 按化身id查询
     * @param playerId
     * @return
     */
    Player selectByPlayerId(Integer playerId);

    /**
     * 插入新的化身
     * @param player
     * @return
     */
    Integer insert(Player player);

    /**
     * 按化身id进行更新
     * @param player
     * @return
     */
    Integer updateByPlayerId(Player player);
}
