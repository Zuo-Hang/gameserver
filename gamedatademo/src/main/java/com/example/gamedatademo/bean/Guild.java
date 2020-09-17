package com.example.gamedatademo.bean;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/09/15:09
 * @Description:
 */
@Data
public class Guild {
    /**
     * id
     */
    private Long id;
    /**
     * 名称
     */
    private String name;
    /**     * 等级
     */
    private Integer level;
    /**
     * 成员
     */
    private String member;
    /**
     * 仓库
     */
    private String warehouse;
    /**
     * 仓库大小
     */
    private Integer warehouseSize;

    private Integer goldNum;
    /**
     * 加入请求
     */
    private String joinRequest;
}
