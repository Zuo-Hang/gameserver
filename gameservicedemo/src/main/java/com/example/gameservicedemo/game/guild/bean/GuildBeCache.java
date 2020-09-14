package com.example.gameservicedemo.game.guild.bean;

import com.example.gamedatademo.bean.Guild;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.tools.bean.Tools;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/09/15:38
 * @Description:
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class GuildBeCache extends Guild {
    /** 成员
     *
     */
    private Map<Integer, PlayerBeCache> memberMap = new ConcurrentHashMap<>();

    /** 公会仓库
     *
     */
    private Map<Long, Tools> warehouseMap =  new ConcurrentSkipListMap<>();


    /** 请求加入公会的列表
     * <playerId,请求>
     */
    private Map<Integer,PlayerJoinRequest> playerJoinRequestMap =  new ConcurrentSkipListMap<>();

    public GuildBeCache(Long guildId,String guildName, Integer level, Integer wareHouseSize) {
        this.setId(guildId);
        this.setName(guildName);
        this.setLevel(level);
        this.setWarehouseSize(wareHouseSize);
        this.setColdNum(0);
    }

    public GuildBeCache() {
    }
/**
     * 向仓库中获取物品/添加物品（安全的）
     */
}
