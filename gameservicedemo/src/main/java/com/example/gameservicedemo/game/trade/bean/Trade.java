package com.example.gameservicedemo.game.trade.bean;

import lombok.Data;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/11/15:29
 * @Description: 一场交易需要在数据库持久化的部分
 */
@Data
public class Trade {
    /**
     * 标识一场交易的唯一id
     */
    private Long id;
    private Integer initiatorId;
    private Integer accepterId;
    private String playerItemsJson;
    private String moneyMapJson;
    /**
     * 此次交易收取的费用（面对面交易不收取费用，拍卖行要收取费用）
     */
    private Integer cost;
    /** 交易时间 */
    private Date date;
    /**
     * 交易形式：面对面私下交易，进入拍卖行交易，拍卖行一口价交易。
     */
    private Integer tradeFormCode;
    /**
     * 假删除，用户可以查看历史交易记录，也可以删除历史记录。但是系统应该保存这部分数据。
     */
    private Integer state;
}
