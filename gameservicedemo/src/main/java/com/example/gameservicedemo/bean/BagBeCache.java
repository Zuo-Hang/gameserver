package com.example.gameservicedemo.bean;

import com.example.gamedatademo.bean.Bag;
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
    Map<Integer,Item> itemMap = new ConcurrentSkipListMap<>();

}
