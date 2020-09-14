package com.example.gameservicedemo.game.trade.bean;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/11/15:39
 * @Description: 交易模式
 */
public enum TradeForm {
    FACE_TO_FACE(0,"面对面交易"),
    /**  竞价拍卖模式  */
    AT_AUCTION(1,"拍卖行竞价拍卖模式"),

    /** 一口价模式 */
    SHELL_NOW(2,"拍卖行一口价模式")
;
    Integer code;
    String describe;

    TradeForm(Integer code, String describe) {
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
