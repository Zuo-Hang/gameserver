package com.example.gamedatademo.bean;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/01/18:03
 * @Description:
 */
@Data
public class Mail {
    private Integer id;
    /**
     * 主题
     */
    private String subject;
    /**
     * 内容
     */
    private String content;

    private Integer sender;

    private Integer receiver;
    /**
     * 0未读，1已读
     */
    private Integer hasRead;

    /**
     * 附件
     */
    private String attachment;

    public Mail( String subject, String content, Integer sender, Integer receiver, Integer hasRead) {
        this.subject = subject;
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.hasRead = hasRead;
    }

    public Mail() {
    }
}
