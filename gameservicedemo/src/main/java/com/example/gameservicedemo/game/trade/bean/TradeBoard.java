package com.example.gameservicedemo.game.trade.bean;

import com.example.gameservicedemo.base.IdGenerator;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.tools.bean.Tools;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/11/12:14
 * @Description: 交易板，保存维护一场交易中产生的信息
 */
@Data
public class TradeBoard extends Trade{
    /** 发起交易的玩家 */
    private PlayerBeCache initiator;

    /** 接受交易的玩家 */
    private PlayerBeCache accepter;

    public TradeBoard(PlayerBeCache initiator, PlayerBeCache accepter) {
        super();
        this.initiator = initiator;
        this.accepter = accepter;
        //设置一个唯一的id
        setId(IdGenerator.getAnId());
        //表示是面对面交易
        setTradeFormCode(TradeForm.FACE_TO_FACE.getCode());
        setCost(0);
        // 初始化金币
        this.moneyMap = new ConcurrentHashMap<>();
        moneyMap.put(initiator.getId(),0);
        moneyMap.put(accepter.getId(),0);
        this.playerItems = new ConcurrentHashMap<>();
        playerItems.put(initiator.getId(),new ConcurrentHashMap<>());
        playerItems.put(accepter.getId(),new ConcurrentHashMap<>());
    }
    /**
     * 需要交易的物品,键为放置者，交易成功时物品将给另一方,key为玩家id
     */
    private Map<Integer, Map<Long, Tools>> playerItems ;


    /**
     * 需要交易的金币，key为玩家id
     */

    private Map<Integer,Integer> moneyMap;
}
