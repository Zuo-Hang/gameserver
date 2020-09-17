package com.example.gamedatademo.bean;

import lombok.Data;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

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
     * 存放的物品,最终以json的格式落库
     */
    private String tools;
    /**
     * 格子
     */
    private String items;

    private Set<String> update=new ConcurrentSkipListSet<>();

//    public synchronized void setSize(Integer size) {
//        this.size = size;
//        update.add("size");
//    }

//    public synchronized void setName(String name) {
//        this.name = name;
//        update.add("name");
//    }
}
