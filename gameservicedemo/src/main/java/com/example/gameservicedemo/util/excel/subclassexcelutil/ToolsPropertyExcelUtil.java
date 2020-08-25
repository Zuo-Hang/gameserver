package com.example.gameservicedemo.util.excel.subclassexcelutil;

import com.example.gameservicedemo.game.tools.bean.ToolsProperty;
import com.example.gameservicedemo.util.excel.ReadExcelByEntity;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/17/21:41
 * @Description:
 */
public class ToolsPropertyExcelUtil extends ReadExcelByEntity<ToolsProperty> {
    /**
     * 构造工具类
     *
     * @param filepath
     */
    public ToolsPropertyExcelUtil(String filepath) {
        super(filepath);
    }
}
