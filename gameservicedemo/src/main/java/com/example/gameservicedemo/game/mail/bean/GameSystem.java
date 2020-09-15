package com.example.gameservicedemo.game.mail.bean;

import com.example.gameservicedemo.game.chat.service.ChatService;
import com.example.gameservicedemo.game.mail.service.MailService;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.service.PlayerLoginService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/15/11:40
 * @Description: 系统(默认发邮件的对象)
 */
@Data
@Component
public class GameSystem extends PlayerBeCache {

    @Autowired
    PlayerLoginService playerLoginService;
    @Autowired
    ChatService chatService;
    @Autowired
    MailService mailService;

    @Override
    public String getName(){
        return "系统";
    }

    /**
     * 通知某个玩家（该玩家在线使用私信模式，不在线则通过邮件模式）
     * @param receiverId 玩家id
     * @param subject 主题
     * @param content 内容
     * @param toolsUuid 附加物品id
     */
    public void noticeSomeOne(Integer receiverId, String subject, String content, Long toolsUuid){
        if(playerLoginService.playerIsOnLine(receiverId)){
            chatService.whisper(this,receiverId,content);
        }else{
            mailService.sendMail(this,receiverId,subject,content,null);
        }
    }
}
