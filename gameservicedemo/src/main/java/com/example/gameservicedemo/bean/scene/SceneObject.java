package com.example.gameservicedemo.bean.scene;

import com.example.gameservicedemo.util.excel.EntityName;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/11/15:44
 * @Description: 场景内的实体，所具有的共性。可以从Excel中配置
 */
@Data
public class SceneObject {
    @EntityName(column = "id")
    private Integer id;

    @EntityName(column = "名字")
    private String name;

    @EntityName(column = "生命值")
    private Long hp;

    @EntityName(column = "魔法值")
    private Long mp;

    @EntityName(column = "攻击力")
    private Long attack;

    @EntityName(column = "交谈文本")
    private String talk = "";

    @EntityName(column = "技能")
    private String skills;

    @EntityName(column = "状态")
    private Integer state;

    @EntityName(column = "角色类型")
    private Integer roleType;

    @EntityName(column = "刷新时间")
    private Long refreshTime;

    @EntityName(column = "掉落")
    private String drop;

    @EntityName(column = "任务")
    private String quests;

    @EntityName(column = "角色描述")
    private String describe;


}
