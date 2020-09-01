package com.example.gameservicedemo.game.mail.controller;

import com.example.commondemo.base.Command;
import com.example.commondemo.message.Message;
import com.example.gameservicedemo.base.controller.ControllerManager;
import com.example.gameservicedemo.game.mail.service.MailService;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.service.PlayerLoginService;
import com.example.gameservicedemo.util.CheckParametersUtil;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;


/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/01/18:28
 * @Description:
 */
@Component
public class MailController {
    @Autowired
    PlayerLoginService playerLoginService;
    @Autowired
    MailService mailService;

    {
        ControllerManager.add(Command.SEND_MAIL.getRequestCode(),this::sendMail);
        ControllerManager.add(Command.MAIL_LIST.getRequestCode(),this::mailList);
        ControllerManager.add(Command.GET_MAIL.getRequestCode(),this::getMail);
        ControllerManager.add(Command.DELETE_MAIL.getRequestCode(),this::deleteMail);
    }

    private void deleteMail(ChannelHandlerContext context, Message message) {
        String[] strings = CheckParametersUtil.checkParameter(context, message, 2);
        PlayerBeCache load = playerLoginService.isLoad(context);
        if(Objects.isNull(strings)||Objects.isNull(load)){
            return;
        }
        mailService.deleteMail(load,Integer.valueOf(strings[1]));
    }

    /**
     *
     * @param context
     * @param message
     */
    private void getMail(ChannelHandlerContext context, Message message) {
        String[] strings = CheckParametersUtil.checkParameter(context, message, 2);
        PlayerBeCache load = playerLoginService.isLoad(context);
        if(Objects.isNull(strings)||Objects.isNull(load)){
            return;
        }
        mailService.getMail(load,Integer.valueOf(strings[1]));
    }

    /**
     * 查看邮件列表
     * @param context
     * @param message
     */
    private void mailList(ChannelHandlerContext context, Message message) {
        String[] strings = CheckParametersUtil.checkParameter(context, message, 1);
        PlayerBeCache load = playerLoginService.isLoad(context);
        if(Objects.isNull(strings)||Objects.isNull(load)){
            return;
        }
        mailService.mailList(load);
    }

    private void sendMail(ChannelHandlerContext context, Message message) {
        //指令 收件人 主题 内容 附件（toolsUUID）
        String[] strings = CheckParametersUtil.checkParameter(context, message, 5);
        PlayerBeCache load = playerLoginService.isLoad(context);
        if(Objects.isNull(strings)||Objects.isNull(load)){
            return;
        }
        mailService.sendMail(load,Integer.valueOf(strings[1]),strings[2],strings[3],Long.valueOf(strings[4]));
    }
}
