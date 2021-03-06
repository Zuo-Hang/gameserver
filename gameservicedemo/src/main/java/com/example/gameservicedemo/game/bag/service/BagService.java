package com.example.gameservicedemo.game.bag.service;

import com.example.commondemo.base.RequestCode;
import com.example.gamedatademo.bean.Bag;
import com.example.gamedatademo.mapper.BagMapper;
import com.example.gameservicedemo.background.WriteBackDB;
import com.example.gameservicedemo.base.IdGenerator;
import com.example.gameservicedemo.event.EventBus;
import com.example.gameservicedemo.event.model.CollectThingEvent;
import com.example.gameservicedemo.game.bag.bean.BagBeCache;
import com.example.gameservicedemo.game.bag.bean.Item;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.service.PlayerDataService;
import com.example.gameservicedemo.game.tools.bean.Tools;
import com.example.gameservicedemo.game.tools.bean.ToolsRepeatKind;
import com.example.gameservicedemo.manager.NotificationManager;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/25/17:55
 * @Description:
 */
@Component
@Slf4j
public class BagService {
    @Autowired
    NotificationManager notificationManager;
    @Autowired
    BagMapper bagMapper;
    @Autowired
    WriteBackDB writeBackDB;
    @Autowired
    PlayerDataService playerDataService;

    /**
     * 从背包中获取当前物品（有无）
     * 判断物品是否可以叠加
     * 有且不可叠加->不可购买
     * ->可叠加并且已叠加数量可叠加最大数量则叠加
     * 无->判断背包是否还有位置
     * 放入背包
     *
     * @param player
     * @param newTools
     * @return
     */
    public boolean putInBag(PlayerBeCache player, Tools newTools) {
        //判断背包中是否存在此类型道具
        BagBeCache bagBeCache = player.getBagBeCache();
        Map<Integer, Item> itemMap = bagBeCache.getItemMap();
        //可叠加的物品
        if (ToolsRepeatKind.STACKABLE.getType().equals(newTools.getRepeatType())) {
            for (Item item : itemMap.values()) {
                //是同一种物品并且未堆叠满
                if (item.getToolsIdType().equals(newTools.getId()) && item.getToolsUuidS().size() < newTools.getRepeat()) {
                    bagBeCache.putInToolsMap(newTools);
                    item.getToolsUuidS().add(newTools.getUuid());
                    bagBeCache.getUpdate().add("items");
                    notificationManager.notifyPlayer(player, "放入成功", RequestCode.SUCCESS.getCode());
                    packBag(bagBeCache);
                    playerDataService.showPlayerBag(player);
                    EventBus.publish(new CollectThingEvent(player, newTools));
                    return true;
                }
            }
        }
        //是不可重复或某个格子以重复至上限  格子数量还未达到背包最大容量
        if (itemMap.size() < bagBeCache.getSize()) {
            //创建一个新的格子存入背包
            Item item = new Item(IdGenerator.getAnId(), newTools.getId(), itemMap.size() + 1);
            item.getToolsUuidS().add(newTools.getUuid());
            bagBeCache.putInToolsMap(newTools);
            bagBeCache.putInItemMap(item.getIndexInBag(), item);
            packBag(bagBeCache);
            playerDataService.showPlayerBag(player);
            EventBus.publish(new CollectThingEvent(player, newTools));
            return true;
        } else {
            //背包容量不足
            notificationManager.notifyPlayer(player, "你的背包已满，要购买背包中不存在类型的装备必须卖掉某些装备", RequestCode.WARNING.getCode());
            return false;
        }
    }

    /**
     * 从背包中移除
     *
     * @param bag
     * @param toolsUuid
     * @return
     */
    public boolean removeFromBag(BagBeCache bag, Long toolsUuid) {
        for (Item item : bag.getItemMap().values()) {
            if (item.getToolsUuidS().contains(toolsUuid)) {
                item.getToolsUuidS().remove(toolsUuid);
                bag.removeFromToolsMap(toolsUuid);
                packBag(bag);
                return true;
            }
        }
        return false;
    }

    /**
     * 判断背包中是否存在某个物品
     *
     * @param bag
     * @param toolsUuid
     * @return
     */
    public Tools containsTools(BagBeCache bag, Long toolsUuid) {
        for (Item item : bag.getItemMap().values()) {
            if (item.getToolsUuidS().contains(toolsUuid)) {
                return bag.getToolsMap().get(toolsUuid);
            }
        }
        return null;
    }

    /**
     * 整理背包
     *
     * @param bag
     */
    public void packBag(BagBeCache bag) {
        Map<Integer, Item> itemMap = bag.getItemMap();
        int i = 1;
        for (Item item : itemMap.values()) {
            if (item.getToolsUuidS().size() == 0) {
                bag.removeFromItemMap(item.getIndexInBag());
                item.setIndexInBag(0);
                item.setToolsIdType(null);
                item.setId(null);
            } else {
                itemMap.remove(item.getIndexInBag());
                item.setIndexInBag(i++);
                itemMap.put(item.getIndexInBag(), item);
            }
        }
    }

    public void initSomeOneBag(PlayerBeCache playerBeCache) {
        Bag bag = bagMapper.selectByBagId(playerBeCache.getBagId());
        BagBeCache bagBeCache = new BagBeCache();
        bagBeCache.setWriteBackDB(writeBackDB);
        bagBeCache.setPlayerId(playerBeCache.getPlayerId());
        BeanUtils.copyProperties(bag, bagBeCache);
        //获取背包中的物品信息并转化为对象
        String toolsJson = bagBeCache.getTools();
        String itemsJson = bagBeCache.getItems();
        Gson gson = new Gson();
        ArrayList<Tools> toolslist = gson.fromJson(toolsJson, new TypeToken<ArrayList<Tools>>() {
        }.getType());
        ArrayList<Item> itemslist = gson.fromJson(itemsJson, new TypeToken<ArrayList<Item>>() {
        }.getType());
        if (!Objects.isNull(toolslist)) {
            //放入被缓存的背包中
            for (Tools v : toolslist) {
                bagBeCache.putInToolsMap(v);
            }
        }
        if (!Objects.isNull(itemslist)) {
            itemslist.forEach(v -> {
                //放入被缓存的背包中
                bagBeCache.putInItemMap(v.getIndexInBag(), v);
            });
        }
        bagBeCache.setTag(true);
        playerBeCache.setBagBeCache(bagBeCache);
        log.info("角色：{} 的背包初始化完毕！", playerBeCache.getName());
    }

    public boolean havePlace(BagBeCache bagBeCache) {
        return bagBeCache.getItemMap().size() < bagBeCache.getSize();
    }
}
