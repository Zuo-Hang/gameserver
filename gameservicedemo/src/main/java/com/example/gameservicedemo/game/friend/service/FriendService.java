package com.example.gameservicedemo.game.friend.service;

import com.example.commondemo.base.RequestCode;
import com.example.gameservicedemo.base.IdGenerator;
import com.example.gameservicedemo.event.EventBus;
import com.example.gameservicedemo.event.model.FriendEvent;
import com.example.gameservicedemo.game.friend.bean.FriendAddRequest;
import com.example.gameservicedemo.game.friend.cache.FriendCache;
import com.example.gameservicedemo.game.mail.bean.GameSystem;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.service.PlayerLoginService;
import com.example.gameservicedemo.game.player.service.RoleTypeService;
import com.example.gameservicedemo.manager.NotificationManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.Even;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/21/20:31
 * @Description: 朋友服务
 */
@Service
@Slf4j
public class FriendService {
    @Autowired
    PlayerLoginService playerLoginService;
    @Autowired
    NotificationManager notificationManager;
    @Autowired
    RoleTypeService roleTypeService;
    @Autowired
    GameSystem gameSystem;
    @Autowired
    FriendCache friendCache;

    /**
     * 删除好友
     *
     * @param player
     * @param friendId
     */
    public void friendDelete(PlayerBeCache player, Integer friendId) {
        if(!player.getFriendList().contains(friendId)){
            notificationManager.notifyPlayer(player,"该用户还不是你的朋友！",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        player.removeFriend(friendId);
        PlayerBeCache playerById = playerLoginService.getPlayerById(friendId);
        notificationManager.notifyPlayer(player,"删除该好友成功！",RequestCode.SUCCESS.getCode());
        if(Objects.isNull(playerById)){
            return;
        }
        playerById.removeFriend(player.getId());
        gameSystem.noticeSomeOne(friendId,"好友相关",MessageFormat.format("{0}与你解除了好友关系",player.getName()),null);
    }

    /**
     * 全服查找用户
     *
     * @param player
     * @param playerId
     */
    public void playerSearch(PlayerBeCache player, Integer playerId) {
        PlayerBeCache aim = playerLoginService.getPlayerById(playerId);
        if (Objects.isNull(aim)) {
            notificationManager.notifyPlayer(player, "该用户不存在，请查看输入id", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        String string = MessageFormat.format("id:{0},name:{1},等级：{2},角色类型:{3}",
                aim.getId(),
                aim.getName(),
                aim.getExp(),
                roleTypeService.getRoleTypeById(aim.getRoleClass()).getName());
        notificationManager.notifyPlayer(player, string, RequestCode.SUCCESS.getCode());
    }

    /**
     * 同意某个好友请求
     *
     * @param player
     * @param requestId
     */
    public void friendAgree(PlayerBeCache player, Long requestId) {
        FriendAddRequest request = friendCache.getRequestById(requestId);
        if (Objects.isNull(request)) {
            notificationManager.notifyPlayer(player, "该请求不存在，请检查请求id或请求是否过期", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        if (!request.getRecipientId().equals(player.getId())) {
            notificationManager.notifyPlayer(player, "你并不是这个请求的接收者，不能进行同意操作", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        player.addFriend(request.getProposerId());
        PlayerBeCache proposer = playerLoginService.getPlayerById(request.getProposerId());
        proposer.addFriend(player.getId());
        notificationManager.notifyPlayer(player, "好友添加成功！", RequestCode.BAD_REQUEST.getCode());
        gameSystem.noticeSomeOne(proposer.getId(),
                "好友相关",
                MessageFormat.format("{0}已经是你的好友了,快去打个招呼吧", player.getName()),
                null);
        EventBus.publish(new FriendEvent(player));
        EventBus.publish(new FriendEvent(proposer));
    }

    /**
     * 发出好友请求
     *
     * @param player
     * @param aimPlayerId
     */
    public void friendAdd(PlayerBeCache player, Integer aimPlayerId) {
        PlayerBeCache aim = playerLoginService.getPlayerById(aimPlayerId);
        if (Objects.isNull(aim)) {
            notificationManager.notifyPlayer(player, "该用户不存在，请检查输入的id", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        FriendAddRequest friendAddRequest = new FriendAddRequest(IdGenerator.getAnId(), player.getId(), aimPlayerId);
        friendCache.addRequest(friendAddRequest);
        String string = MessageFormat.format("id为{0}的{1}在{3}请求添加你为好友，请求id为{2},同意请在24小时内处理，过期请求自动失效",
                player.getId(), player.getName(), friendAddRequest.getId(), new Date());
        gameSystem.noticeSomeOne(aimPlayerId, "好友请求", string, null);
        notificationManager.notifyPlayer(player, "你的好友请求已发出，若24小时内未收到同意请求请视为拒绝", RequestCode.SUCCESS.getCode());
    }

    /**
     * 查看好友列表
     *
     * @param player
     */
    public void friendList(PlayerBeCache player) {
        List<Integer> friendList = player.getFriendList();
        if (friendList.isEmpty()) {
            notificationManager.notifyPlayer(player, "你还没有好友呢！", RequestCode.SUCCESS.getCode());
            return;
        }
        StringBuilder string = new StringBuilder("好友列表如下：\n");
        friendList.forEach(friendId -> {
            PlayerBeCache friend = playerLoginService.getPlayerById(friendId);
            string.append(MessageFormat.format("id:{0} name:{1} 是否在线：{2}\n",
                    friend.getId(),
                    friend.getName(),
                    playerLoginService.playerIsOnLine(friend.getId())?"是":"否"));
        });
        notificationManager.notifyPlayer(player, string, RequestCode.SUCCESS.getCode());
    }
}
