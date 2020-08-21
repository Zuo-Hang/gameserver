package com.example.gameservicedemo.service.skill;

import com.example.gameservicedemo.bean.Creature;
import com.example.gameservicedemo.bean.skill.Skill;
import com.example.gameservicedemo.bean.scene.Scene;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/13/18:22
 * @Description: 技能生效的抽象
 */
@FunctionalInterface
public interface ISkillEffect {
    /**
     *  施放技能造成影响
     * @param initiator 施放者
     * @param target 施放目标
     * @param gameScene 场景
     * @param skill 技能
     */
    void skillEffect(Creature initiator, Creature target, Scene gameScene, Skill skill);
}
