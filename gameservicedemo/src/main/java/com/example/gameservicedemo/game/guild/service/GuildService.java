package com.example.gameservicedemo.game.guild.service;

import com.example.commondemo.base.Command;
import com.example.commondemo.base.RequestCode;
import com.example.gamedatademo.bean.Player;
import com.example.gamedatademo.mapper.PlayerMapper;
import com.example.gameservicedemo.base.IdGenerator;
import com.example.gameservicedemo.game.bag.bean.BagBeCache;
import com.example.gameservicedemo.game.bag.service.BagService;
import com.example.gameservicedemo.game.guild.bean.GuildBeCache;
import com.example.gameservicedemo.game.guild.bean.PlayerJoinRequest;
import com.example.gameservicedemo.game.guild.bean.RoleType;
import com.example.gameservicedemo.game.guild.cache.GuildCache;
import com.example.gameservicedemo.game.mail.bean.GameSystem;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.tools.bean.Tools;
import com.example.gameservicedemo.manager.NotificationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/09/17:07
 * @Description:-------------------------------------现在还是线程不安全的，需要改进
 */
@Slf4j
@Service
public class GuildService {
    @Autowired
    NotificationManager notificationManager;
    @Autowired
    GuildCache guildCache;
    @Autowired
    GameSystem gameSystem;
    @Autowired
    BagService bagService;
    @Autowired
    PlayerMapper playerMapper;

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
        guildBeCache.getMemberMap().put(player.getId(), player);
        player.setGuildId(guildBeCache.getId());
        player.setGuildRoleType(RoleType.President.getCode());
        guildCache.putInCache(guildBeCache);
        guildCache.insertGuild(guildBeCache);
        notificationManager.notifyPlayer(player, "公会已经创建完毕！", RequestCode.SUCCESS.getCode());
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
        PlayerJoinRequest playerJoinRequest = new PlayerJoinRequest(new Date(), player);
        guild.getPlayerJoinRequestMap().put(player.getId(), playerJoinRequest);
        notificationManager.notifyPlayer(player, "已经发出加入请求，等待处理中……", RequestCode.SUCCESS.getCode());
        //在这里或通知公会会长
        guild.getMemberMap().values().forEach(p -> {
            if (p.getGuildRoleType().equals(RoleType.President.getCode())) {
                gameSystem.noticeSomeOne(p.getId(),
                        "有关公会",
                        MessageFormat.format("收到{0}加入{1}公会的申请，请注意处理!", player.getName(), guild.getName()),
                        null);
            }
        });
        guildCache.updateGuild(guild);
    }

    /**
     * 同意加入请求
     *
     * @param admin
     * @param playerId
     */
    public void guildPermit(PlayerBeCache admin, Integer playerId) {
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
        Player player = playerMapper.selectByPlayerId(playerId);
        Long playerGuildId = player.getGuildId();
        if (Objects.nonNull(playerGuildId)) {
            notificationManager.notifyPlayer(admin, "该玩家已经加入了其他公会，此操作无效！", RequestCode.WARNING.getCode());
            return;
        }
        player.setGuildId(guild.getId());
        player.setGuildRoleType(RoleType.Ordinary_Member.getCode());
        guild.getPlayerJoinRequestMap().remove(playerId);
        //更新操作应该移到统一的操作--------------------------------------------------------------------------------------
        playerMapper.updateByPlayerId(player);
        notificationManager.notifyPlayer(admin, "已经同意该玩家加入公会！", RequestCode.SUCCESS.getCode());
        //通知加入者
        gameSystem.noticeSomeOne(player.getUserId(), "有关公会", MessageFormat.format("你已经加入公会：{0}", guild.getName()), null);
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
        stringBuilder.append(MessageFormat.format("名称：{0} 等级：{1} 成员数量：{3} \n成员列表:\n",
                guild.getName(), guild.getLevel(), guild.getMemberMap().size()));
        guild.getMemberMap().values().forEach(p -> {
            stringBuilder.append(MessageFormat.format("id:{0} name:{1} 职位：{3}\n",
                    p.getId(), p.getName(), RoleType.getEnumByCode(p.getGuildRoleType()).getDescribe()));
        });
        stringBuilder.append(MessageFormat.format("仓库容量：{0}\n", guild.getWarehouseSize()));
        guild.getWarehouseMap().values().forEach((tools) -> {
            stringBuilder.append(MessageFormat.format("toolsId:{0} 名称:{1}", tools.getUuid(), tools.getName()));
        });
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
            playerJoinRequestMap.values().forEach(p -> {
                stringBuilder.append(MessageFormat.format("请求人id：{0} 请求人名称:{1} 请求时间:{2}\n",
                        p.getPlayer().getId(), p.getPlayer().getName(), p.getDate()));
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
        //-----------------------------------------------------安全问题
        guild.setColdNum(guild.getColdNum() + number);
        //----------------------------------------更新玩家数据库
        guildCache.updateGuild(guild);
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
            guild.getWarehouseMap().put(tools.getUuid(), tools);
        } else {
            //仓库已满
            notificationManager.notifyPlayer(player, "公会的仓库已满，不能进行捐献！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
    }

    /**
     * 退出公会
     *
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
        guild.getMemberMap().remove(player.getId());
        guildCache.updateGuild(guild);
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
        guild.getMemberMap().keySet().forEach(id -> {
            Player player1 = playerMapper.selectByPlayerId(id);
            player1.setGuildRoleType(null);
            player1.setGuildId(null);
            playerMapper.updateByPlayerId(player1);
            //给玩家发送邮件说明公会解散
        });
        guild.getMemberMap().clear();
        guildCache.updateGuild(guild);
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
        if (Objects.isNull(guild.getMemberMap().get(playerId))) {
            notificationManager.notifyPlayer(admin, "该玩家不在当前公会当中！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        Player player = playerMapper.selectByPlayerId(playerId);
        if (player.getGuildRoleType() <= admin.getGuildRoleType()) {
            notificationManager.notifyPlayer(admin, "只能授权给当前权限比自己小的会员！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        if (roleTypeCode < admin.getGuildRoleType()) {
            notificationManager.notifyPlayer(admin, "只能授权给他人比自己小的权限！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        player.setGuildRoleType(roleTypeCode);
        //更新数据库
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
        if (Objects.isNull(guild.getMemberMap().get(playerId))) {
            notificationManager.notifyPlayer(admin, "该玩家不在当前公会当中！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        Player player = playerMapper.selectByPlayerId(playerId);
        player.setGuildRoleType(null);
        player.setGuildId(null);
        guild.getMemberMap().remove(playerId);
        guildCache.updateGuild(guild);
        //---------------更新player表
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
        if (!guild.takeColdNum(100)) {
            notificationManager.notifyPlayer(player, "公会的金币不足以支出这次获取！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        guildCache.updateGuild(guild);
        player.setMoney(player.getMoney() + 100);
        playerMapper.updateByPlayerId(player);
        //更改玩家显示
    }
}
