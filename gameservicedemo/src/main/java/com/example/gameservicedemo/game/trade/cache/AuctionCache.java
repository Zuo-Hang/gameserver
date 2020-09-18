package com.example.gameservicedemo.game.trade.cache;

import com.example.gameservicedemo.game.trade.bean.Auction;
import com.example.gameservicedemo.game.trade.service.AuctionService;
import com.example.gameservicedemo.manager.TimedTaskManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/16/15:55
 * @Description: 缓存所有处在拍卖过程当中的拍卖事项
 */
@Component
@Slf4j
public class AuctionCache {
    @Autowired
    AuctionService auctionService;

    private Map<Long, Auction> auctionCache = new ConcurrentHashMap<>();

    /**
     * 加入缓存
     *
     * @param auction
     */
    public void putInCache(Auction auction) {
        auctionCache.put(auction.getId(), auction);
    }

    /**
     * 获取
     *
     * @param id
     * @return
     */
    public Auction getAuctionById(Long id) {
        return auctionCache.get(id);
    }

    /**
     * 获取所有
     *
     * @return
     */
    public Collection<Auction> getAllAuction() {
        return auctionCache.values();
    }

    public void removeCache(Long id){
        auctionCache.remove(id);
    }

    @PostConstruct
    private void doBackGround() {
        //定时查看这些拍卖是否结束了
        TimedTaskManager.scheduleAtFixedRate(5000, 500, () -> {
                    try {
                        auctionCache.forEach((k,auction) -> {
                                    log.debug("拍卖品{}", auction);
                                    // 如果拍卖品被拍卖超过一天，结束拍卖
                                    if (System.currentTimeMillis() - auction.getPublishTime().getTime() >
                                            2*60*1000) {
                                        //执行拍卖结束的相关事宜
                                        auctionService.finishAuction(auction);
                                    }
                                }
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }

}
