package com.example.gameservicedemo.game.guild.bean;

import com.example.gamedatademo.bean.Guild;
import com.example.gamedatademo.bean.Player;
import com.example.gameservicedemo.background.WriteBackDB;
import com.example.gameservicedemo.game.tools.bean.Tools;
import com.google.gson.Gson;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/09/15:38
 * @Description:
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GuildBeCache extends Guild {

    private WriteBackDB writeBackDB;

    /**
     * 成员
     */
    private List<Integer> memberIdList = new CopyOnWriteArrayList<>();

    /**
     * 公会仓库
     */
    private Map<Long, Tools> warehouseMap = new ConcurrentSkipListMap<>();

    Integer maxNum=10;

    /**
     * 请求加入公会的列表
     * <playerId,请求>
     */
    private Map<Integer, PlayerJoinRequest> playerJoinRequestMap = new ConcurrentSkipListMap<>();

    public GuildBeCache(Long guildId, String guildName, Integer level, Integer wareHouseSize) {
        this.setId(guildId);
        this.setName(guildName);
        this.setLevel(level);
        this.setWarehouseSize(wareHouseSize);
        this.setGoldNum(0);
    }

    public GuildBeCache() {
    }

    /**
     * 新增成员
     * @param player
     */
    public void addPlayer(Player player){
        memberIdList.add(player.getPlayerId());
        getUpdate().add("member");
        Gson gson = new Gson();
        setMember(gson.toJson(memberIdList));
        writeBackDB.updateGuildDb(this);
    }

    /**
     * 移除成员
     * @param playerId
     */
    public void removePlayer(Integer playerId){
        memberIdList.remove(playerId);
        getUpdate().add("member");
        Gson gson = new Gson();
        setMember(gson.toJson(memberIdList));
        writeBackDB.updateGuildDb(this);
    }

    /**
     * 添加申请
     * @param playerId
     * @param request
     */
    public void addJoinRequest(Integer playerId,PlayerJoinRequest request){
        playerJoinRequestMap.put(playerId, request);
        getUpdate().add("joinRequest");
        Gson gson = new Gson();
        setJoinRequest(gson.toJson(playerJoinRequestMap));
        writeBackDB.updateGuildDb(this);
    }

    /**
     * 清除申请
     * @param playerId
     */
    public void deleteJoinRequest(Integer playerId){
        playerJoinRequestMap.remove(playerId);
        getUpdate().add("joinRequest");
        Gson gson = new Gson();
        setJoinRequest(gson.toJson(playerJoinRequestMap));
        writeBackDB.updateGuildDb(this);
    }

    /**
     * 从仓库获取物品(加锁的，线程安全)
     */
    public synchronized Tools warehouseTake(Long toolsId) {
        Tools remove = warehouseMap.remove(toolsId);
        Gson gson = new Gson();
        setWarehouse(gson.toJson(warehouseMap));
        getUpdate().add("warehouse");
        writeBackDB.updateGuildDb(this);
        return remove;
    }

    /**
     * 向仓库中添加物品（安全的）
     */
    public synchronized boolean warehouseAdd(Tools tools) {
        if (warehouseMap.size() >= getWarehouseSize()) {
            return false;
        }
        warehouseMap.put(tools.getUuid(), tools);
        Gson gson = new Gson();
        setWarehouse(gson.toJson(warehouseMap));
        getUpdate().add("warehouse");
        writeBackDB.updateGuildDb(this);
        return true;
    }

    /**
     * 捐献金币
     */
    public synchronized boolean contributionGoldNum(Integer count){
        this.setGoldNum(getGoldNum()+count);
        getUpdate().add("goldNum");
        writeBackDB.updateGuildDb(this);
        return true;
    }

    /**
     * 获取金币
     */
    public synchronized boolean takeGoldNum(Integer count){
        if(getGoldNum()-count<0){
            return false;
        }
        this.setGoldNum(getGoldNum()-count);
        getUpdate().add("goldNum");
        writeBackDB.updateGuildDb(this);
        return true;
    }
}
