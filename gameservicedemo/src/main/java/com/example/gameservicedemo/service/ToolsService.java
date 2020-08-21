package com.example.gameservicedemo.service;

import com.example.commondemo.base.RequestCode;
import com.example.gameservicedemo.bean.BagBeCache;
import com.example.gameservicedemo.bean.Buffer;
import com.example.gameservicedemo.bean.PlayerBeCache;
import com.example.gameservicedemo.bean.shop.Shop;
import com.example.gameservicedemo.bean.shop.Tools;
import com.example.gameservicedemo.bean.shop.ToolsProperty;
import com.example.gameservicedemo.cache.ToolsCache;
import com.example.gameservicedemo.cache.ToolsPropertyInfoCache;
import com.example.gameservicedemo.manager.NotificationManager;
import com.example.gameservicedemo.manager.TimedTaskManager;
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
     * @param shop
     */
    public void initShopGoodsMap(Shop shop){
        String goods = shop.getGoods();
        if(Objects.isNull(goods)){
            return;
        }
        String[] split = goods.split(",");
//        if()
        for (String s : split) {
            //获取到商品
            Tools toolsById = getToolsById(Integer.valueOf(s));
            //放入货架
            shop.getGoodsMap().put(toolsById.getId(),toolsById);
        }
        log.info("商店：{} 的货架初始化完毕",shop.getShopName());
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
        Tools tools1 = bagBeCache.getToolsMap().get(tools.getId());
        Integer type = tools.getType();
        if (type>3) {//1~3是不同种类的装备
            notificationManager.notifyPlayer(player, "此物品并非可装配的整备", RequestCode.BAD_REQUEST.getCode());
            return false;
        }
        if (Objects.isNull(tools1)) {
            notificationManager.notifyPlayer(player, "你的背包中还没有这件装备哦", RequestCode.BAD_REQUEST.getCode());
            return false;
        }
        if (player.getEquipmentBar().size() >= 6) {
            notificationManager.notifyPlayer(player, "你的装备栏已满，使用指令\"\"替换装备", RequestCode.BAD_REQUEST.getCode());
            return false;
        }
        //将背包中的装备添加到装备栏
        player.getEquipmentBar().put(tools1.getId(), tools1);
        //将背包中的装备移除
        bagBeCache.getToolsMap().remove(tools1.getId());
        //计算装备对玩家属性的影响
        Map<Integer, ToolsProperty> toolsInfluence = player.getToolsInfluence();
        //buf影响（装备的唯一被动，如名刀、金身、复活甲等）
        //----------------------------------------------------------------------------------------------------需细化
        Integer bufferId = tools.getBuffer();
        if (bufferId != null) {
            Buffer buffer1 = new Buffer();
            Buffer buffer = bufferService.getBuffer(bufferId);
            BeanUtils.copyProperties(buffer, buffer1);
            boolean add = player.getBufferList().add(buffer1);
            if (add) {
                log.info("{}添加了Buf{}", player.getName(), buffer1.getName());
            }
        }

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
        notificationManager.notifyPlayer(player,MessageFormat.format("穿戴装备：{0} 成功",tools1.getName()),RequestCode.SUCCESS.getCode());
        return true;
    }

    /**
     * 卸载某个装备
     * 1.判断此装备是否被装配
     * 2.移除装备放入背包
     * 3.用户加成减去装备的影响值，去除装备带来的buf
     *
     * @param player
     * @param tools
     */
    public boolean takeOffTools(PlayerBeCache player, Tools tools) {
        Tools tools1 = player.getEquipmentBar().get(tools.getId());
        if (Objects.isNull(tools1)) {
            notificationManager.notifyPlayer(player, "你的装备栏并没有这件装备哦", RequestCode.BAD_REQUEST.getCode());
            return false;
        }
        Tools remove = player.getEquipmentBar().remove(tools.getId());
        List<ToolsProperty> toolsPropertie = remove.getToolsPropertie();
        //线程安全问题-----------------------------------------------------------------------------------------
        //去除buf加成
        //更新影响
        if (!Objects.isNull(toolsPropertie)) {
            toolsPropertie.forEach(v -> {
                int value = player.getToolsInfluence().get(v.getId()).getValue() - v.getValue();
                player.getToolsInfluence().get(v.getId()).setValue(value);
            });
        }
        //将卸下的装备放回背包
        //判断背包容量
        player.getBagBeCache().getToolsMap().put(remove.getId(), remove);
        notificationManager.notifyPlayer(player,MessageFormat.format("脱下装备：{0} 成功",tools1.getName()),RequestCode.SUCCESS.getCode());
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
     * @param context
     * @param toolsId
     */
    public void showToolsInfo(ChannelHandlerContext context,Integer toolsId){
        Tools toolsById = getToolsById(toolsId);
        if(Objects.isNull(toolsById)){
            notificationManager.notifyByCtx(context,"不存在这个物品，请检查输入！",RequestCode.BAD_REQUEST.getCode());
            return;
        }
        String s;
        Integer bufferId = toolsById.getBuffer();
        if(Objects.isNull(bufferId)){
            s="无被动";
        }else {//需调用buf效果的查看
            s = bufferService.bufferInfo(bufferService.getBuffer(bufferId));
        }
        //增益
        StringBuilder add=new StringBuilder();
        List<ToolsProperty> toolsPropertie = toolsById.getToolsPropertie();
        if(!Objects.isNull(toolsPropertie)){
            toolsPropertie.forEach(v->{
                add.append(MessageFormat.format("{0}增加{1}点;", ToolsPropertyInfoCache.toolsPropertyInfoCache.get(v.getId()),v.getValue()));
            });
        }
        StringBuilder stringBuilder = new StringBuilder("物品的详细信息如下：\n");
        stringBuilder.append(MessageFormat.format("id:{0}\n名称:{1}\n唯一被动（触发的buf）:{2}\n耐久度:{3}\n" +
                        "是否可叠加:{4}\n增益:{5}\n购入价格:{6}\n卖出价格:{7}\n描述信息:{8}\n",
                toolsById.getId(),
                toolsById.getName(),
                s,
                toolsById.getDurability(),
                toolsById.getRepeat()==1?"不可叠加":"可最大叠到"+toolsById.getRepeat(),
                add.toString(),
                toolsById.getPriceIn(),
                toolsById.getPriceOut(),
                toolsById.getDescribe()
                ));
        notificationManager.notifyByCtx(context,stringBuilder.toString(),RequestCode.SUCCESS.getCode());
    }

    /**
     * 修理装备，在这个期间不可以使用技能
     * @param playerBeCache
     * @param toolsId
     */
    public void fixTools(PlayerBeCache playerBeCache,Integer toolsId){
        Tools toolsInBag;
        if(!Objects.isNull(playerBeCache.getBagBeCache().getToolsMap().get(toolsId))){
            toolsInBag=playerBeCache.getBagBeCache().getToolsMap().get(toolsId);
        }else if(!Objects.isNull(playerBeCache.getEquipmentBar().get(toolsId))){
            toolsInBag=playerBeCache.getEquipmentBar().get(toolsId);
        }else{
            notificationManager.notifyPlayer(playerBeCache,"你还未拥有该装备",RequestCode.WARNING.getCode());
            return;
        }
        //是否在待修理列表中
        if(!playerBeCache.getNeedFix().contains(toolsId)){
            notificationManager.notifyPlayer(playerBeCache,"该装备目前不需要修理",RequestCode.WARNING.getCode());
            return;
        }
        //开启线程去修理---------------------------------------------------考虑线程安全问题
        notificationManager.notifyPlayer(playerBeCache, MessageFormat.format("将要修理装备：{0},需要{1}秒，在此期间你将不能使用任何技能！",toolsInBag.getName(),15),RequestCode.WARNING.getCode());
        playerBeCache.setCanUseSkill(false);
        TimedTaskManager.singleThreadSchedule( 15*1000,
                ()->{
                    toolsInBag.setDurability(getToolsById(toolsId).getDurability());
                    playerBeCache.setCanUseSkill(true);
                    notificationManager.notifyPlayer(playerBeCache,MessageFormat.format("装备{0}修理完毕！",toolsInBag.getName()),RequestCode.SUCCESS.getCode());
                }
        );
    }

    /**
     * 将不需要装备出售
     * @param player
     * @param toolsId
     */
    public void sellTools(PlayerBeCache player,Integer toolsId){
        //判断是否是正在装配的装备
        Tools tools = player.getEquipmentBar().get(toolsId);
        //是正在装配的装备->卸载装备,并提醒
        if (!Objects.isNull(tools)) {
            takeOffTools(player,tools);
            notificationManager.notifyPlayer(player,"由于该装备之前处于装备栏，请注意自己属性的变化值",RequestCode.WARNING.getCode());
        }
        //是否是背包中的物品
        Tools toolsInBag = player.getBagBeCache().getToolsMap().get(toolsId);
        if (Objects.isNull(toolsInBag)) {
            notificationManager.notifyPlayer(player, "你暂时还没有拥有这件物品哦", RequestCode.BAD_REQUEST.getCode());
            return ;
        }
        //判断是否是重叠的物品
        Integer count = toolsInBag.getCount();
        if(count>1){
            //有叠加的情况下，减少叠加数量
            toolsInBag.setCount(toolsInBag.getCount()-1);
        }else{
            //只有一件的情况下移除该物品
            player.getBagBeCache().getToolsMap().remove(toolsId);
        }
        player.setMoney(player.getMoney()+toolsInBag.getPriceOut());
        notificationManager.notifyPlayer(player,MessageFormat.format("{0}出售成功，获得金{1}币",
                toolsInBag.getName(),toolsInBag.getPriceOut()),RequestCode.SUCCESS.getCode());
    }
}
