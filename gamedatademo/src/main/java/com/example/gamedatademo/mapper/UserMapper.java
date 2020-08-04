package com.example.gamedatademo.mapper;

import com.example.gamedatademo.bean.User;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/30/17:24
 * @Description: 用户相关的 mybatis mapper接口
 */
public interface UserMapper {
    /**
     * 插入新的用户
     * @param user
     * @return
     */
    Integer insert(User user);

    /**
     * 按照id查找用户
     * @param userId
     * @return
     */
    User selectByUserId(Integer userId);

    /**
     * 按照用户Id删除用户
     * @param userId
     * @return
     */
    Integer deleteByUserId(Integer userId);

    /**
     * 按照用户id更新
     * @param userId
     * @return
     */
    Integer updateByUserId(Integer userId);
}
