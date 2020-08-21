package com.example.gameservicedemo.cache;

import com.example.gameservicedemo.bean.skill.Skill;
import com.example.gameservicedemo.util.excel.subclassexcelutil.SkillExcelUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/12/17:35
 * @Description: 技能的缓存 读入Excel 并生成实体对象进行缓存
 */
@Component
@Slf4j
public class SkillCache {
    /**
     * 缓存
     */
    private static Cache<Integer, Skill> skillsCache = CacheBuilder.newBuilder()
            .recordStats()
            .removalListener(
                    notification -> log.debug(notification.getKey() + "技能被移除, 原因是" + notification.getCause())
            ).build();

    /**
     * 初始化技能
     */
    @PostConstruct
    private void init() {
        SkillExcelUtil skillExcelUtil = new SkillExcelUtil("C:\\java_project\\mmodemo\\gameservicedemo\\src\\main\\resources\\game_configuration_excel\\skill.xlsx");
        Map<Integer, Skill> skillMap = skillExcelUtil.getMap();
        skillMap.values().forEach((v) -> skillsCache.put(v.getId(), v));
        log.info("技能配置表加载");
    }

    /**
     * 获取技能
     *
     * @param skillId
     * @return
     */
    public static Skill get(Integer skillId) {
        return skillsCache.getIfPresent(skillId);
    }

    /**
     * 添加技能
     *
     * @param skillId
     * @param value
     */
    public static void put(Integer skillId, Skill value) {
        skillsCache.put(skillId, value);
    }
}
