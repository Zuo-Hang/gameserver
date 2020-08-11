package com.example.gameservicedemo.bean;

import com.example.gameservicedemo.util.excel.EntityName;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/11/18:05
 * @Description: 角色类型 ，用户创建化身时可以进行选择。不同的类型的化身所具备的体能天赋都有所差异。
 */
@Data
public class RoleType {
    @EntityName(column = "id")
    private Integer id;

    @EntityName(column = "职业名称")
    private String name;

    @EntityName(column = "物理攻击")
    private Integer ad;

    @EntityName(column = "法术攻击")
    private Integer ap;

    @EntityName(column = "防御力")
    private Integer def;

    @EntityName(column = "技能")
    private String skills = "";
}
