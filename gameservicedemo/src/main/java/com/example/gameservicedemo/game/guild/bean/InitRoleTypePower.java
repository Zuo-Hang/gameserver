package com.example.gameservicedemo.game.guild.bean;

import com.example.commondemo.base.Command;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/09/17:39
 * @Description: 初始化权限
 */
@Component
public class InitRoleTypePower {
    /**
     * 对于未参加公会的player可以创建公会和提出加入公会的申请。
     */
    @PostConstruct
    private void initPower(){
        //会长可以进行的操作 查看公会、公会捐献、授权公会成员、获取公会物品、允许入会、退出公会、从公会踢出会员、解散公会
        RoleType.President.getPowerSet().add(Command.GUILD_PERMIT.getRequestCode());
        RoleType.President.getPowerSet().add(Command.GUILD_SHOW.getRequestCode());
        RoleType.President.getPowerSet().add(Command.GUILD_DONATE.getRequestCode());
        RoleType.President.getPowerSet().add(Command.GUILD_GRANT.getRequestCode());
        RoleType.President.getPowerSet().add(Command.GUILD_TAKE.getRequestCode());
        RoleType.President.getPowerSet().add(Command.GUILD_QUIT.getRequestCode());
        RoleType.President.getPowerSet().add(Command.GUILD_KICK.getRequestCode());
        RoleType.President.getPowerSet().add(Command.GUILD_DISMISS.getRequestCode());
        //副会长可以进行的操作 查看公会、公会捐献、授权公会成员（只能授权比自己低的）、获取公会物品、允许入会、退出公会、从公会踢出会员
        RoleType.Vice_President.getPowerSet().add(Command.GUILD_PERMIT.getRequestCode());
        RoleType.Vice_President.getPowerSet().add(Command.GUILD_SHOW.getRequestCode());
        RoleType.Vice_President.getPowerSet().add(Command.GUILD_DONATE.getRequestCode());
        RoleType.Vice_President.getPowerSet().add(Command.GUILD_GRANT.getRequestCode());
        RoleType.Vice_President.getPowerSet().add(Command.GUILD_TAKE.getRequestCode());
        RoleType.Vice_President.getPowerSet().add(Command.GUILD_QUIT.getRequestCode());
        RoleType.Vice_President.getPowerSet().add(Command.GUILD_KICK.getRequestCode());
        //精英可以进行的操作 查看公会、公会捐献、获取公会物品、允许入会、退出公会
        RoleType.Elite.getPowerSet().add(Command.GUILD_PERMIT.getRequestCode());
        RoleType.Elite.getPowerSet().add(Command.GUILD_SHOW.getRequestCode());
        RoleType.Elite.getPowerSet().add(Command.GUILD_DONATE.getRequestCode());
        RoleType.Elite.getPowerSet().add(Command.GUILD_TAKE.getRequestCode());
        RoleType.Elite.getPowerSet().add(Command.GUILD_QUIT.getRequestCode());
        //会员可以进行的操作 查看公会、公会捐献、获取公会物品、退出公会
        RoleType.Ordinary_Member.getPowerSet().add(Command.GUILD_PERMIT.getRequestCode());
        RoleType.Ordinary_Member.getPowerSet().add(Command.GUILD_SHOW.getRequestCode());
        RoleType.Ordinary_Member.getPowerSet().add(Command.GUILD_DONATE.getRequestCode());
        RoleType.Ordinary_Member.getPowerSet().add(Command.GUILD_TAKE.getRequestCode());
        RoleType.Ordinary_Member.getPowerSet().add(Command.GUILD_QUIT.getRequestCode());
    }
}
