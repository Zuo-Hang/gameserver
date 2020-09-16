package com.example.gameservicedemo.game.trade.bean;

import lombok.Data;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/11/11:59
 * @Description: 拍卖事项，记录一场拍卖的主要信息。 继承自交易，因为拍卖属于特殊的交易。
 */
@Data
public class Auction extends Trade {
    /** 标识物品的种类 */
    private Integer toolsId;

    /** 起拍价 */
    private Integer basePrice;

    /** 当前价 每次竞价都要高于当前价 */
    private Integer auctionPrice;

    /** 一口价还是竞拍 */
    private Integer auctionMode;
    /**
     * 发布时间
     */
    private Date publishTime;
    /**
     * 竞价 保存所有的竞价（只有拍卖模式才会用到）
     */
    private Map<Integer,Integer> bidding=new ConcurrentHashMap<>();
    /**
     * 发布者id
     */
    private Integer publisherId;

    public Auction(Long id,Integer toolsId, Integer basePrice, Integer auctionMode,  Integer publisherId) {
        setId(id);
        this.toolsId = toolsId;
        this.basePrice=basePrice;
        this.auctionPrice = basePrice;
        this.auctionMode = auctionMode;
        this.publisherId = publisherId;
        publishTime=new Date();
    }

    public Auction() {
    }
}
