package com.example.gameservicedemo.game.player.service;

import com.example.commondemo.base.RequestCode;
import com.example.gameservicedemo.base.IdGenerator;
import com.example.gameservicedemo.game.bag.service.BagService;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.tools.bean.Tools;
import com.example.gameservicedemo.game.player.cache.PlayerCache;
import com.example.gameservicedemo.game.scene.bean.NPC;
import com.example.gameservicedemo.manager.NotificationManager;
import com.example.gameservicedemo.game.scene.service.SceneService;
import com.example.gameservicedemo.game.tools.service.ToolsService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/05/16:46
 * @Description: 角色化身行为有关的服务
 */
@Slf4j
@Service
public class PlayerService {
    @Autowired
    PlayerCache playerCache;
    @Autowired
    SceneService sceneService;
    @Autowired
    ToolsService toolsService;
    @Autowired
    BagService bagService;
    @Autowired
    PlayerDataService playerDataService;
    @Autowired
    PlayerLoginService playerLoginService;
    @Autowired
    NotificationManager notificationManager;

    /**
     * 获取周边环境(展示同一场景内的NPC)
     *
     * @param context
     */
    public void aoi(ChannelHandlerContext context) {
        //获取当前场景的所有实体信息
        PlayerBeCache playerByCtx = playerCache.getPlayerByChannel(context.channel());
        Integer nowAt = playerByCtx.getNowAt();
        Map<Integer, NPC> npcs = sceneService.getScene(nowAt).getNpcs();
        Collection<NPC> values = npcs.values();
        if (values == null) {
            notificationManager.notifyByCtx(context, "这里空无一人！", RequestCode.SUCCESS.getCode());
            return;
        }
        StringBuffer ret = new StringBuffer();
        ret.append("\nnpc如下：\n");
        if (values != null) {
            for (NPC objectsId : values) {
                ret.append(objectsId.displayData() + "\n");
            }
        }
        notificationManager.notifyByCtx(context, ret.toString(), RequestCode.SUCCESS.getCode());
    }
    /**
     * 购买装备
     * 钱（判断、更改）
     * @param context
     * @param toolsId
     */
    public void buyTools(ChannelHandlerContext context,Integer toolsId){
        //判断是否登录
        if(Objects.isNull(playerLoginService.isLoad(context))){return;}
        PlayerBeCache playerByContext = playerLoginService.getPlayerByContext(context);
        Tools toolsById = toolsService.getToolsById(toolsId);
        //判断金币是否足够
        if(playerByContext.getMoney()<toolsById.getPriceIn()){
            notificationManager.notifyByCtx(context,"你的金币还不足以购买此商品",RequestCode.WARNING.getCode());
            return;
        }
        //创建一个新的道具放入背包，因为后期可能改变这个道具的某些属性，所以不能使用缓存中的道具对象
        Tools newTools = new Tools();
        BeanUtils.copyProperties(toolsById,newTools);
        //设置唯一的UUID
        newTools.setUuid(IdGenerator.getAnId());
        String msg="不能放入背包，购买失败！";
        //放入背包
        if(bagService.putInBag(playerByContext,newTools)){
            //扣除金币
            playerByContext.setMoney(playerByContext.getMoney()-toolsService.getToolsById(toolsId).getPriceIn());
            msg=MessageFormat.format("你已成功购买了道具{0}，新道具已经放入你的背包了，你可以使用\"see_player_bag\"查看",toolsService.getToolsById(toolsId).getName());
        }
        notificationManager.notifyByCtx(context,msg,RequestCode.SUCCESS.getCode());
        playerDataService.showPlayerInfo(playerByContext);
    }
}
