package com.example.gameservicedemo.util.excel.subclassexcelutil;

import com.example.gameservicedemo.game.player.bean.RoleType;
import com.example.gameservicedemo.util.excel.ReadExcelByEntity;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/13/10:03
 * @Description:
 */
public class RoleTypeExcelUtil extends ReadExcelByEntity <RoleType>{
    /**
     * 构造工具类
     *
     * @param filepath
     */
    public RoleTypeExcelUtil(String filepath) {
        super(filepath);
    }
}
