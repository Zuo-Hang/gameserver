package com.example.gameservicedemo.game.trade.service;

import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.service.PlayerLoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    /**
     * 确认交易
     * @param load
     */
    public void tradeConfirm(PlayerBeCache load) {
    }

    /**
     * 发起交易
     * @param seller
     */
    public void tradeInitiate(PlayerBeCache seller,Integer buyerId) {
        PlayerBeCache buyer = playerLoginService.getPlayerById(buyerId);
        if(Objects.isNull(buyer)){

        }


    }

    /**
     * 放入物品
     * @param load
     * @param valueOf
     */
    public void tradeTools(PlayerBeCache load, Long valueOf) {
    }
}
