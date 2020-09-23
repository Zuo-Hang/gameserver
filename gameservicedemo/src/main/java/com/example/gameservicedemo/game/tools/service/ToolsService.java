package com.example.gameservicedemo.game.tools.service;

import com.example.commondemo.base.RequestCode;
import com.example.gameservicedemo.background.WriteBackDB;
import com.example.gameservicedemo.event.EventBus;
import com.example.gameservicedemo.event.model.EquipmentEvent;
import com.example.gameservicedemo.game.bag.bean.BagBeCache;
import com.example.gameservicedemo.game.bag.service.BagService;
import com.example.gameservicedemo.game.buffer.bean.Buffer;
import com.example.gameservicedemo.game.hurt.ChangePlayerInformationImp;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.buffer.service.BufferService;
import com.example.gameservicedemo.game.player.service.PlayerDataService;
import com.example.gameservicedemo.game.player.service.PlayerLoginService;
import com.example.gameservicedemo.game.shop.bean.Shop;
import com.example.gameservicedemo.game.tools.bean.Tools;
import com.example.gameservicedemo.game.tools.bean.ToolsProperty;
import com.example.gameservicedemo.game.skill.bean.Skill;
import com.example.gameservicedemo.game.tools.bean.ToolsType;
import com.example.gameservicedemo.game.tools.cache.ToolsCache;
import com.example.gameservicedemo.game.tools.cache.ToolsPropertyInfoCache;
import com.example.gameservicedemo.manager.NotificationManager;
import com.example.gameservicedemo.manager.TimedTaskManager;
import com.example.gameservicedemo.game.skill.service.SkillService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/17/21:51
 * @Description: 处理装备等物品的服务
 */
@Service
@Slf4j
public class ToolsService {
    @Autowired
    ToolsCache toolsCache;
    @Autowired
    NotificationManager notificationManager;
    @Autowired
    BufferService bufferService;
    @Autowired
    ChangePlayerInformationImp changePlayerInformationImp;
    @Autowired
    PlayerDataService playerDataService;
    @Autowired
    PlayerLoginService playerLoginService;
    @Autowired
    BagService bagService;
    @Autowired
    SkillService skillService;

    /**
     * 屏蔽数据与业务层
     *
     * @param toolsId
     * @return
     */
    public Tools getToolsById(Integer toolsId) {
        return toolsCache.getToolsById(toolsId);
    }

    /**
     * 初始化商店的货架
     *
     * @param shop
     */
    public void initShopGoodsMap(Shop shop) {
        String goods = shop.getGoods();
        if (Objects.isNull(goods)) {
            return;
        }
        String[] split = goods.split(",");
        for (String s : split) {
            //获取到商品
            Tools toolsById = getToolsById(Integer.valueOf(s));
            //放入货架
            shop.getGoodsMap().put(toolsById.getId(), toolsById);
        }
        log.info("商店：{} 的货架初始化完毕", shop.getShopName());
    }

    /**
     * 装配某件装备
     * 1.获取玩家的背包
     * 2.判断背包中是否有此装备，判断此物品是否是可穿戴的装备
     * 3.判断穿戴位置是否有空，无空则替换装备
     * 4.穿戴完毕后改变玩家增益属性
     *
     * @param player
     * @param tools
     * @return
     */
    public boolean wearTools(PlayerBeCache player, Tools tools) {
        BagBeCache bagBeCache = player.getBagBeCache();
        //需要对这件装备进行判断
        Integer type = tools.getType();
        if (type > 3) {//1~3是不同种类的装备
            notificationManager.notifyPlayer(player, "此物品并非可装配的整备", RequestCode.BAD_REQUEST.getCode());
            return false;
        }
        if (player.getEquipmentBar().size() >= 6) {
            notificationManager.notifyPlayer(player, "你的装备栏已满，使用指令\"\"替换装备", RequestCode.BAD_REQUEST.getCode());
            return false;
        }
        //将背包中的装备添加到装备栏，将背包中的装备移除
        player.getEquipmentBar().put(tools.getUuid(), tools);
        bagService.removeFromBag(player.getBagBeCache(),tools.getUuid());
        //计算装备对玩家属性的影响
        Map<Integer, ToolsProperty> toolsInfluence = player.getToolsInfluence();
        //性能影响
        List<ToolsProperty> toolsPropertie = tools.getToolsPropertie();
        if (!Objects.isNull(toolsPropertie)) {
            toolsPropertie.forEach(v -> {
                //安全问题-----------------------------------------------------------------------
                //将装备的影响叠加到用户的影响集合数值上
                int value = toolsInfluence.get(v.getId()).getValue() + v.getValue();
                toolsInfluence.get(v.getId()).setValue(value);
            });
        }

        //添加装备的唯一被动，如名刀、金身、复活甲等技能
        Integer skillId = tools.getPassiveSkills();
        if (Objects.nonNull(skillId)) {
            //判断之前是否装配过
            Skill hasUse = player.getHasUseSkillMap().get(skillId);
            if (Objects.isNull(hasUse)) {
                //之前没有添加过或者cd在移除前已好
                Skill skill = new Skill();
                BeanUtils.copyProperties(skillService.getSkillById(skillId), skill);
                //设置上次使用的时间
                skill.setActiveTime(System.currentTimeMillis() - skill.getCd());
                Skill put = player.getSkillHaveMap().put(skill.getId(), skill);
                if (put != null) {
                    log.info("{}添加了被动技能{}", player.getName(), skill.getName());
                }
            } else {
                //之前添加过，并且上次移除时cd并未冷却
                player.getHasUseSkillMap().remove(hasUse.getId());
                player.getSkillHaveMap().put(hasUse.getId(), hasUse);
            }
        }
        notificationManager.notifyPlayer(player, MessageFormat.format("穿戴装备：{0} 成功", tools.getName()), RequestCode.SUCCESS.getCode());
        //调用回显
        playerDataService.showPlayerEqu(player);
        playerDataService.showPlayerBag(player);
        playerDataService.showPlayerInfo(player);
        EventBus.publish(new EquipmentEvent(player,tools));
        return true;
    }

