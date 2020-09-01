package com.example.gameservicedemo.base;

import com.example.gameservicedemo.util.IdWorker;
import com.example.gameservicedemo.util.SnowFlake;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/31/12:13
 * @Description: //id生成器
 */
public class IdGenerator {
    static IdWorker idWorker = new IdWorker();
    public static Long getAnId(){
        // 使用推特的雪花算法

//        SnowFlake snowFlake = new SnowFlake(1, 1);
        return idWorker.nextId();
    }
}
