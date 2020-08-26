package com.example.gameservicedemo.game.copy.bean;

import com.example.gameservicedemo.game.scene.bean.Scene;
import com.example.gameservicedemo.game.team.bean.Team;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/26/11:14
 * @Description: 临时场景
 */
public class TemporaryScene extends Scene {
    /**只有一个队伍可以进入*/
    Team team=null;
}
