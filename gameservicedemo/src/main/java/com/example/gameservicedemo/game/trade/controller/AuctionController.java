package com.example.gameservicedemo.game.trade.controller;

import com.example.commondemo.base.Command;
import com.example.commondemo.base.RequestCode;
import com.example.commondemo.message.Message;
import com.example.gameservicedemo.base.controller.ControllerManager;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.service.PlayerLoginService;
import com.example.gameservicedemo.game.trade.service.AuctionService;
import com.example.gameservicedemo.manager.NotificationManager;
import com.example.gameservicedemo.util.CheckParametersUtil;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/16/11:17
 * @Description: 拍卖行控制器
 */
@Component
public class AuctionController {
    @Autowired
    AuctionService auctionService;
    @Autowired
    PlayerLoginService playerLoginService;
    @Autowired
    NotificationManager notificationManager;

    {
        ControllerManager.add(Command.AUCTION_PUSH.getRequestCode(),this::auctionPush);
        ControllerManager.add(Command.AUCTION_SHOW.getRequestCode(),this::auctionShow);
        ControllerManager.add(Command.AUCTION_BID.getRequestCode(),this::auctionBid);
    }

    private void auctionBid(ChannelHandlerContext context, Message message) {
        PlayerBeCache player = playerLoginService.isLoad(context);
        if(Objects.isNull(player)){
            notificationManager.notifyByCtx(context,"你还未加载角色，不能进行此项操作！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //指令 拍卖序号 出价
        String[] strings = CheckParametersUtil.checkParameter(context, message, 3);
        if(Objects.isNull(strings)){
            notificationManager.notifyPlayer(player,"输入的参数个数不对！",RequestCode.BAD_REQUEST.getCode());
        }
        auctionService.auctionBid(player,Long.valueOf(strings[1]),Integer.valueOf(strings[2]));
    }

    private void auctionShow(ChannelHandlerContext context, Message message) {
        PlayerBeCache player = playerLoginService.isLoad(context);
        if(Objects.isNull(player)){
            notificationManager.notifyByCtx(context,"你还未加载角色，不能进行此项操作！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        String[] strings = CheckParametersUtil.checkParameter(context, message, 1);
        if(Objects.isNull(strings)){
            notificationManager.notifyPlayer(player,"输入的参数个数不对！",RequestCode.BAD_REQUEST.getCode());
        }
        auctionService.auctionShow(player);
    }

    private void auctionPush(ChannelHandlerContext context, Message message) {
        PlayerBeCache player = playerLoginService.isLoad(context);
        if(Objects.isNull(player)){
            notificationManager.notifyByCtx(context,"你还未加载角色，不能进行此项操作！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //指令 拍卖模式 物品 价格
        String[] strings = CheckParametersUtil.checkParameter(context, message, 4);
        if(Objects.isNull(strings)){
            notificationManager.notifyPlayer(player,"输入的参数个数不对！",RequestCode.BAD_REQUEST.getCode());
        }
        auctionService.auctionPush(player,Integer.valueOf(strings[1]),Long.valueOf(strings[2]),Integer.valueOf(strings[3]));
    }
}
