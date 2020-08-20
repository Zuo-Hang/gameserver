package com.example.gameservicedemo.service;

import com.example.commondemo.base.RequestCode;
import com.example.gamedatademo.bean.Bag;
import com.example.gamedatademo.bean.Player;
import com.example.gamedatademo.mapper.BagMapper;
import com.example.gamedatademo.mapper.PlayerMapper;
import com.example.gameservicedemo.bean.*;
import com.example.gameservicedemo.bean.scene.Scene;
import com.example.gameservicedemo.bean.shop.Tools;
import com.example.gameservicedemo.bean.shop.ToolsProperty;
import com.example.gameservicedemo.cache.PlayerCache;
import com.example.gameservicedemo.bean.scene.NPC;
import com.example.gameservicedemo.cache.ToolsPropertyInfoCache;
import com.example.gameservicedemo.manager.NotificationManager;
import com.example.gameservicedemo.manager.TimedTaskManager;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/05/16:46
 * @Description:
 */
@Slf4j
@Service
public class PlayerService {
    @Autowired
    PlayerMapper playerMapper;
    @Autowired
    UserService userService;
    @Autowired
    PlayerCache playerCache;
    @Autowired
    BagMapper bagMapper;
    @Autowired
    SceneService sceneService;
    @Autowired
    ToolsService toolsService;
    @Autowired
    RoleTypeService roleTypeService;
    @Autowired
    NotificationManager notificationManager;

    /**
     * 创建玩家
     * -------------------------------------------------同时创建相关联的背包
     *
     * @param context
     * @param playerName
     */
    public void playerCreat(ChannelHandlerContext context, String playerName, Integer roleClass) {
        UserBeCache userByCtx = userService.getUserByCxt(context);
        int userId = userByCtx.getUserId();
        Player player = new Player();
        player.setPlayerName(playerName);
        player.setUserId(userId);
        player.setNowAt(1);
        player.setExp(0);
        player.setMoney(0);
        player.setState(1);
        player.setRoleClass(roleClass);
        Integer insert = playerMapper.insert(player);
        log.info("成功创建角色{}", player.toString());
        notificationManager.notifyByCtx(context, "你已成功创建角色：" + playerName + "，快使用 load 命令去登录吧", RequestCode.SUCCESS.getCode());
    }

