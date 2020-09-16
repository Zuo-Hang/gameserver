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
 * 5. 5000 - 5999 之间为和装备有关
 * 6. 6000 - 6999 之间为和组队有关
 * 7. 7000 - 7499 之间为和副本有关
 * 8. 7500 - 7999 之间为和游戏中的通讯有关
 * 9. 8000 - 8499 之间为和公会有关
 * 10.8500 - 9000 之间为和交易有关
 */
public enum Command {
    /**
     * 心跳
     **/
    HERATBEAT("heartbeat", 1000, "心跳"),
    /**
     * 断线重连
     */
    RECONNECTION("Reconnection", 1100, "断线重连 参数为userId 例如：Reconnection 1"),
    /**
     * 未知的命令
     */
    UNKNOWN("unknown", 1999, "未知的命令"),


    /**
     * 创建用户
     */
    USER_CREATE("user_create", 2000, "创建用户，参数为用户昵称、密码、手机号，会返回登陆的唯一id, 例： user_create 玩家66 123 1501545"),
    /**
     * 玩家登陆, 参数为 用户账号和密码，例： login 2 12345
     */
    USER_LOGIN("login", 2100, "玩家登陆, 参数为 用户账号和密码，例： login 2 12345"),
    /**
     * 查看当前账号下的所有已创建角色
     */
    SEE_MY_PLAYER("see_my_player", 2200, "查看当前账号下所有已创建的的角色信息"),
    /**
     * 退出登录
     */
    USER_LOGOUT("logout", 2400, "玩家退出登录"),
    /**
     * 查看所有角色类型
     */
    SEE_ROLE_TYPE("see_role_type", 2510, "查看系统支持的所有角色类型"),
    /**
     * 创建角色
     */
    PLAYER_CREATE("role_create", 2500, "创建角色,参数为角色名 所属角色类型 例：role_create  战不息 1"),
    /**
     * 角色登陆 ，参数为 当前用户下的角色id，例： 2001 1313
     */
    PLAYER_LOGIN("load", 2600, " 角色登陆 ，参数为 当前用户下的角色id，例： 2001 1313"),
    /**
     * 玩家退出，无参数， 例： exit
     */
    PLAYER_EXIT("exit", 2900, "玩家退出，无参数， 例： exit"),


    /**
     * 角色从当前位置可以进行的移动 例： move
     **/
    CAM_MOVE("can_move", 2710, "可以移动到的地方，参数 无"),
    /**
     * 角色移动， 参数 场景id ， 例： move 2
     **/
    MOVE("move", 2700, "角色移动， 参数 场景id ， 例： move 2"),
    /**
     * AOI, 显示场景内各种游戏对象
     **/
    AOI("aoi", 3000, "AOI, 显示场景内各种游戏对象"),
    /**
     * 与npc谈话
     **/
    TALK_WITH_NPC("talk", 3100, "与npc谈话"),


    /**
     * 查看当前角色的技能状况
     */
    SEE_PLAYER_SKILL("see_player_skill", 4000, "查看当前角色的技能状况"),
    /**
     * 对己方使用技能
     **/
    SKILL_TO_SELF("skill_to_self", 4100, "对己方使用技能 skill_to_self 技能id 例如：skill_to_self 1"),
    /**
     * 使用技能攻击怪物
     **/
    SKILL_TO_MONSTER("skill_to_monster", 4200),
    /**
     * 技能攻击玩家
     **/
    SKILL_TO_PVP("skill_to_pvp", 4300, "技能攻击玩家"),
    /**
     * 使用群体攻击技能
     */
    SKILL_TO_GROUP("skill_to_group",4400,"使用群体攻击技能"),
    /**
     * 使用召唤技能
     */
    SKILL_CALL("skill_call",4500,"使用召唤技能"),

