package com.example.gameservicedemo.bean;

import com.example.gameservicedemo.bean.skill.Skill;
import com.example.gameservicedemo.cache.SkillCache;
import com.example.gameservicedemo.util.excel.EntityName;
import lombok.Data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/11/18:05
 * @Description: 角色类型 ，用户创建化身时可以进行选择。不同的类型的化身所具备的体能天赋都有所差异。
 */
@Data
public class RoleType {
    @EntityName(column = "id")
    private Integer id;

    @EntityName(column = "职业名称")
    private String name;



    @EntityName(column = "基础hp")
    private Integer baseHp;

    @EntityName(column = "基础mp")
    private Integer baseMp;
    @EntityName(column = "技能")
    private String skills = "";
    @EntityName(column = "增益属性")
    private String gainAttribute="";
    /**
     * 用来存放该角色所具备的技能  键为技能id 值为技能对象
     */
    private Map<Integer, Skill> skillMap = new HashMap<>();

    /**
     * 获取当前角色类型所拥有的技能集合
     */
    public Map<Integer, Skill> getSkillMap() {
        // 如果技能映射不存在，则现在加载
        if (skillMap.size() == 0 && !this.skills.equals("")) {
            String skillsString = this.getSkills();
            Arrays.stream(skillsString.split(","))
                    .map(Integer::valueOf)
                    .map(SkillCache::get)
                    .forEach(skill -> this.skillMap.put(skill.getId(), skill));
        }
        return skillMap;
    }

    public void setSkillMap(Map<Integer, Skill> skillMap) {
        this.skillMap = skillMap;
    }
}
