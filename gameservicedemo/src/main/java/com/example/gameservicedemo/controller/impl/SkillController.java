package com.example.gameservicedemo.controller.impl;

import com.example.commondemo.base.Command;
import com.example.commondemo.message.Message;
import com.example.gameservicedemo.controller.ControllerManager;
import com.example.gameservicedemo.service.skill.SkillService;
import com.example.gameservicedemo.util.CheckParametersUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/13/15:38
 * @Description: 技能控制器
 */
@Component
@Slf4j
public class SkillController {
    @Autowired
    SkillService skillService;

    {
        ControllerManager.add(Command.SKILL_TO_SELF.getRequestCode(), this::useSkillToSelf);
    }

    /**
     * 使用技能
     * @param context
     * @param message
     */
    public void useSkillToSelf(ChannelHandlerContext context, Message message) {
        //命令名称 技能id
        String[] strings = CheckParametersUtil.checkParameter(context, message, 2);
        Integer skillId = Integer.valueOf(strings[1]);
        //---------------------------------判断玩家是否加载
        skillService.useSkillToSelf(context,skillId);
    }


}
