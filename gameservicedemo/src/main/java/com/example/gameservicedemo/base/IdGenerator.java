package com.example.gameservicedemo.base;

import com.example.gameservicedemo.util.SnowFlake;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/31/12:13
 * @Description: //id生成器
 */
public class IdGenerator {
    public static Long getAnId(){
        // 使用推特的雪花算法
        SnowFlake snowFlake = new SnowFlake(1, 1);
        return snowFlake.nextId();
    }
}
