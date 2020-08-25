package com.example.gameservicedemo.game.bag.bean;

import com.example.gamedatademo.bean.Bag;
import com.example.gameservicedemo.game.tools.bean.Tools;
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
    private Integer playerId;
    /** 背包中的物品 */
    Map<Integer, Tools> toolsMap=new ConcurrentSkipListMap<>();
}
