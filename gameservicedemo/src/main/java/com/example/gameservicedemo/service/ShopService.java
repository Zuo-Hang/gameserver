package com.example.gameservicedemo.service;

import com.example.gameservicedemo.bean.shop.Shop;
import com.example.gameservicedemo.bean.shop.Tools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    ToolsService toolsService;

    /**
     * 初始化商店的货架
     * @param shop
     */
    public void initShopGoodsMap(Shop shop){
        String goods = shop.getGoods();
        String[] split = goods.split(",");
        for (String s : split) {
            //获取到商品
            Tools toolsById = toolsService.getToolsById(Integer.valueOf(s));
            //放入货架
            shop.getGoodsMap().put(toolsById.getId(),toolsById);
        }
        log.info("商店：{} 的货架初始化完毕",shop.getShopName());
    }


}
