package com.example.gameservicedemo.util.excel.subclassexcelutil;

import com.example.gameservicedemo.game.shop.bean.Shop;
import com.example.gameservicedemo.util.excel.ReadExcelByEntity;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/11/18:04
 * @Description:
 */
public class ShopExcelUtil extends ReadExcelByEntity<Shop> {
    /**
     * 构造工具类
     *
     * @param filepath
     */
    public ShopExcelUtil(String filepath) {
        super(filepath);
    }
}
