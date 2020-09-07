package com.example.gameservicedemo.game.hurt;

import com.example.gameservicedemo.background.WriteBackDB;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.tools.bean.ToolsProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/07/11:09
 * @Description: 保证更改数据时的线程安全性
 */
@Component
public class ChangePlayerInformationImp extends ChangeCreatureInformation{
    @Autowired
    WriteBackDB writeBackDB;

    public void changePlayerMagic(PlayerBeCache player, Integer magic) {
        synchronized (player){
            if(player.getMp() + magic>0&&player.getMp() + magic<=player.getMaxMp()){
                player.setMp(player.getMp() + magic);
            }else if(player.getMp() + magic<=0){
                player.setMp(0);
            }else{
                player.setMp(player.getMaxMp());
            }
        }
    }

    public void changePlayerMoney(PlayerBeCache player, Integer money) {
        synchronized (player){
            //更新钱数
            player.setMoney(player.getMoney() + money);
            //提交更改数据库任务
            player.getUpdate().add(5);
            writeBackDB.delayWriteBackPlayer(player);
        }
    }


    public void changePlayerInfluence(PlayerBeCache player, ToolsProperty toolsProperty) {
        synchronized (player){
            player.getToolsInfluence().put(toolsProperty.getId(),toolsProperty);
        }
    }
}
