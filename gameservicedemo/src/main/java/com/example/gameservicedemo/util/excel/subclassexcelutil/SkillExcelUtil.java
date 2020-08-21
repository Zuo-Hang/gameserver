package com.example.gameservicedemo.util.excel.subclassexcelutil;

import com.example.gameservicedemo.bean.skill.Skill;
import com.example.gameservicedemo.util.excel.ReadExcelByEntity;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/11/18:00
 * @Description:
 */
public class SkillExcelUtil extends ReadExcelByEntity<Skill> {
    /**
     * 构造工具类
     *
     * @param filepath
     */
    public SkillExcelUtil(String filepath) {
        super(filepath);
    }
}
