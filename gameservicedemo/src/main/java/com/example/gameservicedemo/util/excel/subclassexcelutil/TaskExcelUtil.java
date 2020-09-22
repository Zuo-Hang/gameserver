package com.example.gameservicedemo.util.excel.subclassexcelutil;

import com.example.gameservicedemo.game.task.bean.Task;
import com.example.gameservicedemo.util.excel.ReadExcelByEntity;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/21/12:21
 * @Description:
 */
public class TaskExcelUtil extends ReadExcelByEntity <Task>{
    /**
     * 构造工具类
     *
     * @param filepath
     */
    public TaskExcelUtil(String filepath) {
        super(filepath);
    }
}