    /**
     * 卸载某个装备
     * 1.判断此装备是否被装配
     * 2.移除装备放入背包
     * 3.用户加成减去装备的影响值，去除装备带来的被动技能
     *
     * @param player
     * @param remove
     */
    public boolean takeOffTools(PlayerBeCache player, Tools remove) {
        List<ToolsProperty> toolsPropertie = remove.getToolsPropertie();
        //线程安全问题-----------------------------------------------------------------------------------------
        //移去被动技能------------------------------------------
        Integer skillId = remove.getPassiveSkills();
        if (Objects.nonNull(skillId)) {
            //在技能列表移除
            Skill skill = player.getSkillHaveMap().remove(skillId);
            //如果未冷却完成，则移到未冷却列表
            if (skill.getCd() > System.currentTimeMillis() - skill.getActiveTime()) {
                player.getHasUseSkillMap().put(skill.getId(), skill);
            }
        }
        //更新影响
        if (!Objects.isNull(toolsPropertie)) {
            toolsPropertie.forEach(v -> {
                int value = player.getToolsInfluence().get(v.getId()).getValue() - v.getValue();
                player.getToolsInfluence().get(v.getId()).setValue(value);
            });
        }
        //将卸下的装备放回背包
        //判断背包容量
        player.getEquipmentBar().remove(remove.getUuid());
        bagService.putInBag(player, remove);
        notificationManager.notifyPlayer(player, MessageFormat.format("脱下装备：{0} 成功", remove.getName()), RequestCode.SUCCESS.getCode());
        playerDataService.showPlayerBag(player);
        playerDataService.showPlayerInfo(player);
        playerDataService.showPlayerEqu(player);
        playerDataService.showSkill(player);
        return true;
    }

    /**
     * 更换装备
     * 先卸载，再装配
     *
     * @param player   玩家
     * @param toolsOut 需要卸下的装备
     * @param toolsIn  需要装配的装备
     * @return
     */
    public boolean replaceTools(PlayerBeCache player, Tools toolsOut, Tools toolsIn) {
        boolean b1 = takeOffTools(player, toolsOut);
        boolean b = wearTools(player, toolsIn);
        return true;
    }

