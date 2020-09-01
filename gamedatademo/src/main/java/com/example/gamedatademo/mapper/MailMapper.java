package com.example.gamedatademo.mapper;

import com.example.gamedatademo.bean.Mail;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/01/18:10
 * @Description:
 */
public interface MailMapper {
    /**
     * 按照邮件id查询
     * @param mailId
     * @return
     */
    Mail selectByMailId(Integer mailId);

    /**
     * 按照发件人id查询
     * @param senderId
     * @return
     */
    List<Mail> selectBySenderId(Integer senderId);

    /**
     * 按照收件人id查询
     * @param receiverId
     * @return
     */
    List<Mail> selectByReceiverId(Integer receiverId);

    /**
     * 插入
     * @param mail
     * @return
     */
    Integer insert(Mail mail);

    /**
     * 按邮件id更新
     * @param mail
     * @return
     */
    Integer updateByMailId(Mail mail);

    /**
     * 按id删除
     * @param mailId
     * @return
     */
    Integer deleteByMailId(Integer mailId);
}
