package com.example.gameservicedemo.bean.shop;

import com.example.gameservicedemo.util.excel.EntityName;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/11/17:30
 * @Description: 道具  可以在商店中出售，并且可以被放在背包当中
 */
@Data
public class Tools {

    @EntityName(column="物品编号")
    private Integer  id;

    @EntityName(column="物品名称")
    private String  name;

    @EntityName(column="触发效果")
    private Integer  buffer;

    @EntityName(column="装备等级")
    private Integer level;

    @EntityName(column = "种类")
    private Integer kind;

    @EntityName(column = "属性")
    private String roleProperties = "";

    @EntityName(column = "部位")
    private String part;

    @EntityName(column = "价格")
    private Integer price ;

    @EntityName(column = "描述")
    private String describe;
}
