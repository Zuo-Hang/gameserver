package com.example.gameservicedemo.game.guild.controller;

import com.example.commondemo.base.Command;
import com.example.commondemo.message.Message;
import com.example.gameservicedemo.base.controller.ControllerManager;
import com.example.gameservicedemo.game.guild.service.GuildService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/09/17:06
 * @Description:
 */
@Slf4j
@Component
public class GuildController {
    @Autowired
    GuildService guildService;

    {
        ControllerManager.add(Command.GUILD_CREATE.getRequestCode(),this::guildCreate);
        ControllerManager.add(Command.GUILD_SHOW.getRequestCode(),this::guildShow);
        ControllerManager.add(Command.GUILD_JOIN.getRequestCode(),this::guildJoin);
        ControllerManager.add(Command.GUILD_PERMIT.getRequestCode(),this::guildPermit);
        ControllerManager.add(Command.GUILD_DONATE.getRequestCode(),this::guildDonate);
        ControllerManager.add(Command.GUILD_TAKE.getRequestCode(),this::guildTake);
        ControllerManager.add(Command.GUILD_GRANT.getRequestCode(),this::guildGrant);
        ControllerManager.add(Command.GUILD_QUIT.getRequestCode(),this::guildQuit);
    }

    private void guildQuit(ChannelHandlerContext context, Message message) {
    }

    private void guildGrant(ChannelHandlerContext context, Message message) {
    }

    private void guildTake(ChannelHandlerContext context, Message message) {
    }

    private void guildDonate(ChannelHandlerContext context, Message message) {
    }

    private void guildPermit(ChannelHandlerContext context, Message message) {
    }

    private void guildJoin(ChannelHandlerContext context, Message message) {
    }

    private void guildShow(ChannelHandlerContext context, Message message) {
    }

    private void guildCreate(ChannelHandlerContext context, Message message) {
    }

}
