package com.example.gameservicedemo.game.mail.service;

import com.example.commondemo.base.RequestCode;
import com.example.gamedatademo.bean.Mail;
import com.example.gamedatademo.bean.Player;
import com.example.gamedatademo.mapper.MailMapper;
import com.example.gamedatademo.mapper.PlayerMapper;
import com.example.gameservicedemo.game.bag.bean.BagBeCache;
import com.example.gameservicedemo.game.bag.service.BagService;
import com.example.gameservicedemo.game.mail.bean.GameSystem;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.service.PlayerDataService;
import com.example.gameservicedemo.game.player.service.PlayerLoginService;
import com.example.gameservicedemo.game.tools.bean.Tools;
import com.example.gameservicedemo.manager.NotificationManager;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/01/18:29
 * @Description:
 */
@Slf4j
@Service
public class MailService {
    @Autowired
    MailMapper mailMapper;
    @Autowired
    PlayerMapper playerMapper;
    @Autowired
    PlayerLoginService playerLoginService;
    @Autowired
    PlayerDataService playerDataService;
    @Autowired
    GameSystem gameSystem;
    @Autowired
    BagService bagService;
    @Autowired
    NotificationManager notificationManager;

    /**
     * 发送邮件
     * @param sender
     * @param receiverId
     * @param subject
     * @param content
     * @param toolsUuid
     */
    public void sendMail(PlayerBeCache sender, Integer receiverId, String subject, String content, Long toolsUuid) {
        //检查收件人是否存在
        Player player = playerMapper.selectByPlayerId(receiverId);
        if(Objects.isNull(player)&&(!sender.equals(gameSystem))){
            notificationManager.notifyPlayer(sender,"收件人不存在，请检查输入的id", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        Mail mail = new Mail(subject, content, sender.getId(), receiverId, 0);

        if(sender!=gameSystem){
            BagBeCache bagBeCache = sender.getBagBeCache();
            if (Objects.nonNull(toolsUuid)&& toolsUuid != 0) {
                Tools tools = bagService.containsTools(bagBeCache, toolsUuid);
                if(Objects.isNull(tools)){
                    notificationManager.notifyPlayer(sender,"你的背包中不存在这个装备", RequestCode.BAD_REQUEST.getCode());
                    return;
                }
                bagService.removeFromBag(bagBeCache,toolsUuid);
                Gson gson = new Gson();
                String toolsJson = gson.toJson(tools);
                mail.setAttachment(toolsJson);
            }
            playerDataService.showPlayerBag(sender);
        }
        Integer insert = mailMapper.insert(mail);
        if(!sender.equals(gameSystem)){
            notificationManager.notifyPlayer(sender,"邮件已发出！",RequestCode.SUCCESS.getCode());
        }
        //判断玩家是否在线，在线则通知
        if(playerLoginService.playerIsOnLine(receiverId)){
            notificationManager.notifyPlayer(playerLoginService.getPlayerById(receiverId), MessageFormat.format("收到{0}的一封邮件!",
                    sender.getName()),RequestCode.WARNING.getCode());
        }
    }

    /**
     * 接收邮件(接受附件)
     * @param player
     * @param mailId
     */
    public void getMail(PlayerBeCache player, Integer mailId) {
        Mail mail = mailMapper.selectByMailId(mailId);
        if(Objects.isNull(mail)|| !mail.getReceiver().equals(player.getId())){
            notificationManager.notifyPlayer(player,"你并没有这封邮件！",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        String attachment = mail.getAttachment();
        if(Objects.isNull(attachment)){
            notificationManager.notifyPlayer(player,"邮件不含有附件！",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        if(mail.getHasRead().equals(1)){
            notificationManager.notifyPlayer(player,"这封邮件已经接收过了！",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        Gson gson = new Gson();
        Tools tools = gson.fromJson(attachment, Tools.class);
        boolean b = bagService.putInBag(player, tools);
        if(!b){
            notificationManager.notifyPlayer(player,"邮件接收失败！背包容量不足！",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        mail.setHasRead(1);
        mailMapper.updateByMailId(mail);
        notificationManager.notifyPlayer(player,"邮件接收成功！",RequestCode.SUCCESS.getCode());
    }

    /**
     * 查看自己的邮件列表
     * @param player
     */
    public void mailList(PlayerBeCache player) {
        List<Mail> mails = mailMapper.selectByReceiverId(player.getId());
        StringBuilder stringBuilder = new StringBuilder("你的邮件列表如下：\n");
        if(mails.size()==0){
            stringBuilder.append("你还没有邮件！");
        }else{
            mails.forEach(mail -> {
                stringBuilder.append(MessageFormat.format("邮件id：{0} 发送人：{1}\n主题：{2}\n内容：{3}\n",
                        mail.getId(),
                        mail.getSender(),
                        mail.getSubject(),
                        mail.getContent()));
                String attachment = mail.getAttachment();
                if(Objects.nonNull(attachment)){
                    Gson gson = new Gson();
                    Tools tools = gson.fromJson(attachment, Tools.class);
                    stringBuilder.append(MessageFormat.format("附件：{0}\n",tools.getName()));
                }
                stringBuilder.append("\n");
            });
        }
        notificationManager.notifyPlayer(player,stringBuilder.toString(),RequestCode.SUCCESS.getCode());
    }

    /**
     * 删除邮件
     * @param player
     * @param mailId
     */
    public void deleteMail(PlayerBeCache player, Integer mailId) {
        Mail mail = mailMapper.selectByMailId(mailId);
        if(Objects.isNull(mail)|| !mail.getReceiver().equals(player.getId())){
            notificationManager.notifyPlayer(player,"你并没有这封邮件！",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        if(Objects.nonNull(mail.getAttachment())&&mail.getHasRead().equals(0)){
            notificationManager.notifyPlayer(player,"这封邮件处于未接收状态，不能进行删除！",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        notificationManager.notifyPlayer(player,MessageFormat.format("已经删除了邮件 id:{0}",
                mailId),RequestCode.SUCCESS.getCode());
        mailMapper.deleteByMailId(mailId);
    }
}
