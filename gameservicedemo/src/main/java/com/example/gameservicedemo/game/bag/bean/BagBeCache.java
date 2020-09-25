package com.example.gameservicedemo.game.bag.bean;

import com.example.gamedatademo.bean.Bag;
import com.example.gameservicedemo.background.WriteBackDB;
import com.example.gameservicedemo.game.tools.bean.Tools;
import com.google.gson.Gson;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/17/20:09
 * @Description:
 */
@Data
public class BagBeCache extends Bag {

    private WriteBackDB writeBackDB;

    public boolean tag;

    private Integer playerId;
    /**
     * 背包中的所有物品物品  <uuid,tools>
     */
    Map<Long, Tools> toolsMap = new ConcurrentSkipListMap<>();
    /**
     * index,item
     */
    Map<Integer, Item> itemMap = new ConcurrentSkipListMap<>();

    /**
     * 放入背包，线程安全。并且自动提交回写库操作
     *
     * @param tools
     */
    public synchronized void putInToolsMap(Tools tools) {
        toolsMap.put(tools.getUuid(), tools);
        if(tag){
            setTools(new Gson().toJson(this.getToolsMap().values()));
            getUpdate().add("tools");
            writeBackDB.updateBagDb(this);
        }
    }

    /**
     * 从背包移除，线程安全。并且自动提交回写库操作
     *
     * @param toolsId
     */
    public synchronized void removeFromToolsMap(Long toolsId) {
        toolsMap.remove(toolsId);
        setTools(new Gson().toJson(this.getToolsMap().values()));
        getUpdate().add("tools");
        writeBackDB.updateBagDb(this);
    }

    /**
     * @param bagIndex
     * @param item
     */
    public synchronized void putInItemMap(Integer bagIndex, Item item) {
        itemMap.put(bagIndex, item);
        //初始化的时候不应该回写
        if(tag){
            setItems(new Gson().toJson(this.getItemMap().values()));
            getUpdate().add("items");
            writeBackDB.updateBagDb(this);
        }
    }

    /**
     * @param bagIndex
     */
    public synchronized void removeFromItemMap(Integer bagIndex) {
        itemMap.remove(bagIndex);
        setItems(new Gson().toJson(this.getItemMap().values()));
        getUpdate().add("items");
        writeBackDB.updateBagDb(this);
    }
}
