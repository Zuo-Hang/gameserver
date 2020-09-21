package com.example.gameservicedemo.game.friend.bean;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/21/21:03
 * @Description:
 */
@Data
public class FriendAddRequest {
    Long id;
    /**
     * 提出者id
     */
    Integer proposerId;
    /**
     * 接受者id
     */
    Integer recipientId;

    public FriendAddRequest() {
    }

    public FriendAddRequest(Long id, Integer proposerId, Integer recipientId) {
        this.id = id;
        this.proposerId = proposerId;
        this.recipientId = recipientId;
    }
}
