package com.example.commondemo.base;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/03/21:14
 * @Description:
 */
public enum Command {
    /** 创建用户 */
    USER_CREATE("user_create",900,"创建用户，参数为用户昵称、密码、手机号，会返回登陆的唯一id, 例： user_create 玩家66 123 1501545"),
    /** 创建角色*/
    PLAYER_CREATE("role_create",9001,"创建角色,参数为角色名 例：role_create  战不息 "),
     /** 玩家登陆, 参数为 用户账号和密码，例： login 2 123456*/
    USER_LOGIN("login",1001,"玩家登陆, 参数为 用户账号和密码，例： login 2 123456"),
     /** 角色登陆 ，参数为 当前用户下的角色id，例： 2001 1313*/
    PLAYER_LOGIN("load",2001," 角色登陆 ，参数为 当前用户下的角色id，例： 2001 1313"),
     /** 玩家退出，无参数， 例： exit*/
    PLAYER_EXIT("exit",2002,"玩家退出，无参数， 例： exit"),
    /** 角色移动， 参数 场景id ， 例： move 2 **/
    MOVE("move",3001,"角色移动， 参数 场景id ， 例： move 2"),
    /** 角色从当前位置可以进行的移动 例： move **/
    CAM_MOVE("can_move",3002,"可以移动到的地方，参数 无"),
    /** AOI, 显示场景内各种游戏对象 **/
    AOI("aoi",4002,"AOI, 显示场景内各种游戏对象"),
    /** 与npc谈话 **/
    TALK_WITH_NPC("talk",13002,"与npc谈话"),
    /** 未知的命令 */
    UNKNOWN("unknown", 9999,"未知的命令"),
    /** 心跳 **/
    HERATBEAT("heartbeat",0,"心跳")
    ;
    private String command;

    private Integer requestCode;

    /** 说明 **/
    private String explain;

    private static final Map<String, Command> COMMAND_MAP = new HashMap<>();
    private static final Map<Integer, Command> ID_MAP = new  HashMap<>();

    Command(String command, Integer requestCode) {
        this.command = command;
        this.requestCode = requestCode;
    }


    Command(String command, Integer requestCode, String explain) {
        this.command = command;
        this.requestCode = requestCode;
        this.explain = explain;
    }

    /**
     *  程序启动时将字符串的命令与枚举对象通过map关联起来
     */
    static {
        for (Command e : EnumSet.allOf(Command.class)) {
            COMMAND_MAP.put(e.command,e);
            ID_MAP.put(e.requestCode,e);
        }
    }
    /**
     *  通过字符串命令查找命令枚举，如果找不到，返回一个默认的枚举对象
     * @param command 字符串命令
     * @param defaultValue 默认命令枚举
     * @return 一个相关服务的枚举
     */
    public static Command findByCommand(String command, Command defaultValue){
        Command value = COMMAND_MAP.get(command);
        if(value == null){
            return defaultValue;
        }
        return value;
    }
    public static Command find(int requestCode, Command defaultValue){
        Command value = ID_MAP.get(requestCode);
        if(value == null){
            return defaultValue;
        }
        return value;
    }
    public Integer getRequestCode() {
        return requestCode;
    }
}
