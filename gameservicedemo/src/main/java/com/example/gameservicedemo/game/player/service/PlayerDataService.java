package com.example.gameservicedemo.game.player.service;

import com.example.commondemo.base.RequestCode;
import com.example.commondemo.message.Message;
import com.example.gameservicedemo.game.bag.bean.BagBeCache;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.base.bean.Creature;
import com.example.gameservicedemo.game.player.cache.PlayerCache;
import com.example.gameservicedemo.game.scene.service.SceneService;
import com.example.gameservicedemo.game.skill.bean.Skill;
import com.example.gameservicedemo.game.skill.bean.SkillActiveAndPassiveType;
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
    SceneService sceneService;
    @Autowired
    PlayerCache playerCache;
    @Autowired
    NotificationManager notificationManager;
    /**
     * 查看当前角色的技能状况
     *
     * @param context
     */
    public void seePlayerSkill(ChannelHandlerContext context) {
        showSkill(playerLoginService.isLoad(context));
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
     * 展示玩家信息：血量 攻击力防御力……
     * @param playerByContext
     */
    public void showPlayerInfo(PlayerBeCache playerByContext){
        if (Objects.isNull(playerByContext)) {
            return;
        }
        String format = MessageFormat.format("角色名称：{0} 职业：{1}\n",
                playerByContext.getName(),
                roleTypeService.getRoleTypeById(playerByContext.getRoleClass()).getName());
        StringBuilder stringBuilder = new StringBuilder(format);
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
        notificationManager.notifyPlayer(playerByContext,stringBuilder.toString(),RequestCode.ABOUT_PLAYER.getCode());
    }

    /**
     * 更新玩家位置
     * @param playerBeCache
     */
    public void showPlayerPosition(PlayerBeCache playerBeCache){
        notificationManager.notifyPlayer(playerBeCache,
                sceneService.getScene(playerBeCache.getNowAt()).getName(),
                RequestCode.ABOUT_SCENE.getCode());
    }
    /**
     * 更新玩家背包显示
     * @param playerByContext
     */
    public void showPlayerBag(PlayerBeCache playerByContext){
        if (Objects.isNull(playerByContext)) {
            return;
        }
        BagBeCache bagBeCache = playerByContext.getBagBeCache();
        StringBuilder stringBuilder = new StringBuilder(MessageFormat.format("这个背包容量为：{0}，当前可用位置：{1},背包中有：\n",
                bagBeCache.getSize(),bagBeCache.getSize()-bagBeCache.getToolsMap().size()));
        Map<Integer, Tools> toolsMap = bagBeCache.getToolsMap();
        if(!Objects.isNull(toolsMap)){
            toolsMap.values().forEach(v->{
                stringBuilder.append(MessageFormat.format("物品id:{0} 名称：{1} 数量：{2} ",v.getId(),v.getName(),v.getCount()));
                if(v.getType()<4){
                    stringBuilder.append("当前耐久度："+v.getDurability());
                }
                stringBuilder.append("\n");
            });
        }else{
            stringBuilder.append("哦，空空如也！");
        }
        notificationManager.notifyPlayer(playerByContext,stringBuilder.toString(),RequestCode.ABOUT_BAG.getCode());
    }
    /**
     * 更新玩家装备栏显示
     * @param playerByContext
     */
    public void showPlayerEqu(PlayerBeCache playerByContext){
        if (Objects.isNull(playerByContext)) {
            return;
        }
        Map<Integer, Tools> equipmentBar = playerByContext.getEquipmentBar();
        if(equipmentBar.values().size()==0){
            notificationManager.notifyPlayer(playerByContext,"你还没有装配任何装备！",RequestCode.ABOUT_EQU.getCode());
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        equipmentBar.values().forEach(v->{
            stringBuilder.append(MessageFormat.format("id:{0} 名称：{1} 当前耐久度：{2}\n",v.getId(), v.getName(),v.getDurability()));
            if(v.getDurability()==0){
                stringBuilder.append("当前耐久度过低，该装备处于无效状态，需要更换或修理！");
            }
        });
        notificationManager.notifyPlayer(playerByContext,stringBuilder.toString(),RequestCode.ABOUT_EQU.getCode());
    }

    public void showSkill(PlayerBeCache player){
        if (Objects.isNull(player)) {return;}
        //获取化身的所有技能
        Map<Integer, Skill> skillHaveMap = player.getSkillHaveMap();
        //显示可用的和不可用的技能
        String s="------------------------------\n";
        StringBuilder skillCanUse = new StringBuilder("可以使用的技能有：\n");
        StringBuilder skillInCd = new StringBuilder("正在CD的技能有：\n");
        StringBuilder activeSkill = new StringBuilder("其中主动技能有：\n");
        StringBuilder passiveSkill = new StringBuilder("被动技能有：\n");
        skillHaveMap.values().forEach(skill->{
            if (System.currentTimeMillis()- skill.getActiveTime()<skill.getCd()) {//表明cd还未好
                String format = MessageFormat.format("id：{0} 名称：{1} 等级：{2} 耗蓝:{3} cd:{4}  冷却完成时间还剩:{5}秒 \n",
                        skill.getId(), skill.getName(), skill.getLevel(), skill.getMpConsumption(), skill.getCd(),
                        (skill.getCd() - (System.currentTimeMillis() - skill.getActiveTime()))/1000);
                skillInCd.append(format);

            } else {
                skillCanUse.append(MessageFormat.format("技能id：{0} 技能名称：{1} ", skill.getId(), skill.getName()));
                if (Objects.nonNull(skill.getDescribe())) {
                    skillCanUse.append("技能描述：" + skill.getDescribe());
                }
                skillCanUse.append("\n");
            }
            if(skill.getSkillActiveOrPassiveType().equals(SkillActiveAndPassiveType.ACTIVE.getCode())){
                activeSkill.append(MessageFormat.format("id:{0} {1}  ",skill.getId(),skill.getName()));
            }else{
                passiveSkill.append(MessageFormat.format("id:{0} {1}  ",skill.getId(),skill.getName()));
            }
        });
        notificationManager.notifyPlayer(player,
                skillCanUse.toString() +s+ skillInCd.toString()+s+activeSkill.toString()+"\n"+s+passiveSkill.toString(),
                RequestCode.ABOUT_SKILL.getCode());
    }
}
