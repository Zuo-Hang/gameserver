package com.example.gameservicedemo.game.trade.controller;

import com.example.commondemo.base.Command;
import com.example.commondemo.base.RequestCode;
import com.example.commondemo.message.Message;
import com.example.gameservicedemo.base.controller.ControllerManager;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.service.PlayerLoginService;
import com.example.gameservicedemo.game.trade.service.TradeService;
import com.example.gameservicedemo.manager.NotificationManager;
import com.example.gameservicedemo.util.CheckParametersUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/11/11:45
 * @Description:
 */
@Component
@Slf4j
public class TradeController {
    @Autowired
    PlayerLoginService playerLoginService;
    @Autowired
    NotificationManager notificationManager;
    @Autowired
    TradeService tradeService;

    {
        ControllerManager.add(Command.TRADE_INITIATE.getRequestCode(), this::tradeInitiate);
        ControllerManager.add(Command.TRADE_BEGIN.getRequestCode(), this::tradeBegin);
        ControllerManager.add(Command.TRADE_TOOLS.getRequestCode(), this::tradeTools);
        ControllerManager.add(Command.TRADE_MONEY.getRequestCode(), this::tradeMoney);
        ControllerManager.add(Command.TRADE_CONFIRM.getRequestCode(), this::tradeConfirm);
        ControllerManager.add(Command.TRADE_SEE.getRequestCode(),this::tradeSee);
    }

    private void tradeSee(ChannelHandlerContext context, Message message) {
        PlayerBeCache load = playerLoginService.isLoad(context);
        if (Objects.isNull(load)) {
            notificationManager.notifyByCtx(context, "你还未加载角色，不能进行此项操作！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //指令 tradeId
        String[] strings = CheckParametersUtil.checkParameter(context, message, 2);
        if (Objects.isNull(strings)) {
            notificationManager.notifyPlayer(load, "输入的参数个数不对", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        tradeService.tradeSee(load,Long.valueOf(strings[1]));
    }

    private void tradeInitiate(ChannelHandlerContext context, Message message) {
        PlayerBeCache load = playerLoginService.isLoad(context);
        if (Objects.isNull(load)) {
            notificationManager.notifyByCtx(context, "你还未加载角色，不能进行此项操作！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //指令 买家的id
        String[] strings = CheckParametersUtil.checkParameter(context, message, 2);
        if (Objects.isNull(strings)) {
            notificationManager.notifyPlayer(load, "输入的参数个数不对", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        tradeService.tradeInitiate(load,Integer.valueOf(strings[1]));
    }

    private void tradeConfirm(ChannelHandlerContext context, Message message) {
        PlayerBeCache load = playerLoginService.isLoad(context);
        if (Objects.isNull(load)) {
            notificationManager.notifyByCtx(context, "你还未加载角色，不能进行此项操作！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //指令 交易id
        String[] strings = CheckParametersUtil.checkParameter(context, message, 2);
        if (Objects.isNull(strings)) {
            notificationManager.notifyPlayer(load, "输入的参数个数不对", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        tradeService.tradeConfirm(load,Long.valueOf(strings[1]));
    }

    private void tradeMoney(ChannelHandlerContext context, Message message) {
        PlayerBeCache load = playerLoginService.isLoad(context);
        if (Objects.isNull(load)) {
            notificationManager.notifyByCtx(context, "你还未加载角色，不能进行此项操作！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //指令 交易id 出价金币
        String[] strings = CheckParametersUtil.checkParameter(context, message, 3);
        if (Objects.isNull(strings)) {
            notificationManager.notifyPlayer(load, "输入的参数个数不对", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        tradeService.tradeMoney(load,Long.valueOf(strings[1]),Integer.valueOf(strings[2]));
    }

    private void tradeTools(ChannelHandlerContext context, Message message) {
        PlayerBeCache load = playerLoginService.isLoad(context);
        if (Objects.isNull(load)) {
            notificationManager.notifyByCtx(context, "你还未加载角色，不能进行此项操作！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //指令 交易id 物品id
        String[] strings = CheckParametersUtil.checkParameter(context, message, 3);
        if (Objects.isNull(strings)) {
            notificationManager.notifyPlayer(load, "输入的参数个数不对", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        tradeService.tradeTools(load,Long.valueOf(strings[1]),Long.valueOf(strings[2]));
    }

    private void tradeBegin(ChannelHandlerContext context, Message message) {
        PlayerBeCache load = playerLoginService.isLoad(context);
        if (Objects.isNull(load)) {
            notificationManager.notifyByCtx(context, "你还未加载角色，不能进行此项操作！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        String[] strings = CheckParametersUtil.checkParameter(context, message, 2);
        if (Objects.isNull(strings)) {
            notificationManager.notifyPlayer(load, "输入的参数个数不对", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        tradeService.tradeBegin(load,Long.valueOf(strings[1]));

    }

}
