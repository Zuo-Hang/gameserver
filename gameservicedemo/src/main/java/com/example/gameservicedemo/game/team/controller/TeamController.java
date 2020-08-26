package com.example.gameservicedemo.game.team.controller;

import com.example.commondemo.base.Command;
import com.example.commondemo.message.Message;
import com.example.gameservicedemo.base.controller.ControllerManager;
import com.example.gameservicedemo.game.team.service.TeamService;
import com.example.gameservicedemo.util.CheckParametersUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/26/11:56
 * @Description: 队伍相关的控制器
 */
@Slf4j
@Controller
public class TeamController {
    @Autowired
    TeamService teamService;

    {
        ControllerManager.add(Command.PUBLISH_TEAM_REQUEST.getRequestCode(), this::publishTeamRequest);
        ControllerManager.add(Command.LEAVE_TEAM.getRequestCode(), this::teamQuit);
        ControllerManager.add(Command.TEAM_SHOW.getRequestCode(), this::teamShow);
        ControllerManager.add(Command.ACCEPT_TEAM_REQUEST.getRequestCode(), this::acceptTeamRequest);
        ControllerManager.add(Command.CREAT_TEAM.getRequestCode(), this::creatTeam);
        ControllerManager.add(Command.KICK_FROM_TEAM.getRequestCode(),this::kickFromTeam);
    }


    private void creatTeam(ChannelHandlerContext context, Message message) {
        String[] strings = CheckParametersUtil.checkParameter(context, message, 1);
        if (Objects.isNull(strings)) {
            return;
        }
        teamService.creatTeam(context);
    }
    private void teamShow(ChannelHandlerContext context, Message message) {
        String[] strings = CheckParametersUtil.checkParameter(context, message, 1);
        if (Objects.isNull(strings)) {
            return;
        }
        teamService.teamShow(context);
    }

    private void publishTeamRequest(ChannelHandlerContext context, Message message) {
        //组队请求  分为两种：好友组队，全服广播组队  现阶段只需按id单个发出组队请求即可
        String[] strings = CheckParametersUtil.checkParameter(context, message, 2);
        if (Objects.isNull(strings)) {
            return;
        }
        teamService.publishTeamRequest(context,Integer.valueOf(strings[1]));
    }

    private void acceptTeamRequest(ChannelHandlerContext context, Message message) {
        String[] strings = CheckParametersUtil.checkParameter(context, message, 2);
        if (Objects.isNull(strings)) {
            return;
        }
        teamService.acceptTeamRequest(context,Long.valueOf(strings[1]));
    }

    private void teamQuit(ChannelHandlerContext context, Message message) {
        //退出时要检查队伍还剩几个人，没人时销毁队伍
        String[] strings = CheckParametersUtil.checkParameter(context, message, 1);
        if (Objects.isNull(strings)) {
            return;
        }
        teamService.leaveTeam(context);
    }


    private void kickFromTeam(ChannelHandlerContext context, Message message) {
        String[] strings = CheckParametersUtil.checkParameter(context, message, 2);
        if (Objects.isNull(strings)) {
            return;
        }
        teamService.kickFromTeam(context,Integer.valueOf(strings[1]));
    }
}
