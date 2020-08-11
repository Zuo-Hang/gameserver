package com.example.gameservicedemo.util.excel.subclassexcelutil;

import com.example.gameservicedemo.bean.scene.Scene;
import com.example.gameservicedemo.util.excel.ReadExcelByEntity;

/**
 * <pre> </pre>
 */
/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/10/15:39
 * @Description: 子类，传入具体的泛型
 */
public class SceneExcelUtil extends ReadExcelByEntity<Scene> {
    /**
     * 构造工具类
     *
     * @param filepath
     */
    public SceneExcelUtil(String filepath) {
        super(filepath);
    }
}
