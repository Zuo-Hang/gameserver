package com.example.gameservicedemo.game.shop.cache;

import com.example.gameservicedemo.game.shop.bean.Shop;
import com.example.gameservicedemo.game.tools.service.ToolsService;
import com.example.gameservicedemo.util.excel.subclassexcelutil.ShopExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/18/9:56
 * @Description:
 */
@Component
@Slf4j
public class ShopCache {
    @Autowired
    ToolsService toolsService;

    private HashMap<Integer, Shop> shopCache=new HashMap<Integer, Shop>();
    @PostConstruct
    public void init(){
        ShopExcelUtil shopExcelUtil = new ShopExcelUtil("C:\\java_project\\mmodemo\\gameservicedemo\\src\\main\\resources\\game_configuration_excel\\shop.xlsx");
        shopExcelUtil.getMap().values().forEach(v->{
            toolsService.initShopGoodsMap(v);
            shopCache.put(v.getShopId(),v);
        });
        log.info("商城信息缓存加载完毕！");
    }

    /**
     * 按照id获取某个商城
     * @param shopId
     * @return
     */
    public Shop getShopById(Integer shopId){
        return shopCache.get(shopId);
    }
}
