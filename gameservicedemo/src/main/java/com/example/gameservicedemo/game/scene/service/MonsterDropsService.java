package com.example.gameservicedemo.game.scene.service;

import com.alibaba.fastjson.JSON;
import com.example.commondemo.base.RequestCode;
import com.example.gameservicedemo.game.bag.bean.BagBeCache;
import com.example.gameservicedemo.game.bag.service.BagService;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.scene.bean.Drop;
import com.example.gameservicedemo.game.scene.bean.SceneObject;
import com.example.gameservicedemo.game.tools.bean.Tools;
import com.example.gameservicedemo.game.tools.service.ToolsService;
import com.example.gameservicedemo.manager.NotificationManager;
import com.example.gameservicedemo.util.ProbabilityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/25/15:50
 * @Description: 怪物掉落
 */
@Service
@Slf4j
public class MonsterDropsService {
    @Autowired
    ToolsService toolsService;
    @Autowired
    BagService bagService;
    @Resource
    private NotificationManager notificationManager;

    /**
     * 物品掉落
     */
    void dropItem(PlayerBeCache player, SceneObject sceneObject) {
        // 掉落金钱
        player.setMoney(player.getMoney() + sceneObject.getDropGoldCoin());
        // 掉落经验
        player.setExp(player.getExp() + sceneObject.getDropExp());
        //额外掉落道具
        List<Drop> dropList = calculateDrop(sceneObject);
        if (Objects.isNull(dropList)) {
            return;
        }
        dropList.forEach(drop -> {
            log.debug("drop {}", drop);
            int chance = drop.getChance();
            if (ProbabilityUtil.getProbability(chance)) {
                Tools toolsById = toolsService.getToolsById(drop.getToolsId());
                Tools tools = new Tools();
                BeanUtils.copyProperties(toolsById, tools);
                //放入背包
                if (bagService.putInBag(player, tools)) {
                    notificationManager.notifyPlayer(player, "掉落{}已经放入背包", RequestCode.SUCCESS.getCode());
                }
            }
        });
    }

    /**
     * 计算物品掉落,获得
     */
    public List<Drop> calculateDrop(SceneObject sceneObject) {
        String dropString = sceneObject.getDrop();
        return JSON.parseArray(dropString, Drop.class);
    }
}
