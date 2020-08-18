package com.example.gameservicedemo.bean;

import com.example.gamedatademo.bean.Player;
import com.example.gameservicedemo.bean.shop.Tools;
import com.example.gameservicedemo.bean.shop.ToolsProperty;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/05/17:13
 * @Description: 应当被缓存的化身信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PlayerBeCache extends Player implements Creature {
    private ChannelHandlerContext context;
    private Creature target;

    /**  受职业配置表和装备影响 */
    /**
     * 最大生命值
     */
    private Integer maxHp;
    /**
     * 最大魔法值
     */
    private Integer maxMp;

    /**
     * 当前的生命值和魔法值
     */
    private Integer mp;
    private Integer hp;
    /**
     * 角色当前处于CD的技能集合
     */
    private Map<Integer, Skill> hasUseSkillMap = new ConcurrentHashMap<>();

    /**
     * 角色当前的buffer,因为可能拥有多个重复的技能，所以这里使用List保存
     */
    private List<Buffer> bufferList = new CopyOnWriteArrayList<>();

    /**
     * 装备栏 装备id 装备 最大值为6 小于6则可以进行装配，否则只能换装
     */
    private Map<Integer, Tools> equipmentBar = new ConcurrentHashMap<>();
    // 背包栏
    private BagBeCache bagBeCache = null;
    /**
     * 这个集合的元素应该在用户加入缓存之前就初始化
     * 受装备影响的属性值，集合中的元素代表当前装备对某一方面加成的总和数值
     * <K,V>
     * 1：HP
     * 2：MP
     * 3：物理防御
     * 4：物理攻击
     * 5：力量
     * 6：智力
     * 7：精神
     * 8：命中
     * 9：法术防御
     * 10：耐力
     * 11:法术攻击
     */
    private Map<Integer, ToolsProperty> toolsInfluence = new ConcurrentHashMap<>();

    @Override
    public Integer getId() {
        return this.getPlayerId();
    }

    @Override
    public String getName() {
        return this.getPlayerName();
    }
}
