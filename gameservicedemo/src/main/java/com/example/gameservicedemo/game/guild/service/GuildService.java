package com.example.gameservicedemo.game.guild.service;

import com.example.commondemo.base.Command;
import com.example.commondemo.base.RequestCode;
import com.example.commondemo.message.Message;
import com.example.gamedatademo.bean.Player;
import com.example.gameservicedemo.background.WriteBackDB;
import com.example.gameservicedemo.base.IdGenerator;
import com.example.gameservicedemo.event.EventBus;
import com.example.gameservicedemo.event.model.GuildEvent;
import com.example.gameservicedemo.game.bag.bean.BagBeCache;
import com.example.gameservicedemo.game.bag.service.BagService;
import com.example.gameservicedemo.game.guild.bean.GuildBeCache;
import com.example.gameservicedemo.game.guild.bean.PlayerJoinRequest;
import com.example.gameservicedemo.game.guild.bean.RoleType;
import com.example.gameservicedemo.game.guild.cache.GuildCache;
import com.example.gameservicedemo.game.mail.bean.GameSystem;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.service.PlayerDataService;
import com.example.gameservicedemo.game.player.service.PlayerLoginService;
import com.example.gameservicedemo.game.tools.bean.Tools;
import com.example.gameservicedemo.manager.NotificationManager;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/09/17:07
 * @Description: 现在还是线程不安全的，需要改进
 */
@Slf4j
@Service
public class GuildService {
    @Autowired
    NotificationManager notificationManager;
    @Autowired
    GuildCache guildCache;
    @Autowired
    WriteBackDB writeBackDB;
    @Autowired
    PlayerDataService playerDataService;
    @Autowired
    GameSystem gameSystem;
    @Autowired
    PlayerLoginService playerLoginService;
    @Autowired
    BagService bagService;

