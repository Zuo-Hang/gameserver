package com.example.gameservicedemo.game.trade.bean;

import lombok.Data;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/11/11:59
 * @Description: 拍卖记录，记录一场拍卖的主要信息。
 */
@Data
public class Auction {

    private Integer thingInfoId;

    private Integer number;

    private Integer auctionPrice;

    private Integer auctionMode;
    /**
     * 发布时间
     */
    private Date publishTime;
    /**
     * 竞拍者 (以json的形式存储)
     */
    private String bidders;
    /**
     * 发布者id
     */
    private Integer publisherId;
}
