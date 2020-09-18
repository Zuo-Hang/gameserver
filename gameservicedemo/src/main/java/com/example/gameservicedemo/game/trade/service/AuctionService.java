package com.example.gameservicedemo.game.trade.service;

import com.example.commondemo.base.RequestCode;
import com.example.gameservicedemo.base.IdGenerator;
import com.example.gameservicedemo.game.bag.bean.BagBeCache;
import com.example.gameservicedemo.game.bag.service.BagService;
import com.example.gameservicedemo.game.mail.bean.GameSystem;
import com.example.gameservicedemo.game.mail.service.MailService;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.service.PlayerDataService;
import com.example.gameservicedemo.game.player.service.PlayerLoginService;
import com.example.gameservicedemo.game.tools.bean.Tools;
import com.example.gameservicedemo.game.tools.service.ToolsService;
import com.example.gameservicedemo.game.trade.bean.Auction;
import com.example.gameservicedemo.game.trade.bean.TradeForm;
import com.example.gameservicedemo.game.trade.cache.AuctionCache;
import com.example.gameservicedemo.manager.NotificationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/16/11:18
 * @Description:0
 */
@Service
@Slf4j
public class AuctionService {
    @Autowired
    BagService bagService;
    @Autowired
    ToolsService toolsService;
    @Autowired
    GameSystem gameSystem;
    @Autowired
    AuctionCache auctionCache;
    @Autowired
    MailService mailService;
    @Autowired
    PlayerLoginService playerLoginService;
    @Autowired
    PlayerDataService playerDataService;
    @Autowired
    NotificationManager notificationManager;

    /**
     * 展示当前处于拍卖的所有物品
     *
     * @param player
     */
    public void auctionShow(PlayerBeCache player) {
        Collection<Auction> allAuction = auctionCache.getAllAuction();
        log.info("拍卖列表{}", allAuction);
        StringBuilder string = new StringBuilder("正在进行拍卖的列表如下：\n");
        if(allAuction.isEmpty()){
            notificationManager.notifyPlayer(player,"还没有进行拍卖的物品，请稍后查看！",RequestCode.SUCCESS.getCode());
            return;
        }
        allAuction.forEach(auction -> {
            if (auction.getAuctionMode().equals(TradeForm.SHELL_NOW.getCode())) {
                string.append(MessageFormat.format("id:{4}\n  名称：{0} 竞拍模式：一口价模式,先到先得 售价：{1} 发布人id{2} 发布时间:{3}",
                        toolsService.getToolsById(auction.getToolsId()).getName(),
                        auction.getBasePrice(),
                        auction.getPublisherId(),
                        auction.getPublishTime(),
                        auction.getId()
                ));
            } else {
                string.append(MessageFormat.format("id:{5}\n  名称：{0} 竞拍模式：价高者得 起拍价：{1} 当前最高价:{2} 发布人id{3} 发布时间:{4}",
                        toolsService.getToolsById(auction.getToolsId()).getName(),
                        auction.getBasePrice(),
                        auction.getAuctionPrice(),
                        auction.getPublisherId(),
                        auction.getPublishTime(),
                        auction.getId()
                ));
            }
        });
        notificationManager.notifyPlayer(player, string, RequestCode.SUCCESS.getCode());
    }

    /**
     * 拍卖结束后进行的操作
     * 物品交换，收税，返还竞价失败者的金钱，将这条交易记录持久化到数据库
     *
     * @param auction
     */
    public void finishAuction(Auction auction) {
        auctionCache.removeCache(auction.getId());
        Map<Integer, Integer> bidding = auction.getBidding();
        Tools toolsInCache = toolsService.getToolsById(auction.getToolsId());
        Tools tools = new Tools();
        BeanUtils.copyProperties(toolsInCache, tools);
        tools.setUuid(IdGenerator.getAnId());
        PlayerBeCache publisher = playerLoginService.getPlayerById(auction.getPublisherId());
        if (bidding.isEmpty()) {
            bagService.putInBag(publisher,tools);
            notificationManager.notifyPlayer(publisher, "物品发布时间结束，未有人参加交易，物品返还至你的邮箱，请注意查收", RequestCode.WARNING.getCode());
            mailService.sendMail(gameSystem, publisher.getId(), "有关拍卖", "拍卖物品返还",null);
            return;
        }
        mailService.sendMail(gameSystem, publisher.getId(), "有关拍卖", MessageFormat.format("此次拍卖成功,{0}卖出了{1}金币",
                tools.getName(),auction.getAuctionPrice()),null);
        bidding.forEach((playerId, bid) -> {
            publisher.setMoney(publisher.getMoney() + auction.getAuctionPrice());
            if (bid.equals(auction.getAuctionPrice())) {
                bagService.putInBag(playerLoginService.getPlayerById(playerId),tools);
                gameSystem.noticeSomeOne(playerId, "有关拍卖", "恭喜你，竞拍成功!", null);
            } else {
                PlayerBeCache bidders = playerLoginService.getPlayerById(playerId);
                //需要安全的
                bidders.setMoney(bidders.getMoney() + bid);
                gameSystem.noticeSomeOne(playerId, "有关拍卖", "竞拍失败，扣除的的金币已返还", null);
            }
        });
    }

