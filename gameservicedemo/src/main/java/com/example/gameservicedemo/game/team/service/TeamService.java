package com.example.gameservicedemo.game.team.service;

import com.example.commondemo.base.RequestCode;
import com.example.gameservicedemo.game.copy.bean.GameCopyScene;
import com.example.gameservicedemo.game.copy.service.GameCopyService;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.service.PlayerDataService;
import com.example.gameservicedemo.game.player.service.PlayerLoginService;
import com.example.gameservicedemo.game.team.bean.Team;
import com.example.gameservicedemo.game.team.bean.TeamRequest;
import com.example.gameservicedemo.game.team.cache.TeamCache;
import com.example.gameservicedemo.manager.NotificationManager;
import com.example.gameservicedemo.util.SnowFlake;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/26/12:01
 * @Description:
 */
@Slf4j
@Service
public class TeamService {
    @Autowired
    TeamCache teamCache;
    @Autowired
    PlayerLoginService playerLoginService;
    @Autowired
    PlayerDataService playerDataService;
    @Autowired
    GameCopyService gameCopyService;
    @Autowired
    NotificationManager notificationManager;

    /**
     * 创建队伍
     *
     * @param context
     */
    public void creatTeam(ChannelHandlerContext context) {
        PlayerBeCache playerByContext = playerLoginService.getPlayerByContext(context);
        if (Objects.isNull(playerByContext)) {
            notificationManager.notifyByCtx(context, "你还未加载角色！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        Team team = new Team();
        SnowFlake snowFlake = new SnowFlake(1, 1);
        long l = snowFlake.nextId();
        team.setId(l);
        team.setCaptainId(Long.valueOf(playerByContext.getId()));
        team.getTeamPlayer().put(Long.valueOf(playerByContext.getId()), playerByContext);
        teamCache.putTeam(team);
        playerByContext.setTeamId(team.getId());
        notificationManager.notifyPlayer(playerByContext,
                MessageFormat.format("你已经创建了队伍，并且身处其中，队伍id：{0} ，使用\"team_show\"查看当前队伍情况", team.getId()),
                RequestCode.SUCCESS.getCode());
    }

    /**
     * 查看当前队伍成员
     *
     * @param context
     */
    public void teamShow(ChannelHandlerContext context) {
        Team team = checkPlayerHaveTeam(context);
        if (Objects.isNull(team)) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder(MessageFormat.format("当前队伍id:{0},成员有：\n", team.getId()));
        team.getTeamPlayer().values().forEach(player -> {
            stringBuilder.append(MessageFormat.format("id:{0} 昵称:{1}", player.getId(), player.getName()));
            if (Long.valueOf(player.getId()).equals(team.getCaptainId())) {
                stringBuilder.append("  队长");
            }
            stringBuilder.append("\n");
        });
        notificationManager.notifyByCtx(context, stringBuilder.toString(), RequestCode.SUCCESS.getCode());
    }

    /**
     * 队长从队伍里面踢人
     *
     * @param context
     */
    public void kickFromTeam(ChannelHandlerContext context, Integer playerId) {
        Team team = checkPlayerHaveTeam(context);
        if (Objects.isNull(team)) {
            return;
        }
        if (!Long.valueOf(playerLoginService.getPlayerByContext(context).getId()).equals(team.getCaptainId())) {
            notificationManager.notifyByCtx(context, "你不是队长！无权踢人", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        PlayerBeCache playerById = playerLoginService.getPlayerById(playerId);
        team.getTeamPlayer().remove(Long.valueOf(playerId));
        notificationManager.notifyTeam(team, MessageFormat.format("{0}离开了队伍", playerById.getName()), RequestCode.BAD_REQUEST.getCode());
        notificationManager.notifyPlayer(playerById, "你已被踢出队伍！", RequestCode.BAD_REQUEST.getCode());
        playerById.setTeamId(null);
    }

    /**
     * 判断是否加载，并有队伍
     *
     * @param context
     * @return
     */
    public Team checkPlayerHaveTeam(ChannelHandlerContext context) {
        PlayerBeCache playerByContext = playerLoginService.getPlayerByContext(context);
        if (Objects.isNull(playerByContext)) {
            notificationManager.notifyByCtx(context, "你还未加载角色！", RequestCode.BAD_REQUEST.getCode());
            return null;
        }
        Long teamId = playerByContext.getTeamId();
        if (Objects.isNull(teamId)) {
            notificationManager.notifyPlayer(playerByContext, "你并没有加入队伍 \n", RequestCode.BAD_REQUEST.getCode());
            return null;
        }
        return teamCache.getTeam(teamId);
    }


    public Team getTeamById(Long teamId) {
        return teamCache.getTeam(teamId);
    }

    /**
     * 邀请某个玩家进入队伍
     *
     * @param context
     * @param playerId
     */
    public void publishTeamRequest(ChannelHandlerContext context, Integer playerId) {
        Team team = checkPlayerHaveTeam(context);
        if (Objects.isNull(team)) {
            return;
        }
        //邀请者
        PlayerBeCache inviter = playerLoginService.getPlayerByContext(context);
        //被邀请者
        PlayerBeCache invitee = playerLoginService.getPlayerById(playerId);
        if(Objects.isNull(invitee)){
            notificationManager.notifyPlayer(inviter, "该玩家并不在线！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        if (team.getTeamPlayer().size() == team.getTeamSize()) {
            notificationManager.notifyPlayer(inviter, "队伍已满,不能继续邀请！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        if (Objects.nonNull(invitee.getTeamId())) {
            notificationManager.notifyPlayer(inviter, MessageFormat.format("{0}已经在其他队伍当中了，不能发出邀请！",
                    invitee.getName()), RequestCode.BAD_REQUEST.getCode());
            return;
        }
        SnowFlake snowFlake = new SnowFlake(1, 1);
        long l = snowFlake.nextId();
        TeamRequest teamRequest = new TeamRequest(l, Long.valueOf(inviter.getId()), Long.valueOf(invitee.getId()), team.getId());
        teamCache.putTeamRequest(teamRequest);
        notificationManager.notifyPlayer(inviter, MessageFormat.format("已经向{0}发出了组队邀请",
                invitee.getName()
        ), RequestCode.SUCCESS.getCode());
        notificationManager.notifyPlayer(invitee, MessageFormat.format("收到了{0}的组队邀请,邀请id为：{1},接受使用\"accept 邀请id\"",
                inviter.getName(),
                teamRequest.getId()
        ), RequestCode.SUCCESS.getCode());
    }

    /**
     * 接受某一组队请求
     *
     * @param context
     * @param teamRequestId
     */
    public void acceptTeamRequest(ChannelHandlerContext context, Long teamRequestId) {
        PlayerBeCache playerByContext = playerLoginService.getPlayerByContext(context);
        if (Objects.isNull(playerByContext)) {
            notificationManager.notifyByCtx(context, "你还未加载角色！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        TeamRequest teamRequest = teamCache.getTeamRequest(teamRequestId);
        if (Objects.isNull(teamRequest)) {
            notificationManager.notifyByCtx(context, "已超过加入时间，或邀请码错误！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        if (!teamRequest.getAcceptPlayerId().equals(Long.valueOf(playerByContext.getId()))) {
            notificationManager.notifyByCtx(context, "该请求邀请的不是你！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        Team team = teamCache.getTeam(teamRequest.getTeamId());
        if (Objects.isNull(team)) {
            notificationManager.notifyByCtx(context, "队伍已销毁，加入失败！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        if (team.getTeamPlayer().size() == team.getTeamSize()) {
            notificationManager.notifyByCtx(context, "队伍成员已满，加入失败！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        team.getTeamPlayer().put(Long.valueOf(playerByContext.getId()), playerByContext);
        playerByContext.setTeamId(team.getId());
        notificationManager.notifyTeam(team, MessageFormat.format("{0}加入了队伍！", playerByContext.getName()), RequestCode.SUCCESS.getCode());
    }


    /**
     * 主动退出队伍
     *
     * @param context
     */
    public void leaveTeam(ChannelHandlerContext context) {
        Team team = checkPlayerHaveTeam(context);
        if (Objects.isNull(team)) {
            return;
        }
        PlayerBeCache leaver = playerLoginService.getPlayerByContext(context);
        team.getTeamPlayer().remove(Long.valueOf(leaver.getId()));
        leaver.setTeamId(null);
        //如果离开后队伍没有人了，销毁队伍
        if (team.getTeamPlayer().size() == 0) {
            teamCache.removeTeam(team.getId());
            return;
        }
        notificationManager.notifyTeam(team, MessageFormat.format("{0}退出了队伍", leaver.getName()), RequestCode.SUCCESS.getCode());
        notificationManager.notifyPlayer(leaver, "你已离开了队伍！", RequestCode.SUCCESS.getCode());
        // 如果退出者是队长，随机指定一个队员作为队长
        if (Long.valueOf(leaver.getId()).equals(team.getCaptainId())) {
            team.getTeamPlayer().values().stream().findAny().ifPresent(
                    p -> team.setCaptainId(Long.valueOf(p.getId()))
            );
            notificationManager.notifyTeam(team, MessageFormat.format("队长更换，现在的队长是：{0}",
                    playerLoginService.getPlayerById(Integer.valueOf(team.getCaptainId().toString())).getName()
            ), RequestCode.SUCCESS.getCode());
        }
    }

    public void enterGameCopy(ChannelHandlerContext context, Integer copyId) {
        Team team = checkPlayerHaveTeam(context);
        if (Objects.isNull(team)) {
            return;
        }
        PlayerBeCache playerByContext = playerLoginService.getPlayerByContext(context);
        if(!team.getCaptainId().equals(Long.valueOf(playerByContext.getId()))){
            notificationManager.notifyPlayer(playerByContext,"你不是队长，只有队长才可以使队伍进入副本！",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //调用进入副本的方法
        gameCopyService.enterGameCopyByTeam(team,copyId);
        notificationManager.notifyTeam(team,"队伍正在进入副本……",RequestCode.SUCCESS.getCode());

    }
}
