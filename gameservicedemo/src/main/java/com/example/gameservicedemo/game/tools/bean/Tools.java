package com.example.gameservicedemo.game.tools.bean;

import com.example.gameservicedemo.base.bean.BeUse;
import com.example.gameservicedemo.util.excel.EntityName;
import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/11/17:30
 * @Description: 道具  可以在商店中出售，并且可以被放在背包当中
 */
@Data
public class Tools extends BeUse {

    @EntityName(column="物品编号")
    private Integer  id;

    @EntityName(column="物品名称")
    private String  name;

    @EntityName(column="唯一被动技能")
    private Integer  passiveSkills;

    @EntityName(column="耐久度")
    private Integer durability;

    @EntityName(column = "可叠加数量")
    private Integer repeat;

    private Integer level;

    /**
     * 加成的json格式
     */
    @EntityName(column = "属性")
    private String toolsProperties = "";

    @EntityName(column = "购入价格")
    private Integer priceIn ;

    @EntityName(column = "卖出价格")
    private Integer priceOut ;

    @EntityName(column = "描述")
    private String describe;
    @EntityName(column = "叠加类型")
    private Integer repeatType;

    /** 属于哪种物品（装备由此属性和repeatKind共同决定） 装备、药品、宠物*/
    @EntityName(column = "类型")
    private Integer type;
    /** 物品在背包中被叠加的数量 */
    //private Integer count;
    /** 物品属性,此装备可以带来的加成、效果 */
    private List<ToolsProperty> toolsPropertie=null;
}
