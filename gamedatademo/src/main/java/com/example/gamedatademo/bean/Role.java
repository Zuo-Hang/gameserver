package com.example.gamedatademo.bean;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/30/17:13
 * @Description:
 */
@Data
public class Role {
    /**
     * 角色id
     */
    private int roleId;
    /**
     * 实体角色的名字
     */
    private String roleName;
    /**
     * 角色类型
     */
    private int roleType;
    /**
     * 角色状态
     */
    private int state;
    /**
     * 角色描述
     */
    private String roleDescribe;



}
