package com.example.gameclientdemo.client;

import com.example.commondemo.base.Command;
import com.example.commondemo.base.RequestCode;
import com.example.commondemo.message.Message;

import java.text.MessageFormat;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/18/12:24
 * @Description: 帮助文档
 */
public class Help {
    /**
     * 获取某一条指令的帮助文档
     * @param command
     */
    static void help(Command command){
        MainView.outputAppend(RequestCode.WARNING.getCode(),MessageFormat.format("{0}:{1}\n",
                command.getCommand(),
                command.getExplain()));
    }

    /**
     * 获取所有的指令的帮助信息
     */
    static void helpAll(){
        Command[] values = Command.values();
        for (Command value : values) {
            help(value);
        }
    }
}