    /**
     * 玩家登录   判断触发登录的原因，做出相应的处理，包含缓存处理和给用户返回的信息处理
     *
     * @param context
     * @param playerId
     */
    public void playerLogin(ChannelHandlerContext context, Integer playerId) {
        StringBuilder result = new StringBuilder();
        //获取对应上下文的缓存角色
        Player playerByCtx = playerCache.getPlayerByCtx(context);
        //如果当前化身为待登录化身角色
        if (playerByCtx != null && playerByCtx.getPlayerId().equals(playerId)) {
            notificationManager.notifyByCtx(context, "此操作无效", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //如果为切换化身，就退出之前化身
        if (playerByCtx != null) {
            logoutScene(context);
        }
        //从数据库调出待登录化身的信息，登录
        Player player1 = playerMapper.selectByPlayerId(playerId);
        PlayerBeCache playerBeCache = new PlayerBeCache();
        BeanUtils.copyProperties(player1, playerBeCache);
        // 以channel id 为键储存玩家数据
        initPlayerInformation(playerBeCache);
        /** 需要初始化玩家信息*/
        playerCache.putCtxPlayer(context, playerBeCache);
        // 保存playerId跟ChannelHandlerContext之间的关系
        playerCache.savePlayerCtx(playerId, context);
        playerBeCache.setContext(context);
        //将玩家加入场景缓存当中
        Map<Integer, PlayerBeCache> players = sceneService.getScene(player1.getNowAt()).getPlayers();
        players.put(player1.getPlayerId(), playerBeCache);
        result.append(playerBeCache.getPlayerName()).append(",角色登陆成功")
                .append("\n 你所在位置为: ")
                .append(playerBeCache.getNowAt()).append("\n");
        result.append("使用指令 `aoi` 可查看周围环境");
        notificationManager.notifyByCtx(context, result.toString(), RequestCode.ABOUT_PLAYER.getCode());
    }

    /**
     * 在化身加入缓存之前对其进行初始化
     * 对于背包初始化
     * 初始化影响列表：
     *
     * @param playerBeCache
     */
    public void initPlayerInformation(PlayerBeCache playerBeCache) {
        //获取角色类型
        Integer roleTypeId = playerBeCache.getRoleClass();
        RoleType roleTypeById = roleTypeService.getRoleTypeById(roleTypeId);
        //将要放进缓存中的player 根据角色类型将 mp hp 初始化
        playerBeCache.setHp(roleTypeById.getBaseHp());
        playerBeCache.setMaxHp(roleTypeById.getBaseHp());
        playerBeCache.setMp(roleTypeById.getBaseMp());
        playerBeCache.setMaxMp(roleTypeById.getBaseMp());
        //加载增益属性---------------------------------------------------------------------------------------------
        Map<Integer, ToolsProperty> toolsInfluence1 = playerBeCache.getToolsInfluence();
        String attribute = roleTypeById.getGainAttribute();
        Gson gson1 = new Gson();
        ArrayList<ToolsProperty> ToolsPropertylist = (ArrayList<ToolsProperty>) gson1.fromJson(attribute, new TypeToken<ArrayList<ToolsProperty>>() {}.getType());
        if(!Objects.isNull(ToolsPropertylist)){
            ToolsPropertylist.forEach(v->{
                toolsInfluence1.put(v.getId(),v);
            });
        }
        //-------------------------------------------------------------------------------------------------------
        //初始化背包
        Bag bag = bagMapper.selectByBagId(playerBeCache.getBagId());
        BagBeCache bagBeCache = new BagBeCache();
        bagBeCache.setPlayerId(playerBeCache.getPlayerId());
        BeanUtils.copyProperties(bag,bagBeCache);
        //获取背包中的物品信息并转化为对象
        String json = bagBeCache.getItems();
        Gson gson = new Gson();
        ArrayList<Tools> toolslist = (ArrayList<Tools>) gson.fromJson(json, new TypeToken<ArrayList<Tools>>() {}.getType());
        if(!Objects.isNull(toolslist)){
            toolslist.forEach(v->{
                //放入被缓存的背包中
                bagBeCache.getToolsMap().put(v.getId(),v);
            });
        }
        playerBeCache.setBagBeCache(bagBeCache);
        log.info("角色：{} 的背包初始化完毕！",playerBeCache.getName());
    }

    /**
     * 判断对应上下文中的用户是否拥有这个角色
     *
     * @param ctx      上下文
     * @param playerId 要判定的角色id
     * @return 用户是否拥有此角色
     */
    public boolean hasPlayer(ChannelHandlerContext ctx, Integer playerId) {
        UserBeCache user = userService.getUserByCxt(ctx);
        List<Player> players = userService.findPlayers(ctx, user.getUserId());
        for (Player player : players) {
            if (player.getPlayerId().equals(playerId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 当前玩家退出场景
     *
     * @param context
     */
    public void logoutScene(ChannelHandlerContext context) {
        //获取到当前化身
        PlayerBeCache playerByCtx = playerCache.getPlayerByCtx(context);
        //null处理
        Optional.ofNullable(playerByCtx).ifPresent(
                p -> {
                    Integer nowAt = playerByCtx.getNowAt();
                    notificationManager.notifyScene(nowAt,
                            MessageFormat.format("玩家 {0} 正在退出", playerByCtx.getPlayerName())
                            , RequestCode.SUCCESS.getCode());
                    // 重点，从缓存中移除（缓存持久化、缓存删除）
                    Player player = new Player();
                    player.setUserId(playerByCtx.getUserId());
                    player.setNowAt(playerByCtx.getNowAt());
                    player.setPlayerName(playerByCtx.getPlayerName());
                    player.setPlayerId(playerByCtx.getPlayerId());
                    //更新数据库
                    playerMapper.updateByPlayerId(player);
                    //清除缓存
                    playerCache.removePlayerByChannelId(context.channel().id().asLongText());
                    playerCache.removePlayerCxt(playerByCtx.getPlayerId());
                    //从场景缓存中移除
                    sceneService.getScene(player.getNowAt()).getPlayers().remove(player.getPlayerId());
                }
        );

    }

    /**
     * 获取周边环境(展示同一场景内的NPC)
     *
     * @param context
     */
    public void aoi(ChannelHandlerContext context) {
        //获取当前场景的所有实体信息
        PlayerBeCache playerByCtx = playerCache.getPlayerByCtx(context);
        Integer nowAt = playerByCtx.getNowAt();
        Map<Integer, NPC> npcs = sceneService.getScene(nowAt).getNpcs();
        Collection<NPC> values = npcs.values();
        if (values == null) {
            notificationManager.notifyByCtx(context, "这里空无一人！", RequestCode.SUCCESS.getCode());
            return;
        }
        StringBuffer ret = new StringBuffer();
        ret.append("\nnpc如下：\n");
        if (values != null) {
            for (NPC objectsId : values) {
                ret.append(objectsId.displayData() + "\n");
            }
        }
        notificationManager.notifyByCtx(context, ret.toString(), RequestCode.SUCCESS.getCode());
    }

    /**
     * 通过context获取角色
     *
     * @param context
     * @return
     */
    public PlayerBeCache getPlayerByContext(ChannelHandlerContext context) {
        return playerCache.getPlayerByCtx(context);
    }

    /**
     * 查看当前角色的技能状况
     *
     * @param context
     */
    public void seePlayerSkill(ChannelHandlerContext context) {
        PlayerBeCache player = getPlayerByContext(context);
        //获取对应类型的所有技能
        Map<Integer, Skill> skillMap = roleTypeService.getRoleTypeById(player.getRoleClass()).getSkillMap();
        //显示可用的和不可用的技能
        Map<Integer, Skill> hasUseSkillMap = player.getHasUseSkillMap();
        StringBuilder skillCanUse = new StringBuilder("可以使用的技能有：\n");
        StringBuilder skillInCD = new StringBuilder("正在CD的技能有：\n");
        for (Skill skill : skillMap.values()) {
            if (Objects.isNull(hasUseSkillMap.get(skill.getId()))) {
                //处于CD的集合中没有这个技能代表可用
                skillCanUse.append(MessageFormat.format("技能id：{0} 技能名称：{1}\n", skill.getId(), skill.getName()));
                if (!Objects.isNull(skill.getDescribe())) {
                    skillCanUse.append("技能描述：" + skill.getDescribe() + "\n");
                }
            } else {
                //这才是缓存中的技能
                Skill skill1 = hasUseSkillMap.get(skill.getId());
                //技能正处于CD当中
                String format = MessageFormat.format("技能id：{0} 技能名称：{1} 等级：{2} 耗蓝:{3} cd:{4}  冷却完成时间还剩:{5}秒 \n",
                        skill1.getId(), skill1.getName(), skill1.getLevel(), skill1.getMpConsumption(), skill1.getCd(),
                        (skill1.getCd() - (System.currentTimeMillis() - skill1.getActiveTime())) * 0.001);
                skillInCD.append(format);
            }
        }
        notificationManager.notifyByCtx(context, skillCanUse.toString() + skillInCD.toString(), RequestCode.SUCCESS.getCode());
    }

    /**
     * 检测玩家是否死亡，若死亡进行复活操作
     *
     * @param playerBeCache
     * @param murderer
     * @return
     */
    public boolean isPlayerDead(PlayerBeCache playerBeCache, Creature murderer) {
        if (playerBeCache.getHp() < 0) {
            playerBeCache.setHp(0);
            playerBeCache.setState(-1);
            //广播通知玩家死亡
            //从场景中移除
            //开启复活操作
            TimedTaskManager.schedule(10000, () -> {
                playerBeCache.setState(1);
                //初始化玩家
                //initPlayer(casualty);
                notificationManager.notifyPlayer(playerBeCache, playerBeCache.getName() + "  你已经复活 \n", RequestCode.SUCCESS.getCode());
            });
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查看背包
     * @param context
     */
    public void seePlayerBag(ChannelHandlerContext context) {
        BagBeCache bagBeCache = getPlayerByContext(context).getBagBeCache();
        StringBuilder stringBuilder = new StringBuilder(MessageFormat.format("这个背包容量为：{0}，当前可用位置：{1},背包中有：\n",
                bagBeCache.getSize(),bagBeCache.getSize()-bagBeCache.getToolsMap().size()));
        Map<Integer, Tools> toolsMap = bagBeCache.getToolsMap();
        if(!Objects.isNull(toolsMap)){
            toolsMap.values().forEach(v->{
                stringBuilder.append(MessageFormat.format("物品id:{0} 名称：{1} 数量：{2}",v.getId(),v.getName(),v.getCount()));
                if(v.getType()<4){
                    stringBuilder.append("当前耐久度："+v.getDurability());
                }
                stringBuilder.append("\n");
            });
        }else{
            stringBuilder.append("哦，空空如也！");
        }
        notificationManager.notifyByCtx(context,stringBuilder.toString(),RequestCode.ABOUT_BAG.getCode());
    }

    /**
     * 购买装备
     * 钱（判断、更改）
     * 从背包中获取当前物品（有无）
     * 判断物品是否可以叠加
     * 有且不可叠加->不可购买
     *     ->可叠加并且已叠加数量可叠加最大数量则叠加
     * 无->判断背包是否还有位置
     * 放入背包
     * @param context
     * @param toolsId
     */
    public void buyTools(ChannelHandlerContext context,Integer toolsId){
        //判断是否登录
        if(!isLoad(context)){return;}
        PlayerBeCache playerByContext = getPlayerByContext(context);
        //判断金币是否足够
        if(playerByContext.getMoney()<toolsService.getToolsById(toolsId).getPriceIn()){
            notificationManager.notifyByCtx(context,"你的金币还不足以购买此商品",RequestCode.WARNING.getCode());
            return;
        }
        //判断背包中是否存在此类型道具
        BagBeCache bagBeCache = playerByContext.getBagBeCache();
        Tools tools = bagBeCache.getToolsMap().get(toolsId);
        if(!Objects.isNull(tools)){
            if(tools.getCount().equals(tools.getRepeat())){
                notificationManager.notifyByCtx(context,"此装备你已叠加到最大值，不可在购买",RequestCode.WARNING.getCode());
                return;
            }
            //以叠加的方式放入背包
            tools.setCount(tools.getCount()+1);

        }else{
            //判断背包中是否有足够的足够的位置存放
            if(bagBeCache.getToolsMap().size()>=bagBeCache.getSize()){
                notificationManager.notifyByCtx(context,"你的背包已满，要购买背包中不存在类型的装备必须卖掉某些装备",RequestCode.WARNING.getCode());
                return;
            }
            //创建一个新的道具放入背包，因为后期可能改变这个道具的某些属性，所以不能使用缓存中的道具对象
            Tools newTools = new Tools();
            BeanUtils.copyProperties(toolsService.getToolsById(toolsId),newTools);
            newTools.setCount(1);
            bagBeCache.getToolsMap().put(newTools.getId(),newTools);
        }
        //扣除金币
        playerByContext.setMoney(playerByContext.getMoney()-toolsService.getToolsById(toolsId).getPriceIn());
        notificationManager.notifyByCtx(context,MessageFormat.format("你已成功购买了道具{0}，新道具已经放入你的背包了，你可以使用\"see_player_bag\"查看",toolsService.getToolsById(toolsId).getName()),RequestCode.SUCCESS.getCode());
    }

    /**
     * 判断当前会话是否有角色加载
     * @param context
     * @return
     */
    public boolean isLoad(ChannelHandlerContext context){
        PlayerBeCache playerByContext = getPlayerByContext(context);
        if(Objects.isNull(playerByContext)){
            notificationManager.notifyByCtx(context,"你还未登录，请使用\"load\"登录角色",RequestCode.BAD_REQUEST.getCode());
            return false;
        }
        return true;
    }
    /**
     * 查看角色装备栏（主要查看当前装备的耐久度，是否需要更换或者修理该装备）
     * @param context
     */
    public void seePlayerEquipmentBar(ChannelHandlerContext context){
        //判断是否登录
        if(!isLoad(context)){return;}
        PlayerBeCache playerByContext = getPlayerByContext(context);
        Map<Integer, Tools> equipmentBar = playerByContext.getEquipmentBar();
        if(equipmentBar.values().size()==0){
            notificationManager.notifyByCtx(context,"你还没有装配任何装备！",RequestCode.SUCCESS.getCode());
            return;
        }
        StringBuilder stringBuilder = new StringBuilder("装备详情如下:\n");
        equipmentBar.values().forEach(v->{
            stringBuilder.append(MessageFormat.format("名称：{0} 当前耐久度：{1}\n",v.getName(),v.getDurability()));
            if(v.getDurability()==0){
                stringBuilder.append("当前耐久度过低，该装备处于无效状态，需要更换或修理！");
            }
        });
        notificationManager.notifyByCtx(context,stringBuilder.toString(),RequestCode.ABOUT_EQU.getCode());
    }

    /**
     * 查看玩家性能属性
     * 生命值&魔法值：最大  当前  恢复能力
     * 金币：
     * 其他基本值：
     * @param context
     */
    public void seePlayerAbility(ChannelHandlerContext context){
        if(!isLoad(context)){return;}
        PlayerBeCache playerByContext = getPlayerByContext(context);
        StringBuilder stringBuilder = new StringBuilder("角色基本属性如下：\n");

        stringBuilder.append(MessageFormat.format("最大生命值：{0}\n当前生命值：{1}\n最大魔法值：{2}\n当前魔法值：{3}\n金币：{4}\n",
                playerByContext.getMaxHp(),
                playerByContext.getHp(),
                playerByContext.getMaxMp(),
                playerByContext.getMp(),
                playerByContext.getMoney()));
        playerByContext.getToolsInfluence().values().forEach(v->{
            stringBuilder.append(MessageFormat.format("{0}:{1}\n",
                    ToolsPropertyInfoCache.toolsPropertyInfoCache.get(v.getId()),
                    v.getValue())
            );
        });
        notificationManager.notifyByCtx(context,stringBuilder.toString(),RequestCode.SUCCESS.getCode());
    }
}
