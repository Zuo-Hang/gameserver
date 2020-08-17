package com.example.gameservicedemo.util.excel.subclassexcelutil;

import com.example.gameservicedemo.bean.Buffer;
import com.example.gameservicedemo.util.excel.ReadExcelByEntity;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/17/16:24
 * @Description:
 */
public class BufferExcelUtill extends ReadExcelByEntity<Buffer> {

    /**
     * 构造工具类
     *
     * @param filepath
     */
    public BufferExcelUtill(String filepath) {
        super(filepath);
    }
}
