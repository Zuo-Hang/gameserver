package com.example.gameservicedemo.game.scene.bean;

import com.example.gameservicedemo.base.bean.Creature;
import com.example.gameservicedemo.game.buffer.bean.Buffer;
import com.example.gameservicedemo.game.skill.bean.Skill;
import com.example.gameservicedemo.util.excel.EntityName;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/11/15:44
 * @Description: 场景内的实体，所具有的共性。可以从Excel中配置
 */
@Data
public class SceneObject implements Creature {
    @EntityName(column = "id")
    private Integer id;
    @EntityName(column = "名字")
    private String name;
    @EntityName(column = "魔法值")
    private Integer mp;
    @EntityName(column = "生命值")
    private Integer hp;
    @EntityName(column = "法术攻击")
    private Integer magicAttack;
    @EntityName(column = "物理攻击")
    private Integer physicalAttack;
    @EntityName(column = "物理防御")
    private Integer physicalDefense;
    @EntityName(column = "法术防御")
    private Integer magicDefense;
    @EntityName(column = "护盾")
    private Integer shield;
    @EntityName(column = "护盾刷新时间")
    private Integer shieldRefresh;
    @EntityName(column = "法术护盾")
    private Integer magicShield;
    @EntityName(column = "法术护盾刷新时间")
    private Integer magicShieldRefresh;
    @EntityName(column = "交谈文本")
    private String talk = "";
    @EntityName(column = "掉落金币")
    private Integer dropGoldCoin;
    @EntityName(column = "掉落经验")
    private Integer dropExp;
    @EntityName(column = "技能")
    private String skills;
    @EntityName(column = "状态")
    private Integer state;
    @EntityName(column = "角色类型")
    private Integer roleType;
    @EntityName(column = "刷新时间")
    private Integer refreshTime;
    @EntityName(column = "掉落物品")
    private String drop;
    @EntityName(column = "任务")
    private String quests;
    @EntityName(column = "角色描述")
    private String describe;

    //最大血量
    Integer maxHp;
    //攻击目标
    Creature target;
    // 死亡时间
    private long deadTime;
    //需初始化
    private volatile boolean canUseSkill=true; //不能使用技能即被沉默
    private Map<Integer, Skill> hasUseSkillMap = new ConcurrentHashMap<>();
    private Map<Integer,Skill> skillHaveMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, Buffer> bufferMap =  new ConcurrentHashMap<>();

    @Override
    public Integer getPHurt() {
        return physicalAttack;
    }

    @Override
    public Integer getMHurt() {
        return magicAttack;
    }

    @Override
    public Integer getPDefense() {
        return physicalDefense;
    }

    @Override
    public Integer getMDefense() {
        return magicDefense;
    }

    //场景对象打出的伤害没有穿透值
    @Override
    public Integer getPPenetration() {
        return 0;
    }

    @Override
    public Integer getMPenetration() {
        return 0;
    }
}
