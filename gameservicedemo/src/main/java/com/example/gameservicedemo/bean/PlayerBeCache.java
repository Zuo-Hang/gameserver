package com.example.gameservicedemo.bean;

import com.example.gamedatademo.bean.Player;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.nio.Buffer;
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
@EqualsAndHashCode(callSuper=true)
public class PlayerBeCache extends Player implements Creature{
    private ChannelHandlerContext context;
    private Creature target;

    /**  受职业配置表和装备影响 */
    private Integer hp;

    private Integer mp;
    /** 角色当前处于CD的技能集合 */
    private Map<Integer, Skill> hasUseSkillMap = new ConcurrentHashMap<>();

    /** 角色当前的buffer,因为可能拥有多个重复的技能，所以这里使用List保存 */
    private List<Buffer> bufferList = new CopyOnWriteArrayList<>();

    @Override
    public Integer getId() {
        return this.getPlayerId();
    }

    @Override
    public String getName() {
        return this.getPlayerName();
    }
}
