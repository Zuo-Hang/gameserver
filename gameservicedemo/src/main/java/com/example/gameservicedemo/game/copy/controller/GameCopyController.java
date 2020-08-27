package com.example.gameservicedemo.game.copy.controller;

import com.example.commondemo.base.Command;
import com.example.commondemo.message.Message;
import com.example.gameservicedemo.base.controller.ControllerManager;
import com.example.gameservicedemo.game.copy.bean.GameCopyScene;
import com.example.gameservicedemo.game.copy.service.GameCopyService;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.service.PlayerLoginService;
import com.example.gameservicedemo.util.CheckParametersUtil;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/27/10:13
 * @Description:
 */
@Controller
public class GameCopyController {
    @Autowired
    PlayerLoginService playerLoginService;
    @Autowired
    GameCopyService gameCopyService;

    {
        ControllerManager.add(Command.ENTER_GAME_COPY.getRequestCode(), this::enterGameCopy);
        ControllerManager.add(Command.EXIT_GAME_COPY.getRequestCode(), this::exitGameCopy);
        ControllerManager.add(Command.SHOW_GAME_COPY.getRequestCode(), this::showGameCopy);
    }

    private void showGameCopy(ChannelHandlerContext context, Message message) {
        gameCopyService.showGameCopy(context);
    }

    private void exitGameCopy(ChannelHandlerContext context, Message message) {
        PlayerBeCache playerByContext = playerLoginService.getPlayerByContext(context);
        if (gameCopyService.isInGameCopy(context)) {
            return;
        }
        gameCopyService.exitGameCopy(playerByContext, (GameCopyScene) playerByContext.getSceneNowAt());
    }

    private void enterGameCopy(ChannelHandlerContext context, Message message) {
        String[] strings = CheckParametersUtil.checkParameter(context, message, 2);
        if (Objects.isNull(strings)) {
            return;
        }
        gameCopyService.enterGameCopy(context, Integer.valueOf(strings[1]));
    }
}
