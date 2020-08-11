package com.example.gameservicedemo.util.excel.subclassexcelutil;

import com.example.gameservicedemo.bean.scene.SceneObject;
import com.example.gameservicedemo.util.excel.ReadExcelByEntity;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/11/15:47
 * @Description:
 */
public class SceneObjectExcelUtil extends ReadExcelByEntity<SceneObject> {

    /**
     * 构造工具类
     *
     * @param filepath
     */
    public SceneObjectExcelUtil(String filepath) {
        super(filepath);
    }
}
