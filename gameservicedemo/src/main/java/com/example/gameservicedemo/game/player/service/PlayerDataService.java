package com.example.gameservicedemo.game.player.service;

import com.example.commondemo.base.RequestCode;
import com.example.gameservicedemo.game.bag.bean.BagBeCache;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.base.bean.Creature;
import com.example.gameservicedemo.game.skill.bean.Skill;
import com.example.gameservicedemo.game.tools.bean.Tools;
import com.example.gameservicedemo.game.tools.cache.ToolsPropertyInfoCache;
import com.example.gameservicedemo.manager.NotificationManager;
import com.example.gameservicedemo.manager.TimedTaskManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/25/12:18
 * @Description: 查看角色化身的一些数据，
 */
@Service
public class PlayerDataService {
    @Autowired
    PlayerLoginService playerLoginService;
    @Autowired
    RoleTypeService roleTypeService;
    @Autowired
    NotificationManager notificationManager;
    /**
     * 查看当前角色的技能状况
     *
     * @param context
     */
    public void seePlayerSkill(ChannelHandlerContext context) {
        PlayerBeCache player = playerLoginService.getPlayerByContext(context);
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
        BagBeCache bagBeCache = playerLoginService.getPlayerByContext(context).getBagBeCache();
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
     * 查看角色装备栏（主要查看当前装备的耐久度，是否需要更换或者修理该装备）
     * @param context
     */
    public void seePlayerEquipmentBar(ChannelHandlerContext context){
        //判断是否登录
        if(!playerLoginService.isLoad(context)){return;}
        PlayerBeCache playerByContext = playerLoginService.getPlayerByContext(context);
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
        if(!playerLoginService.isLoad(context)){return;}
        PlayerBeCache playerByContext = playerLoginService.getPlayerByContext(context);
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
