package com.example.gameservicedemo.game.shop.service;

import com.example.commondemo.base.RequestCode;
import com.example.gameservicedemo.game.shop.bean.Shop;
import com.example.gameservicedemo.game.tools.bean.Tools;
import com.example.gameservicedemo.game.tools.bean.ToolsType;
import com.example.gameservicedemo.game.shop.cache.ShopCache;
import com.example.gameservicedemo.manager.NotificationManager;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/18/10:00
 * @Description:
 */
@Service
@Slf4j
public class ShopService {
    @Autowired
    ShopCache shopCache;
    @Autowired
    NotificationManager notificationManager;

    /**
     * 展示
     * 应按照装备的属性展示这些可购买的装备 （id 名字）
     */
    public void showShop(ChannelHandlerContext context){
        Shop shopById = shopCache.getShopById(1);
        StringBuilder stringBuilder = new StringBuilder(MessageFormat.format("欢迎来到{0},这里的待售物品如下：\n", shopById.getShopName()));
        Map<Integer, Tools> goodsMap = shopById.getGoodsMap();
        StringBuilder stringBuilder1 = new StringBuilder(ToolsType.PHYSICS.getDescribe()+":\n");
        StringBuilder stringBuilder2 = new StringBuilder(ToolsType.MAGIC.getDescribe()+":\n");
        StringBuilder stringBuilder3 = new StringBuilder(ToolsType.DEFENSE.getDescribe()+":\n");
        StringBuilder stringBuilder4 = new StringBuilder(ToolsType.MEDICINE.getDescribe()+":\n");
        StringBuilder stringBuilder5 = new StringBuilder(ToolsType.PEST.getDescribe()+":\n");
        goodsMap.values().forEach(v->{
            String s = MessageFormat.format("商品id：{0}  商品名称：{1}\n", v.getId(), v.getName());
            switch (v.getType()){
                case 1:
                    stringBuilder1.append(s);
                    break;
                case 2:
                    stringBuilder2.append(s);
                    break;
                case 3:
                    stringBuilder3.append(s);
                    break;
                case 4:
                    stringBuilder4.append(s);
                    break;
                case 5:
                    stringBuilder5.append(s);
                    break;
            }
        });
        stringBuilder.append(stringBuilder1).append(stringBuilder2).append(stringBuilder3).append(stringBuilder4).append(stringBuilder5);
        stringBuilder.append("你可以使用\"see_tools_info\"来查看某一商品的详细信息！");
        notificationManager.notifyByCtx(context,stringBuilder.toString(), RequestCode.SUCCESS.getCode());
    }
}