    /**
     * 查看背包当前情况
     */
    SEE_PLAYER_BAG("see_player_bag", 4001, "查看背包当前情况"),
    /**
     * 查看当前装备栏情况
     */
    SEE_PLAYER_EQU("see_player_equ", 4002, "查看当前装备栏情况"),
    /**
     * 查看角色属性
     */
    SEE_PLAYER_ABILITY("see_player_ability", 4003, "查看角色属性"),


    /**
     * 展示商店 show_shop 5000
     */
    SHOW_SHOP("show_shop", 5000, "展示商店中可以购买的物品"),
    /**
     * 查看装备详情信息 see_tools_info 5100
     */
    SEE_TOOLS_INFO("see_tools_info", 5100, "查看某件装备的详细信息"),
    /**
     * 买东西 buy_tools 5200
     */
    BUY_TOOLS("buy_tools", 5200, "购买某件物品 buy_tools 物品id 例子：buy_tools 1"),
    /**
     * 穿装备 wear_tools 5300
     */
    WEAR_TOOLS("wear_tools", 5300, "穿戴背包中的某件装备 wear_tools 装备id"),
    /**
     * 卸装备 take_off_tools 5400
     */
    TAKE_OFF_TOOLS("take_off_tools", 5400, "卸装备 take_off_tools 装备id"),
    /**
     * 更换装备 replace_tools 5500
     */
    REPLACE_TOOLS("replace_tools", 5500, "更换装备 replace_tools 原装备id 新装备id"),
    /**
     * 修理装备 fix_tools 5600
     */
    FIX_TOOLS("fix_tools", 5600, "修理某件装备 fix_tools 装备id"),
    /**
     * 出售装备 sell_tools 5700
     */
    SELL_TOOLS("sell_tools", 5700, "出售不再需要的装备 sell_tools 装备id"),
    /**
     * 使用药品
     */
    USE_MEDICINE("use_medicine",5800,"使用药品 use_medicine 药品uuid"),


    /**
     * buffer开始
     **/
    START_BUFFER("start_buffer", 3004, "buffer开始"),


    /**
     * 邀请组队 publishTeamRequest
     **/
    PUBLISH_TEAM_REQUEST("publish_team_request", 6000, "邀请组队"),
    /**
     * 加入队伍 accept Team request
     **/
    ACCEPT_TEAM_REQUEST("accept", 6010, "接受组队请求->加入队伍  accept 请求id"),
    /**
     * 离开队伍
     **/
    LEAVE_TEAM("leave", 6020, "离开队伍"),
    /**
     * 查看队伍
     **/
    TEAM_SHOW("team", 6030, "查看队伍"),
    /**
     * 创建队伍，开房间。默认自己是房主
     */
    CREAT_TEAM("creat_team", 6040, "创建队伍，开房间。默认自己是房主"),

    KICK_FROM_TEAM("kick", 6050, "将某一成员踢出自己的队伍  tick playerId"),
    /**
     * 队长令队伍进入副本
     */
    TEAM_ENTER_GAME_COPY("team_enter_copy", 6060, "队长令队伍进入副本"),
    /**
     * 独自进入副本
     */
    ENTER_GAME_COPY("enter_copy", 7000, "独自进入副本"),
    /**
     * 退出副本
     */
    EXIT_GAME_COPY("exit_copy", 7010, "退出副本"),
    /**
     * 查看副本
     */
    SHOW_GAME_COPY("show_copy", 7020, "查看副本"),

    /**
     * 私聊
     **/
    WHISPER("whisper", 7610, "私聊"),

    /**
     * 公共聊天
     **/
    PUBLIC_CHAT("public_chat", 7620, "公共聊天"),

    /**
     * 发送邮件
     **/
    SEND_MAIL("send_mail", 7710, "发送邮件"),

    /**
     * 邮件列表
     **/
    MAIL_LIST("mail_list", 7720, "邮件列表"),