    /**
     * 创建一个公会
     *
     * @param player
     */
    public void guildCreat(PlayerBeCache player, String name) {
        if (Objects.nonNull(player.getGuildId()) && player.getGuildId() != 0) {
            notificationManager.notifyPlayer(player, "你已经在公会中了，无法创建新的公会！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        GuildBeCache guildBeCache = new GuildBeCache(IdGenerator.getAnId(), name, 1, 30);
        guildBeCache.setWriteBackDB(writeBackDB);
        //这里应该只保存id
        guildBeCache.getMemberIdList().add(player.getId());
        player.setGuildId(guildBeCache.getId());
        player.setGuildRoleType(RoleType.President.getCode());
        guildCache.putInCache(guildBeCache);
        writeBackDB.insertGuild(guildBeCache);
        notificationManager.notifyPlayer(player, "公会已经创建完毕！", RequestCode.SUCCESS.getCode());
        //加入公会事件
        EventBus.publish(new GuildEvent(player,guildBeCache));
    }

    /**
     * 加入一个公会，即提出一个加入申请
     *
     * @param player
     * @param guildId
     */
    public void guildJoin(PlayerBeCache player, Long guildId) {
        if (Objects.nonNull(player.getGuildId()) && player.getGuildId() != 0) {
            notificationManager.notifyPlayer(player, "你已经在公会中了，只有退出当前公会后才能加入新的公会！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //判断公会是否存在
        GuildBeCache guild = guildCache.getGuildByGuildId(guildId);
        if (Objects.isNull(guild)) {
            notificationManager.notifyPlayer(player, "该公会并不存在，请检查输入的公会id", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        if(guild.getMemberIdList().size()>=guild.getMaxNum()){
            notificationManager.notifyPlayer(player, "该公会人数已经达到了上限！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        PlayerJoinRequest playerJoinRequest = new PlayerJoinRequest(new Date(), player.getId());
        guild.addJoinRequest(player.getId(), playerJoinRequest);
        notificationManager.notifyPlayer(player, "已经发出加入请求，等待处理中……", RequestCode.SUCCESS.getCode());
        //在这里或通知公会会长
        guild.getMemberIdList().forEach(playerId -> {
            if (playerLoginService.getPlayerById(playerId).getGuildRoleType().equals(RoleType.President.getCode())) {
                gameSystem.noticeSomeOne(playerId,
                        "有关公会",
                        MessageFormat.format("收到{0}加入{1}公会的申请，请注意处理!", player.getName(), guild.getName()),
                        null);
            }
        });
    }

    /**
     * 同意加入请求
     *
     * @param admin
     * @param playerId
     */
    public synchronized void guildPermit(PlayerBeCache admin, Integer playerId) {
        //判断权限、判断请求是否存在、判断请求者当前是否已加入公会（当前公会|其他公会）、执行操作、通知已加入
        Long guildId = admin.getGuildId();
        if (Objects.isNull(guildId) || guildId == 0) {
            notificationManager.notifyPlayer(admin, "你还未加入任何公会，不能进行此项操作！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        GuildBeCache guild = guildCache.getGuildByGuildId(guildId);
        if (!checkUpPower(admin, Command.GUILD_PERMIT.getRequestCode())) {
            notificationManager.notifyPlayer(admin, MessageFormat.format("你还没有这个权限：{0}",
                    Command.GUILD_PERMIT.getExplain()), RequestCode.BAD_REQUEST.getCode());
            return;
        }
        PlayerJoinRequest playerJoinRequest = guild.getPlayerJoinRequestMap().get(playerId);
        if (Objects.isNull(playerJoinRequest)) {
            notificationManager.notifyPlayer(admin, "该玩家并未提出申请，或申请已经处理！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        if(guild.getMemberIdList().size()>=guild.getMaxNum()){
            notificationManager.notifyPlayer(admin, "目前公会人数已经达到了上限！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //获取玩家
        PlayerBeCache player = playerLoginService.getPlayerById(playerId);
        Long playerGuildId = player.getGuildId();
        if (Objects.nonNull(playerGuildId)) {
            notificationManager.notifyPlayer(admin, "该玩家已经加入了其他公会，此操作无效！", RequestCode.WARNING.getCode());
            return;
        }
        player.setGuildId(guild.getId());
        player.setGuildRoleType(RoleType.Ordinary_Member.getCode());
        guild.deleteJoinRequest(playerId);
        guild.addPlayer(player);
        //更新操作应该移到统一的操作--------------------------------------------------------------------------------------
        notificationManager.notifyPlayer(admin, "已经同意该玩家加入公会！", RequestCode.SUCCESS.getCode());
        //通知加入者
        gameSystem.noticeSomeOne(player.getUserId(), "有关公会", MessageFormat.format("你已经加入公会：{0}", guild.getName()), null);
        EventBus.publish(new GuildEvent(player,guild));
    }

    /**
     * 检查某会员是否拥有某项权力
     *
     * @param admin
     * @param requestCode
     * @return
     */
    private boolean checkUpPower(PlayerBeCache admin, Integer requestCode) {
        Integer guildRoleType = admin.getGuildRoleType();
        RoleType enumByCode = RoleType.getEnumByCode(guildRoleType);
        if (Objects.isNull(enumByCode)) {
            return false;
        }
        return enumByCode.getPowerSet().contains(requestCode);
    }

    /**
     * 展示玩家所加入的公会的信息，包含名称、等级、成员列表、仓库内容
     * 注意：成员的申请列表需要单独查看（并不是所有人都可以查看申请列表）
     *
     * @param player
     */
    public void guildShow(PlayerBeCache player) {
        Long guildId = player.getGuildId();
        if (Objects.isNull(guildId) || guildId == 0) {
            notificationManager.notifyPlayer(player, "你还未加入任何公会，不能进行此项操作！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        GuildBeCache guild = guildCache.getGuildByGuildId(guildId);
        StringBuilder stringBuilder = new StringBuilder("你所加入的公会的信息如下：\n");
        stringBuilder.append(MessageFormat.format("名称：{0} 等级：{1} 成员数量：{2} \n成员列表:\n",
                guild.getName(), guild.getLevel(), guild.getMemberIdList().size()));
        guild.getMemberIdList().forEach(playerId -> {
            PlayerBeCache p = playerLoginService.getPlayerById(playerId);
            stringBuilder.append(MessageFormat.format("id:{0} name:{1} 职位：{2}\n",
                    p.getId(), p.getName(), RoleType.getEnumByCode(p.getGuildRoleType()).getDescribe()));
        });
        stringBuilder.append(MessageFormat.format("仓库容量：{0}\n", guild.getWarehouseSize()));
        guild.getWarehouseMap().values().forEach((tools) -> {
            stringBuilder.append(MessageFormat.format("toolsId:{0} 名称:{1}\n", tools.getUuid(), tools.getName()));
        });
        if(Objects.nonNull(guild.getGoldNum())&&guild.getGoldNum()>0){
            stringBuilder.append(MessageFormat.format("公会含有金币:{0}",guild.getGoldNum()));
        }
        notificationManager.notifyPlayer(player, stringBuilder.toString(), RequestCode.SUCCESS.getCode());
    }

    /**
     * 查看加入公会请求
     *
     * @param player
     */
    public void guildShowReq(PlayerBeCache player) {
        Long guildId = player.getGuildId();
        if (Objects.isNull(guildId) || guildId == 0) {
            notificationManager.notifyPlayer(player, "你还未加入任何公会，不能进行此项操作！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        GuildBeCache guild = guildCache.getGuildByGuildId(guildId);
        if (!checkUpPower(player, Command.GUILD_SHOW_REQ.getRequestCode())) {
            notificationManager.notifyPlayer(player, "你没有权限查看请求列表", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        StringBuilder stringBuilder = new StringBuilder("请求列表如下：\n");
        Map<Integer, PlayerJoinRequest> playerJoinRequestMap = guild.getPlayerJoinRequestMap();
        if (playerJoinRequestMap.size() == 0) {
            stringBuilder.append("还没有新的请求");
        } else {
            playerJoinRequestMap.values().forEach(v -> {
                PlayerBeCache p = playerLoginService.getPlayerById(v.getPlayerId());
                stringBuilder.append(MessageFormat.format("请求人id：{0} 请求人名称:{1} 请求时间:{2}\n",
                        p.getId(), p.getName(), v.getDate()));
            });
        }
        notificationManager.notifyPlayer(player, stringBuilder.toString(), RequestCode.SUCCESS.getCode());
    }

    /**
     * 向公会捐献金币
     *
     * @param player 玩家
     * @param number 捐献的数量
     */
    public void guildDonateCold(PlayerBeCache player, Integer number) {
        Long guildId = player.getGuildId();
        if (Objects.isNull(guildId) || guildId == 0) {
            notificationManager.notifyPlayer(player, "你还未加入任何公会，不能进行此项操作！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        GuildBeCache guild = guildCache.getGuildByGuildId(guildId);
        if (player.getMoney() < number) {
            notificationManager.notifyPlayer(player, "你的金币不足，不能进行此项操作！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        player.setMoney(player.getMoney() - number);
        //更改显示
        playerDataService.showPlayerInfo(player);
        guild.contributionGoldNum(number);
        notificationManager.notifyPlayer(player,MessageFormat.format("向公会{0}贡献{1}金币成功!",
                guild.getName(),number),RequestCode.SUCCESS.getCode());
    }

    /**
     * 向公会捐献物品
     *
     * @param player
     * @param toolsUuid
     */
    public void guildDonate(PlayerBeCache player, Long toolsUuid) {
        Long guildId = player.getGuildId();
        if (Objects.isNull(guildId) || guildId == 0) {
            notificationManager.notifyPlayer(player, "你还未加入任何公会，不能进行此项操作！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        GuildBeCache guild = guildCache.getGuildByGuildId(guildId);
        //判断玩家是否拥有该物品
        BagBeCache bagBeCache = player.getBagBeCache();

        Tools tools = bagService.containsTools(bagBeCache, toolsUuid);
        if (Objects.isNull(tools)) {
            notificationManager.notifyPlayer(player, "你的背包里并没有这件物品！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        if (guild.getWarehouseMap().size() < guild.getWarehouseSize()) {
            bagService.removeFromBag(bagBeCache, toolsUuid);
            playerDataService.showPlayerBag(player);
            guild.warehouseAdd(tools);
            notificationManager.notifyPlayer(player, "向公会的仓库捐献成功！", RequestCode.BAD_REQUEST.getCode());
        } else {
            //仓库已满
            notificationManager.notifyPlayer(player, "公会的仓库已满，不能进行捐献！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
    }

    /**
     * 退出公会
     *--------------------------------------------------应该通知所有会员
     * @param player
     */
    public void guildQuit(PlayerBeCache player) {
        Long guildId = player.getGuildId();
        if (Objects.isNull(guildId) || guildId == 0) {
            notificationManager.notifyPlayer(player, "你还未加入任何公会，不能进行此项操作！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        GuildBeCache guild = guildCache.getGuildByGuildId(guildId);
        //如果是会长退出，则解散公会
        if (player.getGuildRoleType().equals(RoleType.President.getCode())) {
            guildDismiss(player);
        }
        player.setGuildRoleType(null);
        player.setGuildId(null);
        //更新player表
        guild.removePlayer(player.getId());
        notificationManager.notifyPlayer(player, "你已退出公会！", RequestCode.SUCCESS.getCode());
    }

    /**
     * 解散公会
     *
     * @param player
     */
    public void guildDismiss(PlayerBeCache player) {
        Long guildId = player.getGuildId();
        if (Objects.isNull(guildId) || guildId == 0) {
            notificationManager.notifyPlayer(player, "你还未加入任何公会，不能进行此项操作！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        if (!checkUpPower(player, Command.GUILD_DISMISS.getRequestCode())) {
            notificationManager.notifyPlayer(player, "你没有解散公会的权限！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        GuildBeCache guild = guildCache.getGuildByGuildId(guildId);
        guild.getMemberIdList().forEach(id -> {
            PlayerBeCache player1 = playerLoginService.getPlayerById(id);
            player1.setGuildRoleType(null);
            player1.setGuildId(null);
            //给玩家发送邮件说明公会解散
        });

        guild.getMemberIdList().clear();
        notificationManager.notifyPlayer(player, "公会已解散！", RequestCode.SUCCESS.getCode());
    }

    /**
     * 授权
     *
     * @param admin
     * @param playerId
     * @param roleTypeCode
     */
    public void guildGrant(PlayerBeCache admin, Integer playerId, Integer roleTypeCode) {
        Long guildId = admin.getGuildId();
        if (Objects.isNull(guildId) || guildId == 0) {
            notificationManager.notifyPlayer(admin, "你还未加入任何公会，不能进行此项操作！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        if (!checkUpPower(admin, Command.GUILD_GRANT.getRequestCode())) {
            notificationManager.notifyPlayer(admin, "你没有向公会成员授权的权限！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        GuildBeCache guild = guildCache.getGuildByGuildId(guildId);
        if (!guild.getMemberIdList().contains(playerId)) {
            notificationManager.notifyPlayer(admin, "该玩家不在当前公会当中！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        PlayerBeCache player = playerLoginService.getPlayerById(playerId);
        if (player.getGuildRoleType() <= admin.getGuildRoleType()) {
            notificationManager.notifyPlayer(admin, "只能授权给当前权限比自己小的会员！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        if (roleTypeCode < admin.getGuildRoleType()) {
            notificationManager.notifyPlayer(admin, "只能授权给他人比自己小的权限！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        player.setGuildRoleType(roleTypeCode);
        RoleType enumByCode = RoleType.getEnumByCode(player.getGuildRoleType());
        if(Objects.nonNull(enumByCode)){
            notificationManager.notifyPlayer(admin, MessageFormat.format("授权{0}为{1}成功！",
                    player.getName(),enumByCode.getDescribe()), RequestCode.SUCCESS.getCode());
            notificationManager.notifyPlayer(player,MessageFormat.format("{0}将你在公会{1}的权限更改为{2}",
                    admin.getName(),enumByCode.getDescribe()),RequestCode.WARNING.getCode());
        }

    }

    /**
     * 从公会踢出玩家
     *
     * @param admin
     * @param playerId
     */
    public void guildKick(PlayerBeCache admin, Integer playerId) {
        Long guildId = admin.getGuildId();
        if (Objects.isNull(guildId) || guildId == 0) {
            notificationManager.notifyPlayer(admin, "你还未加入任何公会，不能进行此项操作！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        if (!checkUpPower(admin, Command.GUILD_KICK.getRequestCode())) {
            notificationManager.notifyPlayer(admin, "你没有从公会踢人的权限！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        GuildBeCache guild = guildCache.getGuildByGuildId(guildId);
        if (!guild.getMemberIdList().contains(playerId)) {
            notificationManager.notifyPlayer(admin, "该玩家不在当前公会当中！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        PlayerBeCache player = playerLoginService.getPlayerById(playerId);
        player.setGuildRoleType(null);
        player.setGuildId(null);
        guild.removePlayer(playerId);
        notificationManager.notifyPlayer(admin, MessageFormat.format("已经将玩家{0}踢出公会！",player.getName()), RequestCode.BAD_REQUEST.getCode());
        gameSystem.noticeSomeOne(playerId,"有关公会",MessageFormat.format("你被{0}踢出公会{1}",admin.getName(),guild.getName()),null);
    }

    /**
     * 从公会获取金币
     *
     * @param player
     */
    public void guildTakeCole(PlayerBeCache player) {
        Long guildId = player.getGuildId();
        if (Objects.isNull(guildId) || guildId == 0) {
            notificationManager.notifyPlayer(player, "你还未加入任何公会，不能进行此项操作！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        GuildBeCache guild = guildCache.getGuildByGuildId(guildId);
        if (!guild.takeGoldNum(100)) {
            notificationManager.notifyPlayer(player, "公会的金币不足以支出这次获取！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        player.setMoney(player.getMoney() + 100);
        //更改玩家显示
        playerDataService.showPlayerInfo(player);
        notificationManager.notifyPlayer(player,MessageFormat.format("从{0}公会获取100金币成功!",guild.getName()),RequestCode.SUCCESS.getCode());
    }

    /**
     * 从仓库获取物品
     * @param player
     * @param toolsId
     */
    public void guildTake(PlayerBeCache player, Long toolsId) {
        Long guildId = player.getGuildId();
        if (Objects.isNull(guildId) || guildId == 0) {
            notificationManager.notifyPlayer(player, "你还未加入任何公会，不能进行此项操作！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        GuildBeCache guild = guildCache.getGuildByGuildId(guildId);
        if(!guild.getWarehouseMap().containsKey(toolsId)){
            //仓库中不存在这件物品
            notificationManager.notifyPlayer(player, "仓库中不存在这件物品！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //判断玩家背包是否还有空
        if(!bagService.havePlace(player.getBagBeCache())) {
            //背包现在没空
            notificationManager.notifyPlayer(player, "你的背包现在没空位！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        Tools tools = guild.warehouseTake(toolsId);
        bagService.putInBag(player,tools);
        notificationManager.notifyPlayer(player, "从公会获取物品成功！", RequestCode.BAD_REQUEST.getCode());
    }

    /**
     * 查看公会列表
     * @param context
     */
    public void guildList(ChannelHandlerContext context) {
        ConcurrentMap<Long, GuildBeCache> allGuildInCache = guildCache.getAll();
        StringBuilder stringBuilder = new StringBuilder("热门公会信息：\n");
        allGuildInCache.values().forEach(guild->{
            stringBuilder.append(MessageFormat.format("id:{0}   名称:{1}    当前人数:{2}\n",
                    guild.getId(),
                    guild.getName(),
                    guild.getMemberIdList().size()));
        });
        notificationManager.notifyByCtx(context,stringBuilder,RequestCode.SUCCESS.getCode());
    }
}
