package com.example.gameservicedemo.base;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/04/12:12
 * @Description:对缓存的抽象
 */
public interface BaseCache<K,V> {
    /** 获取缓存数据 */
    V get(K id);
    /** 添加数据 */
    void put(K id, V value);
}
