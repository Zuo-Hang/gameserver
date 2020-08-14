package com.example.gameclientdemo.client;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/13/21:35
 * @Description:
 */
@Data
public class BaseData {
    /**
     * 对已经登录的用户id进行存储，若断线后可以利用userId实现重连，继续游戏
     * 再用户未登录，与登出后会呈现null值
     */
    Integer userId=null;
}
