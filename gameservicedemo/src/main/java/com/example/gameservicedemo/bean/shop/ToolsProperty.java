package com.example.gameservicedemo.bean.shop;

import com.example.gameservicedemo.util.excel.EntityName;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/17/21:33
 * @Description: 装备的属性实体类
 */
@Data
public class ToolsProperty {
    @EntityName(column = "ID")
    private Integer id;

    @EntityName(column = "属性名")
    private String name;
    /**
     * 此值在使用的时候会覆盖标准属性值
     */
    @EntityName(column = "标准属性值")
    private Integer value;

    /**
     * 是否影响生命
     */
    @EntityName(column = "种类")
    private String type;

    @EntityName(column = "属性描述")
    private String describe;
}
