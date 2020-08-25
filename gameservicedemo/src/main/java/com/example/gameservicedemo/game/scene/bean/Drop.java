package com.example.gameservicedemo.game.scene.bean;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/25/17:39
 * @Description: 击杀场景对象后的掉落物品
 */
@Data
public class Drop {
    // 掉落概率
    Integer chance;
    // 掉落物品
    Integer toolsId;
}
