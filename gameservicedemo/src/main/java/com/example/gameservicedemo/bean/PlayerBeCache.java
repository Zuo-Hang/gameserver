package com.example.gameservicedemo.bean;

import com.example.gamedatademo.bean.Player;
import com.example.gameservicedemo.bean.shop.Tools;
import com.example.gameservicedemo.bean.shop.ToolsProperty;
import com.example.gameservicedemo.bean.skill.Skill;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
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
     * 是否可以使用技能  （某些情况下不能使用技能，如修理装备的时候，使用金身的时候）
     */
    private volatile boolean canUseSkill=true;
    /**
     * 角色当前处于CD的技能集合
     */
    private Map<Integer, Skill> hasUseSkillMap = new ConcurrentHashMap<>();

    /**
     * 可以使用的技能集合
     * 主要为天赋技能、装备技能
     * 注意：不包含角色类型所附带的常驻技能
     */
    private Map<Integer,Skill> canUseSkillMap = new ConcurrentHashMap<>();
    /**
     * 角色当前的buffer,因为可能拥有多个重复的技能，所以这里使用List保存
     */
    private List<Buffer> bufferList = new CopyOnWriteArrayList<>();

    /**
     * 装备栏 装备id 装备 最大值为6 小于6则可以进行装配，否则只能换装
     */
    private Map<Integer, Tools> equipmentBar = new ConcurrentHashMap<>();
    /**
     * 需要被修理的装备的id列表
     */
    private List<Integer> needFix=new ArrayList<Integer>();
    // 背包栏
    private BagBeCache bagBeCache = null;
    /**
     * 这个集合的元素应该在用户加入缓存之前就初始化
     * 受装备影响的属性值，集合中的元素代表当前装备对某一方面加成的总和数值
     * ID
     * 1	HP增益
     * 2	MP增益
     * 3	物理攻击
     * 4	法术攻击
     * 5	物理穿透
     * 6	法术穿透
     * 7	物理防御
     * 8	法术防御
     * 9	物理吸血
     * 10	法术吸血
     * 11	暴击率
     * 12	冷却  为百分比
     * 13	暴击效果
     * 14	恢复HP速度
     * 15	恢复MP速度
     *
     * v 只有两个属性 id 和value  可以用id在缓存的map中查找到对这种id类型的描述
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
