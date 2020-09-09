package com.example.gameservicedemo.game.guild.bean;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/09/15:03
 * @Description:
 */
public enum RoleType {
    President(1,"会长", new HashSet<Integer>()),

    Vice_President(2,"副会长", new HashSet<Integer>()),

    Elite(3,"精英", new HashSet<Integer>()),

    Ordinary_Member(4,"普通会员", new HashSet<Integer>())

    ;
    private Integer code;
    private String describe;
    /**
     * set中包含了该角色可以进行的具体操作命令（Command）的requestCode
     */
    private Set<Integer> powerSet;

    RoleType(Integer code, String describe, Set<Integer> powerSet) {
        this.code = code;
        this.describe = describe;
        this.powerSet = powerSet;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescribe() {
        return describe;
    }

    public Set<Integer> getPowerSet() {
        return powerSet;
    }
}
