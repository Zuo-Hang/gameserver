package com.example.gameservicedemo.game.bag.bean;

import com.example.gameservicedemo.game.tools.bean.Tools;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/31/16:16
 * @Description: 背包中的一格
 */
@Data
public class Item {
    /**
     * 这个格子的id
     */
    Long id;
    Integer toolsIdType;
    /**
     * 这个格子在所属背包的位置
     */
    int indexInBag;
    /**
     * 背包中存放的物品。可以进行种类、数量的判断
     */
    List<Long> toolsUuidS=new ArrayList<>();

    public Item(Long id, Integer toolsIdType, int indexInBag) {
        this.id = id;
        this.toolsIdType = toolsIdType;
        this.indexInBag = indexInBag;
    }
}
