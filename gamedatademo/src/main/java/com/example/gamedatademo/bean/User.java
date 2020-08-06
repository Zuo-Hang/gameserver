package com.example.gamedatademo.bean;

import lombok.Data;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/30/17:00
 * @Description:用户实体类
 */
@Data
public class User {
    /**
     * 用户Id
     */
    private int userId;
    /**
     * 用户昵称
     */
    private String nickName;
    /**
     * 用户密码
     */
    private String password;
    /**
     * 用户手机号
     */
    private String phoneNumber;
    /**
     * 账号是否被禁用
     */
    private int disable;
    /**
     * 账号创建时间
     */
    private Date creatTime;
    /**
     * 最后一次登录时间
     */
    private Date lastLoginTime;
}
