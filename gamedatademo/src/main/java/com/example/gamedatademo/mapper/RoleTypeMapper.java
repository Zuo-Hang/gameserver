package com.example.gamedatademo.mapper;

import com.example.gamedatademo.bean.RoleType;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/05/16:30
 * @Description: 角色类型相关的 mybatis mapper接口
 */
public interface RoleTypeMapper {
    RoleType selectByRoleTypeId(Integer roleTypeId);
}
