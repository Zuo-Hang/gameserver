package com.example.gameservicedemo.bean.shop;

import com.example.gameservicedemo.util.excel.EntityName;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/11/18:02
 * @Description: 商店实体类
 */
@Data
public class Shop {

    @EntityName(column = "ID")
    private Integer shopId;

    @EntityName(column = "商店名字")
    private String shopName;

    @EntityName(column = "货物")
    private String goods;


    private Map<Integer, Tools> goodsMap = new HashMap<>();

}
