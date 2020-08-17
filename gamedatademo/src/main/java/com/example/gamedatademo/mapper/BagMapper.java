package com.example.gamedatademo.mapper;

import com.example.gamedatademo.bean.Bag;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/17/20:00
 * @Description:
 */
public interface BagMapper {
    Bag selectByBagId(Integer bagId);

    Integer insert(Bag bag);

    Integer updateByBagId(Bag bag);
}
