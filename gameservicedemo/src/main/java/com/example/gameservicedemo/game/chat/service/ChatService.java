package com.example.gameservicedemo.game.chat.service;

import com.example.commondemo.base.RequestCode;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.service.PlayerLoginService;
import com.example.gameservicedemo.manager.NotificationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/01/17:14
 * @Description: 聊天服务
 */
@Service
@Slf4j
public class ChatService {
    @Autowired
    PlayerLoginService playerLoginService;
    @Autowired
    NotificationManager notificationManager;

    /**
     * 全服广播
     *
     * @param player
     * @param string
     */
    public void publicChat(PlayerBeCache player, String string) {
        playerLoginService.getAllPlayerLoaded().values().forEach(v -> {
            notificationManager.notifyPlayer(v, MessageFormat.format("收到 {0} 的全局广播：{1}",
                    player.getName(),
                    string), RequestCode.WARNING.getCode());
        });
        notificationManager.notifyPlayer(player, "已经进行了全局广播", RequestCode.SUCCESS.getCode());
    }

    /**
     * 私信
     *
     * @param player
     * @param targetPlayerId
     * @param string
     */
    public void whisper(PlayerBeCache player, Integer targetPlayerId, String string) {
        PlayerBeCache targetPlayer = playerLoginService.getPlayerById(targetPlayerId);
        if (Objects.isNull(targetPlayer)) {
            notificationManager.notifyPlayer(player, "未发现该玩家，输入的id错误或该玩家已下线。", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        notificationManager.notifyPlayer(targetPlayer, MessageFormat.format("{0} 私信你说：{1}",
                player.getName(),
                string), RequestCode.WARNING.getCode());
        notificationManager.notifyPlayer(player, MessageFormat.format("已经私信了 {0} ", targetPlayer.getName()), RequestCode.SUCCESS.getCode());
    }
}
