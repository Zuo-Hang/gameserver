package com.example.gameservicedemo.game.player.cache;

import com.example.gameservicedemo.game.player.bean.RoleType;
import com.example.gameservicedemo.util.excel.subclassexcelutil.RoleTypeExcelUtil;
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
 * @Date: 2020/08/13/9:45
 * @Description: 从配置文件读取角色信息，将其创造实体对象并进行缓存
 */
@Component
@Slf4j
public class RoleTypeCache {
    /**
     * 缓存不过期
     */
    private Cache<Integer, RoleType> roleTypeCache = CacheBuilder.newBuilder()
            .removalListener(
                    notification -> System.out.println(notification.getKey() + "角色类型对象被移除, 原因是" + notification.getCause())
            ).build();

    /**
     * 初始化缓存
     */
    @PostConstruct
    public void init() {
        RoleTypeExcelUtil roleTypeExcelUtil = new RoleTypeExcelUtil("C:\\java_project\\mmodemo\\gameservicedemo\\src\\main\\resources\\game_configuration_excel\\roleType.xlsx");
        Map<Integer, RoleType> map = roleTypeExcelUtil.getMap();
        for (RoleType roleType : map.values()) {
            roleTypeCache.put(roleType.getId(), roleType);
        }
        log.info("角色类型加载完毕！");
    }

    /**
     * 从缓存中获取角色类型
     *
     * @param roleTypeId
     * @return
     */
    public RoleType getRoleType(Integer roleTypeId) {
        return roleTypeCache.getIfPresent(roleTypeId);
    }

    public Map<Integer, RoleType> asMap(){
        return roleTypeCache.asMap();
    }

}