    /**
     * 发布一件新的拍卖物品
     *
     * @param player    发布玩家
     * @param form      交易形式
     * @param toolsId   物品id
     * @param basePrice 起拍价
     */
    public void auctionPush(PlayerBeCache player, Integer form, Long toolsId, Integer basePrice) {
        BagBeCache bag = player.getBagBeCache();
        Tools tools = bagService.containsTools(bag, toolsId);
        if (Objects.isNull(tools)) {
            notificationManager.notifyPlayer(player, "你的背包中没有这件物品，请检查输入的id", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //移除物品
        bagService.removeFromBag(bag, toolsId);
        Auction auction = new Auction(IdGenerator.getAnId(), tools.getId(), basePrice, form, player.getId());
        auctionCache.putInCache(auction);
        log.info("{}发布了新的竞拍", player.getName());
        playerDataService.showPlayerBag(player);
        notificationManager.notifyPlayer(player, "你的物品已经成功委托到拍卖行，最晚一天后会通知交易结果。", RequestCode.SUCCESS.getCode());
    }

    /**
     * 卖家进行竞价
     *
     * @param buyer     卖家
     * @param auctionId 拍卖序号
     * @param bid       出价
     */
    public synchronized void auctionBid(PlayerBeCache buyer, Long auctionId, Integer bid) {
        Auction auction = auctionCache.getAuctionById(auctionId);
        if (Objects.isNull(auction)) {
            notificationManager.notifyPlayer(buyer, "你要找的物品已经不存在", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        if (bid <= auction.getAuctionPrice()) {
            notificationManager.notifyPlayer(buyer, "出价低于当前拍卖价值，叫价无效。", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        if (bid > buyer.getMoney()) {
            notificationManager.notifyPlayer(buyer, "支付不起出价，叫价无效。", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //-------------------------------叫价成功
        notificationManager.notifyPlayer(buyer, "叫价成功！", RequestCode.SUCCESS.getCode());

        //一口价模式
        if (auction.getAuctionMode().equals(TradeForm.SHELL_NOW.getCode())) {
            //线程安全的改变值
            buyer.setMoney(buyer.getMoney() - bid);
            playerDataService.showPlayerInfo(buyer);
            Tools toolsInCache = toolsService.getToolsById(auction.getToolsId());
            Tools tools = new Tools();
            BeanUtils.copyProperties(toolsInCache, tools);
            tools.setUuid(IdGenerator.getAnId());
            bagService.putInBag(buyer, tools);
            PlayerBeCache publisher = playerLoginService.getPlayerById(auction.getPublisherId());
            publisher.setMoney(publisher.getMoney() + bid);
            notificationManager.notifyPlayer(buyer, "交易达成，获取到的物品已经放到了你的背包里。", RequestCode.WARNING.getCode());
            notificationManager.notifyPlayer(publisher, "交易达成，卖出的金币已经更新。", RequestCode.WARNING.getCode());
            auctionCache.removeCache(auction.getId());
            //----------------------------------------------------------将这次交易持久化到数据库
        } else {
            Integer had = auction.getBidding().get(buyer.getId());
            if (Objects.isNull(had)) {
                //第一次竞拍
                buyer.setMoney(buyer.getMoney() - bid);
            } else {
                //只扣除这次加的差价
                buyer.setMoney(buyer.getMoney() - (bid - had));
            }
            playerDataService.showPlayerInfo(buyer);
            auction.getBidding().put(buyer.getId(), bid);
            auction.setAuctionPrice(bid);
            notificationManager.notifyPlayer(buyer, "竞拍模式！需等到拍卖结束才能得知花落谁家", RequestCode.WARNING.getCode());
        }
    }
}