    /**
     * 展示某一装备的详细信息
     * id,名称，唯一被动（触发的buf）,耐久度,是否可叠加,增益,购入价格,卖出价格,描述
     *
     * @param context
     * @param toolsId
     */
    public void showToolsInfo(ChannelHandlerContext context, Integer toolsId) {
        Tools toolsById = getToolsById(toolsId);
        if (Objects.isNull(toolsById)) {
            notificationManager.notifyByCtx(context, "不存在这个物品，请检查输入！", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        String s = " ";
        Integer passiveSkills = toolsById.getPassiveSkills();
        if (Objects.isNull(passiveSkills)) {
            s = "无被动";
        } else {
            Skill skillById = skillService.getSkillById(passiveSkills);
            s = skillById.getName() + "——" + skillById.getDescribe();
        }
        //增益
        StringBuilder add = new StringBuilder();
        List<ToolsProperty> toolsPropertie = toolsById.getToolsPropertie();
        if (!Objects.isNull(toolsPropertie)) {
            toolsPropertie.forEach(v -> {
                add.append(MessageFormat.format("{0}增加{1}点;", ToolsPropertyInfoCache.toolsPropertyInfoCache.get(v.getId()), v.getValue()));
            });
        }
        StringBuilder stringBuilder = new StringBuilder("物品的详细信息如下：\n");
        stringBuilder.append(MessageFormat.format("id:{0}\n名称:{1}\n唯一被动技能:{2}\n耐久度:{3}\n" +
                        "是否可叠加:{4}\n增益:{5}\n购入价格:{6}\n卖出价格:{7}\n描述信息:{8}\n",
                toolsById.getId(),
                toolsById.getName(),
                s,
                toolsById.getDurability(),
                toolsById.getRepeat() == 1 ? "不可叠加" : "可最大叠到" + toolsById.getRepeat(),
                add.toString(),
                toolsById.getPriceIn(),
                toolsById.getPriceOut(),
                toolsById.getDescribe()
        ));
        notificationManager.notifyByCtx(context, stringBuilder.toString(), RequestCode.SUCCESS.getCode());
    }

    /**
     * 修理装备，在这个期间不可以使用技能
     *
     * @param playerBeCache
     * @param toolsUuid
     */
    public void fixTools(PlayerBeCache playerBeCache, Long toolsUuid) {
        Tools toolsInBag = bagService.containsTools(playerBeCache.getBagBeCache(), toolsUuid);
        Tools toolsInEqu = playerBeCache.getEquipmentBar().get(toolsUuid);
        if (Objects.isNull(toolsInBag) && Objects.isNull(toolsInEqu)) {
            notificationManager.notifyPlayer(playerBeCache, "你还未拥有该装备", RequestCode.WARNING.getCode());
            return;
        }
        if (Objects.isNull(toolsInBag)) {
            toolsInBag = toolsInEqu;
        }
        //是否在待修理列表中
        if (!playerBeCache.getNeedFix().contains(toolsUuid)) {
            notificationManager.notifyPlayer(playerBeCache, "该装备目前不需要修理", RequestCode.WARNING.getCode());
            return;
        }
        //开启线程去修理---------------------------------------------------考虑线程安全问题
        notificationManager.notifyPlayer(playerBeCache, MessageFormat.format("将要修理装备：{0},需要{1}秒，在此期间你将不能使用任何技能！", toolsInBag.getName(), 15), RequestCode.WARNING.getCode());
        playerBeCache.setCanUseSkill(false);
        Tools finalToolsInBag = toolsInBag;
        TimedTaskManager.singleThreadSchedule(15 * 1000,
                () -> {
                    finalToolsInBag.setDurability(getToolsById(finalToolsInBag.getId()).getDurability());
                    playerBeCache.setCanUseSkill(true);
                    notificationManager.notifyPlayer(playerBeCache, MessageFormat.format("装备{0}修理完毕！", finalToolsInBag.getName()), RequestCode.SUCCESS.getCode());
                }
        );
    }

    /**
     * 将不需要装备出售
     *
     * @param player
     * @param toolsUuid
     */
    public void sellTools(PlayerBeCache player, Long toolsUuid) {
        //判断是否是正在装配的装备
        Tools tools = player.getEquipmentBar().get(toolsUuid);
        //是正在装配的装备->卸载装备,并提醒
        if (Objects.nonNull(tools)) {
            takeOffTools(player, tools);
            notificationManager.notifyPlayer(player, "由于该装备之前处于装备栏，请注意自己属性的变化值", RequestCode.WARNING.getCode());
            //从装备栏卸载
        }
        //是否是背包中的物品
        Tools toolsInBag = bagService.containsTools(player.getBagBeCache(), toolsUuid);
        if (Objects.isNull(toolsInBag)) {
            notificationManager.notifyPlayer(player, "你暂时还没有拥有这件物品哦", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        //在背包中移除物品-------------------是否有线程安全问题？
        bagService.removeFromBag(player.getBagBeCache(),toolsInBag.getUuid());
        //更新钱数
        changePlayerInformationImp.changePlayerMoney(player,toolsInBag.getPriceOut());
        notificationManager.notifyPlayer(player, MessageFormat.format("{0}出售成功，获得金{1}币",
                toolsInBag.getName(), toolsInBag.getPriceOut()), RequestCode.SUCCESS.getCode());
    }

    /**
     * 使用药品
     * @param player
     * @param toolsId
     */
    public void useMedicine(PlayerBeCache player, Long toolsId) {
        Tools tools = bagService.containsTools(player.getBagBeCache(), toolsId);
        if(Objects.isNull(tools)){
            notificationManager.notifyPlayer(player,"你的背包中没有这件物品，请检查输入的uuid",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        if(!tools.getType().equals(ToolsType.MEDICINE.getCode())){
            notificationManager.notifyPlayer(player,"这件物品并非药物！",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        Skill skillById;
        //红药
        if(tools.getId().equals(28)){
            skillById = skillService.getSkillById(33);
        }else{
            //蓝药
            skillById = skillService.getSkillById(34);
        }
        Buffer buffer = bufferService.getBuffer(skillById.getBuffer());
        bufferService.startBuffer(player,buffer);
        bagService.removeFromBag(player.getBagBeCache(),toolsId);
    }
}
