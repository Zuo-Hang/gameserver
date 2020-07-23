package com.example.commondemo.entity;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hang hang
 * @Date: 2020/07/23/15:47
 * @Description:通用服务id，服务端根据这个服务id分发请求
 *  * 1. 1000 - 1999 之间的服务与玩家，角色登陆相关
 *  * 2. 2000 - 2999 之间的服务与获取游戏内信息有关
 *  * 1. 1000 - 1999 之间的服务与角色场景相关
 */
public enum Command {
    // 创建用户
    USER_CREATE("user_create",900,"创建用户，参数为用户昵称、密码、手机号，会返回登陆的唯一id, 例： user_create 玩家66 123 1501545"),

    // 创建角色
    ROLE_CREATE("role_create",9001,"创建角色,参数为角色名，职业代码，例：role_create  战不息 1"),

    // 玩家登陆, 参数为 用户账号和密码，例： login 2 123456
    USER_LOGIN("login",1001,"玩家登陆, 参数为 用户账号和密码，例： login 2 123456"),

    // 玩家退出，无参数， 例： exit
    PLAYER_EXIT("exit",2002,"玩家退出，无参数， 例： exit"),

    /** 角色移动， 参数 场景id ， 例： move 2 **/
    MOVE("move",3001,"角色移动， 参数 场景id ， 例： move 2"),

    /** AOI, 显示场景内各种游戏对象 **/
    AOI("aoi",3002,"AOI, 显示场景内各种游戏对象")
    ;

    private String command;

    private int index;

    /** 说明 **/
    private String explain;

    Command(String command, int index, String explain) {
        this.command = command;
        this.index = index;
        this.explain = explain;
    }

    Command(String command, int index) {
        this.command = command;
        this.index = index;
    }

    private static final Map<String, Command> COMMAND_MAP = new HashMap<>();
    private static final Map<Integer, Command> INDEX_MAP = new  HashMap<>();

    /**
     *  程序启动时将字符串的命令与枚举对象通过map关联起来
     */
    static {
        for (Command e : EnumSet.allOf(Command.class)) {
            COMMAND_MAP.put(e.command,e);
            INDEX_MAP.put(e.index,e);
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

    /**
     *  通过整型的服务id查找命令枚举，如果找不到，返回一个默认的枚举对象
     * @param index  服务id
     * @param defaultValue  默认命令枚举
     * @return 一个相关服务的枚举
     */
    public static Command find(int index, Command defaultValue){
        Command value = INDEX_MAP.get(index);
        if(value == null){
            return defaultValue;
        }
        return value;
    }

    public String getCommand() {
        return command;
    }

    public int getIndex() {
        return index;
    }

    public String getExplain() {
        return explain;
    }

    @Override
    public String toString() {
        return "Command{" +
                "command='" + command + '\'' +
                ", index=" + index +
                ", explain='" + explain + '\'' +
                '}';
    }
}
