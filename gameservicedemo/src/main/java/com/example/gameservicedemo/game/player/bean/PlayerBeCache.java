package com.example.gameservicedemo.game.player.bean;

import com.example.gamedatademo.bean.Player;
import com.example.gameservicedemo.game.bag.bean.BagBeCache;
import com.example.gameservicedemo.game.buffer.bean.Buffer;
import com.example.gameservicedemo.base.bean.Creature;
import com.example.gameservicedemo.game.scene.bean.Pet;
import com.example.gameservicedemo.game.scene.bean.Scene;
import com.example.gameservicedemo.game.tools.bean.Tools;
import com.example.gameservicedemo.game.tools.bean.ToolsProperty;
import com.example.gameservicedemo.game.skill.bean.Skill;
import com.example.gameservicedemo.game.tools.bean.ToolsPropertyInfo;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
     * 护盾
     */
    private Integer magicShield = 0;
    /**
     * 魔法，物理护盾
     */
    private Integer Shield = 0;

    Scene sceneNowAt=null;

    Pet pet=null;

    /**
     * 是否可以使用技能  （某些情况下不能使用技能，如修理装备的时候，使用金身的时候）
     */
    private volatile boolean canUseSkill = true;
    /**
     * 角色当前处于CD的技能集合，主要为装备技能。判断使用。
     */
    private Map<Integer, Skill> hasUseSkillMap = new ConcurrentHashMap<>();

    /**
     * 当前拥有的所有技能
     * 包含天赋技能、装备技能、职业常驻技能
     */
    private Map<Integer, Skill> skillHaveMap = new ConcurrentHashMap<>();
    /**
     * 角色当前的buffer,因为可能拥有多个重复的技能，所以这里使用List保存
     */
    private ConcurrentHashMap<Integer, Buffer> bufferMap = new ConcurrentHashMap<>();

    /**
     * 装备栏 装备id 装备 最大值为6 小于6则可以进行装配，否则只能换装
     */
    private Map<Long, Tools> equipmentBar = new ConcurrentHashMap<>();
    /**
     * 需要被修理的装备的id列表
     */
    private List<Long> needFix = new ArrayList<Long>();
    // 背包栏
    private BagBeCache bagBeCache = null;
    /**
     * 这个集合的元素应该在用户加入缓存之前就初始化
     * 受装备影响的属性值，集合中的元素代表当前装备对某一方面加成的总和数值
     */
    private Map<Integer, ToolsProperty> toolsInfluence = new ConcurrentHashMap<>();

    Long teamId=null;

    @Override
    public Integer getId() {
        return this.getPlayerId();
    }

    @Override
    public String getName() {
        return this.getPlayerName();
    }

    @Override
    public Integer getPHurt() {
        return toolsInfluence.get(ToolsPropertyInfo.Physical_attack.getId()).getValue();
    }

    @Override
    public Integer getMHurt() {
        return toolsInfluence.get(ToolsPropertyInfo.Magic_Attack.getId()).getValue();
    }

    @Override
    public Integer getPDefense() {
        return toolsInfluence.get(ToolsPropertyInfo.Physical_defense.getId()).getValue();
    }

    @Override
    public Integer getMDefense() {
        return toolsInfluence.get(ToolsPropertyInfo.Magic_defense.getId()).getValue();
    }

    @Override
    public Integer getPPenetration() {
        return toolsInfluence.get(ToolsPropertyInfo.Physical_penetration.getId()).getValue();
    }

    @Override
    public Integer getMPenetration() {
        return toolsInfluence.get(ToolsPropertyInfo.Spell_penetration.getId()).getValue();
    }


}
