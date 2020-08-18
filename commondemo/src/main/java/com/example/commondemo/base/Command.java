package com.example.commondemo.base;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/03/21:14
 * @Description: 服务请求码，服务端根据这个码分发请求
 * 1. 1000 - 1999 为基础服务码，包含与服务器建立连接，心跳，未知等
 * 2. 2000 - 2999 之间为与用户相关的操作
 * 3. 3000 - 3999 之间为与角色场景相关
 * 4. 4000 - 4999 之间为技能相关
 */
public enum Command {
    /** 心跳 **/
    HERATBEAT("heartbeat",1000,"心跳"),
    /** 断线重连 */
    RECONNECTION("Reconnection",1100,"断线重连 参数为userId 例如：Reconnection 1"),
    /** 未知的命令 */
    UNKNOWN("unknown", 1999,"未知的命令"),
    /** 创建用户 */
    USER_CREATE("user_create",2000,"创建用户，参数为用户昵称、密码、手机号，会返回登陆的唯一id, 例： user_create 玩家66 123 1501545"),
    /** 玩家登陆, 参数为 用户账号和密码，例： login 2 12345*/
    USER_LOGIN("login",2100,"玩家登陆, 参数为 用户账号和密码，例： login 2 12345"),
    /** 查看当前账号下的所有已创建角色 */
    SEE_MY_PLAYER("see_my_player",2200,"查看当前账号下所有已创建的的角色信息"),
    /** 退出登录 */
    USER_LOGOUT("logout",2400,"玩家退出登录"),
    /** 查看所有角色类型 */
    SEE_ROLE_TYPE("see_role_type",2510,"查看系统支持的所有角色类型"),
    /** 创建角色*/
    PLAYER_CREATE("role_create",2500,"创建角色,参数为角色名 所属角色类型 例：role_create  战不息 1"),
     /** 角色登陆 ，参数为 当前用户下的角色id，例： 2001 1313*/
    PLAYER_LOGIN("load",2600," 角色登陆 ，参数为 当前用户下的角色id，例： 2001 1313"),
     /** 玩家退出，无参数， 例： exit*/
    PLAYER_EXIT("exit",2900,"玩家退出，无参数， 例： exit"),
    /** 角色从当前位置可以进行的移动 例： move **/
    CAM_MOVE("can_move",2710,"可以移动到的地方，参数 无"),
    /** 角色移动， 参数 场景id ， 例： move 2 **/
    MOVE("move",2700,"角色移动， 参数 场景id ， 例： move 2"),
    /** AOI, 显示场景内各种游戏对象 **/
    AOI("aoi",3000,"AOI, 显示场景内各种游戏对象"),
    /** 与npc谈话 **/
    TALK_WITH_NPC("talk",3100,"与npc谈话"),
    /** 查看当前角色的技能状况 */
    SEE_PLAYER_SKILL("see_player_skill",4000,"查看当前角色的技能状况"),
    /** 对己方使用技能 **/
    SKILL_TO_SELF("skill_to_self",4100,"对己方使用技能 skill_to_self 技能id 例如：skill_to_self 1"),
    /** 使用技能攻击怪物 **/
    SKILL_TO_MONSTER("skill_to_monster",4200),
    /** 技能攻击玩家 **/
    SKILL_TO_PVP("skill_to_PVP",4300,"技能攻击玩家"),
    /** 查看背包当前情况 */
    SEE_PLAYER_BAG("see_player_bag",4001,"查看背包当前情况"),
    /**
     * 买东西
     * 穿装备
     * 卸装备
     * 修理装备
     * 出售装备
     */
    /** buffer开始 **/
    START_BUFFER("start_buffer",3004,"buffer开始")
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
