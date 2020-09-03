package com.example.gameservicedemo.game.tools.controller;

import com.example.commondemo.base.Command;
import com.example.commondemo.base.RequestCode;
import com.example.commondemo.message.Message;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.service.PlayerLoginService;
import com.example.gameservicedemo.game.tools.bean.Tools;
import com.example.gameservicedemo.base.controller.ControllerManager;
import com.example.gameservicedemo.manager.NotificationManager;
import com.example.gameservicedemo.game.player.service.PlayerService;
import com.example.gameservicedemo.game.shop.service.ShopService;
import com.example.gameservicedemo.game.tools.service.ToolsService;
import com.example.gameservicedemo.util.CheckParametersUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/19/10:27
 * @Description: 与装备有关的控制器
 */
@Controller
@Slf4j
public class ToolsController {
    @Autowired
    ShopService shopService;
    @Autowired
    PlayerService playerService;
    @Autowired
    PlayerLoginService playerLoginService;
    @Autowired
    NotificationManager notificationManager;
    @Autowired
    ToolsService toolsService;


    {
        ControllerManager.add(Command.SHOW_SHOP.getRequestCode(), this::showShop);
        ControllerManager.add(Command.SEE_TOOLS_INFO.getRequestCode(), this::seeToolsInfo);
        ControllerManager.add(Command.BUY_TOOLS.getRequestCode(), this::buyTools);
        ControllerManager.add(Command.WEAR_TOOLS.getRequestCode(), this::wearTools);
        ControllerManager.add(Command.TAKE_OFF_TOOLS.getRequestCode(), this::takeOffTools);
        ControllerManager.add(Command.REPLACE_TOOLS.getRequestCode(), this::replaceTools);
        ControllerManager.add(Command.FIX_TOOLS.getRequestCode(), this::fixTools);
        ControllerManager.add(Command.SELL_TOOLS.getRequestCode(), this::sellTools);
ControllerManager.add(Command.USE_MEDICINE.getRequestCode(),this::useMedicine);
    }

    private void useMedicine(ChannelHandlerContext context, Message message) {
        String[] parameter = CheckParametersUtil.checkParameter(context, message, 2);
        PlayerBeCache load = playerLoginService.isLoad(context);
        if(Objects.isNull(load)){
            notificationManager.notifyByCtx(context,"你还未加载化身！",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        if(Objects.isNull(parameter)){
            notificationManager.notifyPlayer(load,"你输入的参数个数错误！",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        toolsService.useMedicine(load,Long.valueOf(parameter[1]));
    }

    /**
     * 查看商店  ok
     *
     * @param context
     * @param message
     */
    public void showShop(ChannelHandlerContext context, Message message) {
        CheckParametersUtil.checkParameter(context, message, 1);
        shopService.showShop(context);
    }

    /**
     * 查看装备详情  ok
     *
     * @param context
     * @param message 指令 ToolsId
     */
    public void seeToolsInfo(ChannelHandlerContext context, Message message) {
        String[] strings = CheckParametersUtil.checkParameter(context, message, 2);
        toolsService.showToolsInfo(context, Integer.valueOf(strings[1]));
    }

    /**
     * 购买装备 ok
     *
     * @param context
     * @param message
     */
    public void buyTools(ChannelHandlerContext context, Message message) {
        String[] strings = CheckParametersUtil.checkParameter(context, message, 2);
        playerService.buyTools(context, Integer.valueOf(strings[1]));
    }

    /**
     * 穿戴装备（加入装备栏）
     *
     * @param context
     * @param message
     */
    public void wearTools(ChannelHandlerContext context, Message message) {
        String[] strings = CheckParametersUtil.checkParameter(context, message, 2);
        if(Objects.isNull(strings)){return;}
        //从用户获取背包后从背包中再获取到装备
        PlayerBeCache playerByContext = playerLoginService.getPlayerByContext(context);
        Tools tools = playerByContext.getBagBeCache().getToolsMap().get(Long.valueOf(strings[1]));
        if (Objects.isNull(tools)) {
            notificationManager.notifyPlayer(playerByContext, "你的背包中还没有这件装备哦", RequestCode.BAD_REQUEST.getCode());
            return ;
        }
        toolsService.wearTools(playerByContext, tools);
    }

    /**
     * 卸下装备
     *
     * @param context
     * @param message
     */
    public void takeOffTools(ChannelHandlerContext context, Message message) {
        String[] strings = CheckParametersUtil.checkParameter(context, message, 2);
        if(Objects.isNull(strings)){return;}
        PlayerBeCache playerByContext = playerLoginService.getPlayerByContext(context);
        Tools tools = playerByContext.getEquipmentBar().get(Long.valueOf(strings[1]));
        if (Objects.isNull(tools)) {
            notificationManager.notifyPlayer(playerByContext, "你的装备栏并没有这件装备哦", RequestCode.BAD_REQUEST.getCode());
            return ;
        }
        toolsService.takeOffTools(playerByContext, tools);
    }

    /**
     * 更换装备
     *
     * @param context
     * @param message
     */
    public void replaceTools(ChannelHandlerContext context, Message message) {
        String[] strings = CheckParametersUtil.checkParameter(context, message, 3);
        Tools toolsOut = toolsService.getToolsById(Integer.valueOf(strings[1]));
        Tools toolsIn = toolsService.getToolsById(Integer.valueOf(strings[2]));
        toolsService.replaceTools(playerLoginService.getPlayerByContext(context), toolsOut,toolsIn);
    }

    /**
     * 修理装备
     *
     * @param context
     * @param message
     */
    public void fixTools(ChannelHandlerContext context, Message message) {
        String[] strings = CheckParametersUtil.checkParameter(context, message, 2);
        Long toolsUuid = Long.valueOf(strings[1]);
        toolsService.fixTools(playerLoginService.getPlayerByContext(context),toolsUuid);
    }

    /**
     * 回收装备
     *
     * @param context
     * @param message
     */
    public void sellTools(ChannelHandlerContext context, Message message) {
        String[] strings = CheckParametersUtil.checkParameter(context, message, 2);
        Long tollsUuid = Long.valueOf(strings[1]);
        toolsService.sellTools(playerLoginService.getPlayerByContext(context),tollsUuid);
    }
}
