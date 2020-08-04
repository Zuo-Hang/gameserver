package com.example.gamedatademo.mapper;

import com.example.gamedatademo.bean.Role;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/04/21:53
 * @Description: 角色相关的 mybatis mapper接口
 */
public interface RoleMapper {
    /**
     * 添加角色
     * @param role
     * @return
     */
    Integer insert(Role role);

    /**
     * 按照角色id查找
     * @param roleId
     * @return
     */
    Role selectByRoleId(Integer roleId);

    /**
     * 按照角色id删除
     * @param roleId
     * @return
     */
    Integer deleteByRoleId(Integer roleId);

    /**
     * 按照角色id更新
     * @param roleId
     * @return
     */
    Integer updateByRoleId(Integer roleId);
}
