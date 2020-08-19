package com.example.gameservicedemo.controller.impl;

import com.example.commondemo.base.Command;
import com.example.commondemo.message.Message;
import com.example.gameservicedemo.bean.shop.Tools;
import com.example.gameservicedemo.controller.ControllerManager;
import com.example.gameservicedemo.manager.NotificationManager;
import com.example.gameservicedemo.service.PlayerService;
import com.example.gameservicedemo.service.ShopService;
import com.example.gameservicedemo.service.ToolsService;
import com.example.gameservicedemo.util.CheckParametersUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

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

    }

    /**
     * 查看商店
     *
     * @param context
     * @param message
     */
    public void showShop(ChannelHandlerContext context, Message message) {
        CheckParametersUtil.checkParameter(context, message, 1);
        shopService.showShop(context);
    }

    /**
     * 查看装备详情
     *
     * @param context
     * @param message 指令 ToolsId
     */
    public void seeToolsInfo(ChannelHandlerContext context, Message message) {
        String[] strings = CheckParametersUtil.checkParameter(context, message, 2);
        toolsService.showToolsInfo(context, Integer.valueOf(strings[1]));
    }

    /**
     * 购买装备
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
        Tools toolsById = toolsService.getToolsById(Integer.valueOf(strings[1]));
        toolsService.wearTools(playerService.getPlayerByContext(context), toolsById);
    }

    /**
     * 卸下装备
     *
     * @param context
     * @param message
     */
    public void takeOffTools(ChannelHandlerContext context, Message message) {
        String[] strings = CheckParametersUtil.checkParameter(context, message, 2);
        Tools toolsById = toolsService.getToolsById(Integer.valueOf(strings[1]));
        toolsService.takeOffTools(playerService.getPlayerByContext(context), toolsById);
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
        toolsService.replaceTools(playerService.getPlayerByContext(context), toolsOut,toolsIn);
    }

    /**
     * 修理装备
     *
     * @param context
     * @param message
     */
    public void fixTools(ChannelHandlerContext context, Message message) {
        String[] strings = CheckParametersUtil.checkParameter(context, message, 2);
        Integer tollsId = Integer.valueOf(strings[1]);
        toolsService.fixTools(playerService.getPlayerByContext(context),tollsId);
    }

    /**
     * 回收装备
     *
     * @param context
     * @param message
     */
    public void sellTools(ChannelHandlerContext context, Message message) {
        String[] strings = CheckParametersUtil.checkParameter(context, message, 2);
        Integer tollsId = Integer.valueOf(strings[1]);
        toolsService.sellTools(playerService.getPlayerByContext(context),tollsId);
    }
}
