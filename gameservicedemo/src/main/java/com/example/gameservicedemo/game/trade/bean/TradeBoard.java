package com.example.gameservicedemo.game.trade.bean;

import com.example.gameservicedemo.base.IdGenerator;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.tools.bean.Tools;
import com.example.gameservicedemo.game.trade.service.TradeService;
import lombok.Data;

import java.util.Date;
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
    /** 用来标记是否双方同意最终交易条件 */
    private boolean[] confirm=new boolean[2];
    /** 接受交易的玩家 */
    private PlayerBeCache accepter;
    /** 交易状态 */
    private Integer state;

    public TradeBoard(PlayerBeCache initiator, PlayerBeCache accepter) {
        super();
        this.initiator = initiator;
        this.accepter = accepter;
        //设置一个唯一的id
        setId(IdGenerator.getAnId());
        //表示是面对面交易
        setTradeFormCode(TradeForm.FACE_TO_FACE.getCode());
        //交易时间
        setDate(new Date());
        //标记这场交易需要交的税为0
        setCost(0);
        setState(TradeState.TO_BEGIN.getCode());
        // 初始化金币
        this.moneyMap = new ConcurrentHashMap<>();
        moneyMap.put(initiator.getId(),0);
        moneyMap.put(accepter.getId(),0);
        //初始化物品
        this.playerTools = new ConcurrentHashMap<>();
        playerTools.put(initiator.getId(),new ConcurrentHashMap<>());
        playerTools.put(accepter.getId(),new ConcurrentHashMap<>());
    }
    /**
     * 需要交易的物品,键为放置者，交易成功时物品将给另一方,key为玩家id
     */
    private Map<Integer, Map<Long, Tools>> playerTools ;
    /**
     * <playerId,出价>
     */
    private Map<Integer,Integer> moneyMap;
}