    /**
     * 接收邮件
     **/
    GET_MAIL("get_mail", 7730, "接收邮件"),
    /**
     * 删除邮件
     */
    DELETE_MAIL("delete_mail", 7740, "删除邮件"),

    /**
     *创建公会
     */
    GUILD_CREATE("guild_create",8000,"创建公会 guild_create 返回guildId  "),

    /**
     *查看公会
     */
    GUILD_SHOW("guild_show",8010,"查看公会 guild_show"),

    // 公会捐献
    GUILD_DONATE("guild_donate",8020,"公会捐献 guild_donate 背包的index（格）"),
    /**
     * 公会捐献金币
     */
    GUILD_DONATE_COLD("guild_donate_cold",8110,"公会捐献金币 guild_donate_cold 金币数量"),

    // 加入公会
    GUILD_JOIN("guild_join",8030,"加入公会 guild_join 返回guildId"),

    // 授权公会成员
    GUILD_GRANT("guild_grant",8040,"授权公会成员 guild_grant playerId 等级id"),

    // 获取公会物品
    GUILD_TAKE("guild_take",8050,"获取公会物品  guild_take toolsId"),
    /**
     * 公会获取金币
     */
    GUILD_TAKE_COLD("GUILD_TAKE_COLD",8120,"从公会取出金币,但是每次只能获取100金币"),

    // 允许入会
    GUILD_PERMIT("guild_permit", 8060,"允许入会 guild_permit playerId"),

    // 退出公会
    GUILD_QUIT("guild_quit", 8070,"退出公会"),
    /**
     * 从公会踢出会员
     */
    GUILD_KICK("guild_kick",8080,"从公会踢出会员"),
    /**
     * 解散公会
     */
    GUILD_DISMISS("guild_dismiss",8090,"解散公会"),
    /**
     * 查看加入请求
     */
    GUILD_SHOW_REQ("guild_show_req",8100,"查看加入请求"),

    /** 交易的流程为：卖家发起交易，买家开始交易（同意交易）、卖家放入货物、卖家放入金币、卖家确认交易 */
    /** 发起交易 **/
    TRADE_INITIATE("trade",8500,"发起交易"),

    /** 开始交易 **/
    TRADE_BEGIN("trade_begin",8510,"开始交易"),

    /** 交易货物 **/
    TRADE_TOOLS("trade_tools",8520,"交易货物"),

    /** 交易金币 **/
    TRADE_MONEY("trade_money",8530,"交易金币"),

    /** 查看交易出价 */
    TRADE_SEE("trade_see",8535,"查看交易出价  trade_see tradeId"),

    /** 确定交易 **/
    TRADE_CONFIRM("trade_confirm",8540,"确定交易"),

    /** 竞拍 */
    AUCTION_BID("auction_bid",8800,"竞拍"),

    /** 发布拍卖品 */
    AUCTION_PUSH("auction_push",8810,"发布拍卖品"),

    /** 查看拍卖品 */
    AUCTION_SHOW("auction_show",8820,"查看拍卖品"),


    ;

    private String command;

    private Integer requestCode;

    /**
     * 说明
     **/
    private String explain;

    private static final Map<String, Command> COMMAND_MAP = new HashMap<>();
    private static final Map<Integer, Command> ID_MAP = new HashMap<>();

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
            COMMAND_MAP.put(e.command, e);
            ID_MAP.put(e.requestCode, e);
        }
    }

    /**
     * 通过字符串命令查找命令枚举，如果找不到，返回一个默认的枚举对象
     *
     * @param command      字符串命令
     * @param defaultValue 默认命令枚举
     * @return 一个相关服务的枚举
     */
    public static Command findByCommand(String command, Command defaultValue) {
        Command value = COMMAND_MAP.get(command);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public static Command find(int requestCode, Command defaultValue) {
        Command value = ID_MAP.get(requestCode);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public Integer getRequestCode() {
        return requestCode;
    }

    public String getExplain() {
        return explain;
    }
}
