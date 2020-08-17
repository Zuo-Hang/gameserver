package com.example.gamedatademo.bean;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/17/19:57
 * @Description:
 */
@Data
public class Bag {
    private Integer id;
    private Integer size;
    private String name;
    /**
     * 存放的物品
     */
    private String items;
}
