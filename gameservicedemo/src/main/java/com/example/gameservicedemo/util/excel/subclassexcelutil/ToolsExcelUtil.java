package com.example.gameservicedemo.util.excel.subclassexcelutil;

import com.example.gameservicedemo.game.tools.bean.Tools;
import com.example.gameservicedemo.util.excel.ReadExcelByEntity;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/11/15:47
 * @Description:
 */
public class ToolsExcelUtil extends ReadExcelByEntity<Tools> {
    /**
     * 构造工具类
     *
     * @param filepath
     */
    public ToolsExcelUtil(String filepath)  {
        super(filepath);
    }


}
