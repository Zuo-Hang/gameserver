package com.example.gameservicedemo.game.trade.service;

import com.example.commondemo.base.RequestCode;
import com.example.gameservicedemo.game.bag.bean.BagBeCache;
import com.example.gameservicedemo.game.bag.service.BagService;
import com.example.gameservicedemo.game.mail.bean.GameSystem;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.service.PlayerDataService;
import com.example.gameservicedemo.game.player.service.PlayerLoginService;
import com.example.gameservicedemo.game.tools.bean.Tools;
import com.example.gameservicedemo.game.trade.bean.TradeBoard;
import com.example.gameservicedemo.game.trade.bean.TradeState;
import com.example.gameservicedemo.game.trade.cache.TradeCache;
import com.example.gameservicedemo.manager.NotificationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/11/11:46
 * @Description:
 */
@Service
@Slf4j
public class TradeService {

    @Autowired
    PlayerLoginService playerLoginService;
    @Autowired
    GameSystem gameSystem;
    @Autowired
    BagService bagService;
    @Autowired
    PlayerDataService playerDataService;
    @Autowired
    TradeCache tradeCache;
    @Autowired
    NotificationManager notificationManager;

    /**
     * 确认交易
     *
     * @param player
     */
    public void tradeConfirm(PlayerBeCache player,Long tradeId) {
        TradeBoard tradeBoard = tradeCache.getTradeBoardByPlayerId(tradeId);
        if(Objects.isNull(tradeBoard)){
            notificationManager.notifyPlayer(player,"该交易不存在，请检查id或交易有效时间",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        if((!player.equals(tradeBoard.getInitiator()))&&(!player.equals(tradeBoard.getAccepter()))){
            notificationManager.notifyPlayer(player,"该交易与你无关，无权确认！",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        boolean[] confirm = tradeBoard.getConfirm();
        if(player.equals(tradeBoard.getInitiator())){
            confirm[0]=true;
            notificationManager.notifyPlayer(tradeBoard.getInitiator(),"已确认当前交易！",RequestCode.WARNING.getCode());
        }
        if(player.equals(tradeBoard.getAccepter())){
            confirm[1]=true;
            notificationManager.notifyPlayer(tradeBoard.getAccepter(),"已确认当前交易！",RequestCode.WARNING.getCode());
        }
        //如果双方都同意,执行交易
        if(confirm[0]&&confirm[1]){
            boolean b = doTrade(tradeBoard);
            notificationManager.notifyPlayer(tradeBoard.getInitiator(),"交易成功",RequestCode.SUCCESS.getCode());
            notificationManager.notifyPlayer(tradeBoard.getAccepter(),"交易成功",RequestCode.SUCCESS.getCode());
            //更新数据库----------------插入一条新的交易信息
        }
    }

    /**
     * 执行交易（扣除金币、交换货物、保存交易记录）
     * 在某一步失败之后，会进行之前操作的反向操作以确保用户信息的安全性。
     * @param tradeBoard
     */
    public boolean doTrade(TradeBoard tradeBoard){
        tradeBoard.getPlayerTools().forEach((playerId,toolsMap)->{
            PlayerBeCache player;
            if(playerId.equals(tradeBoard.getInitiator().getId())){
                player=tradeBoard.getAccepter();
            }else{
                player=tradeBoard.getInitiator();
            }
            toolsMap.values().forEach(tools -> {
                bagService.putInBag(player,tools);
            });
        });
        tradeBoard.getInitiator().setMoney(tradeBoard.getInitiator().getMoney()+tradeBoard.getMoneyMap().get(tradeBoard.getAccepter().getId()));
        tradeBoard.getAccepter().setMoney(tradeBoard.getAccepter().getMoney()+tradeBoard.getMoneyMap().get(tradeBoard.getInitiator().getId()));
        tradeBoard.setState(TradeState.FINISH.getCode());
        playerDataService.showPlayerBag(tradeBoard.getInitiator());
        playerDataService.showPlayerBag(tradeBoard.getAccepter());
        playerDataService.showPlayerInfo(tradeBoard.getInitiator());
        playerDataService.showPlayerInfo(tradeBoard.getAccepter());
        return true;
    }
    /**
     * 发起交易
     *
     * @param seller
     */
    public void tradeInitiate(PlayerBeCache seller, Integer buyerId) {
        PlayerBeCache buyer = playerLoginService.getPlayerById(buyerId);
        if (Objects.isNull(buyer)) {
            notificationManager.notifyPlayer(seller, "该玩家不存在，请检查输入id", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //创建一场新的交易
        TradeBoard tradeBoard = new TradeBoard(seller, buyer);
        //将交易缓存
        tradeCache.putCache(tradeBoard);
        notificationManager.notifyPlayer(seller, MessageFormat.format("你已经向{0}发起了交易请求,交易id为：{1}。请在三分钟内完成交易流程\n",
                buyer.getName(),tradeBoard.getId()), RequestCode.SUCCESS.getCode());
        gameSystem.noticeSomeOne(buyerId, "交易相关", MessageFormat.format("收到{0}交易请求,交易id为：{1}。如果同意开始交易，请回复 `trade_begin 交易id`，并在三分钟内完成交易流程\n",
                seller.getName(), tradeBoard.getId()), null);
    }

    /**
     * 放入物品
     * @param player
     * @param tradeId
     * @param toolsId
     */
    public void tradeTools(PlayerBeCache player, Long tradeId, Long toolsId) {
        TradeBoard tradeBoard = tradeCache.getTradeBoardByPlayerId(tradeId);
        if(Objects.isNull(tradeBoard)){
            notificationManager.notifyPlayer(player,"该交易不存在，请检查id或交易有效时间",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        BagBeCache bagBeCache = player.getBagBeCache();
        Tools tools = bagService.containsTools(bagBeCache, toolsId);
        if(Objects.isNull(tools)){
            notificationManager.notifyPlayer(player,"你的背包中不存在该物品",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        tradeBoard.getPlayerTools().get(player.getId()).put(toolsId,tools);
        bagService.removeFromBag(player.getBagBeCache(),toolsId);
        playerDataService.showPlayerBag(player);
        notificationManager.notifyPlayer(player,"出价成功！",RequestCode.SUCCESS.getCode());
        PlayerBeCache anotherPlayer=player.equals(tradeBoard.getInitiator())?tradeBoard.getAccepter():tradeBoard.getInitiator();
        notificationManager.notifyPlayer(anotherPlayer,MessageFormat.format("交易{0}另一方出价成功，使用'trade_see'查看详情",
                tradeBoard.getId()),RequestCode.SUCCESS.getCode());
    }

    /**
     * 开始交易
     * @param buyer
     * @param tradeId
     */
    public void tradeBegin(PlayerBeCache buyer, Long tradeId) {
        TradeBoard tradeBoard = tradeCache.getTradeBoardByPlayerId(tradeId);
        if(Objects.isNull(tradeBoard)){
            notificationManager.notifyPlayer(buyer,"该交易不存在，请检查id或交易有效时间",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //更改交易状态为交易进行中
        tradeBoard.setState(TradeState.TRADING.getCode());
        PlayerBeCache seller = tradeBoard.getInitiator();
        notificationManager.notifyPlayer(seller,
                MessageFormat.format("{0} 同意了你的交易请求，请用`trade_tools`指令来开价愿意交易的货物\n",
                        buyer.getName()),RequestCode.WARNING.getCode());
        notificationManager.notifyPlayer(buyer,"你已同意交易,请用`trade_money`指令来开价此次交易愿意支付的金币\n",RequestCode.SUCCESS.getCode());
    }

    /**
     * 使用金币出价
     * @param player
     * @param tradeId
     * @param count
     */
    public void tradeMoney(PlayerBeCache player, Long tradeId, Integer count) {
        TradeBoard tradeBoard = tradeCache.getTradeBoardByPlayerId(tradeId);
        if(Objects.isNull(tradeBoard)){
            notificationManager.notifyPlayer(player,"该交易不存在，请检查id或交易有效时间",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        if(count>player.getMoney()){
            notificationManager.notifyPlayer(player,"你出了一个你支付不起的价格！",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        tradeBoard.getMoneyMap().put(player.getId(),tradeBoard.getMoneyMap().get(player.getId())+count);
        //使用线程安全的方式扣除金币
        player.setMoney(player.getMoney()-count);
        playerDataService.showPlayerInfo(player);
        notificationManager.notifyPlayer(player,"出价成功！",RequestCode.SUCCESS.getCode());
        PlayerBeCache anotherPlayer=player.equals(tradeBoard.getInitiator())?tradeBoard.getAccepter():tradeBoard.getInitiator();
        notificationManager.notifyPlayer(anotherPlayer,MessageFormat.format("交易{0}另一方出价成功，使用'trade_see'查看详情",
                tradeBoard.getId()),RequestCode.SUCCESS.getCode());
    }

    /**
     * 查看一场交易的出价
     * @param player
     * @param tradeId
     */
    public void tradeSee(PlayerBeCache player, Long tradeId) {
        TradeBoard tradeBoard = tradeCache.getTradeBoardByPlayerId(tradeId);
        if(Objects.isNull(tradeBoard)){
            notificationManager.notifyPlayer(player,"该交易不存在，请检查id或交易有效时间",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        if((!player.equals(tradeBoard.getInitiator()))&&(!player.equals(tradeBoard.getAccepter()))){
            notificationManager.notifyPlayer(player,"该交易与你无关，无权查看！",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        String string = MessageFormat.format("交易id:{0},创建时间：{1}\n", tradeBoard.getId(), tradeBoard.getDate());
        StringBuilder stringBuilder;

        stringBuilder = new StringBuilder(string);
        stringBuilder.append(MessageFormat.format("{0}出价为：\n物品：", tradeBoard.getInitiator().getName()));
        tradeBoard.getPlayerTools().get(tradeBoard.getInitiator().getId()).values().forEach(tools->{
            stringBuilder.append(tools.getName());
        });
        stringBuilder.append(MessageFormat.format("\n金钱：{0}\n",tradeBoard.getMoneyMap().get(tradeBoard.getInitiator().getId())));
        stringBuilder.append(MessageFormat.format("{0}出价为：\n物品：", tradeBoard.getAccepter().getName()));
        tradeBoard.getPlayerTools().get(tradeBoard.getAccepter().getId()).values().forEach(tools->{
            stringBuilder.append(tools.getName());
        });
        stringBuilder.append(MessageFormat.format("\n金钱：{0}\n",tradeBoard.getMoneyMap().get(tradeBoard.getAccepter().getId())));

        notificationManager.notifyPlayer(player,stringBuilder,RequestCode.SUCCESS.getCode());
    }
}
