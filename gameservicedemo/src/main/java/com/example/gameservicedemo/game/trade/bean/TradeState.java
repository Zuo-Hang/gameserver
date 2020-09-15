package com.example.gameservicedemo.game.trade.bean;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/15/21:10
 * @Description:
 */
public enum TradeState {
    TO_BEGIN(0,"交易未开始"),
    TRADING(1,"交易进行中"),
    FINISH(2,"交易完成")
    ;
    Integer code;
    String describe;

    TradeState(Integer code, String describe) {
        this.code = code;
        this.describe = describe;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescribe() {
        return describe;
    }
}
