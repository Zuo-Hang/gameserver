package com.example.gameservicedemo.service;

import com.example.gameservicedemo.bean.PlayerBeCache;
import com.example.gameservicedemo.bean.shop.Tools;
import com.example.gameservicedemo.bean.shop.ToolsProperty;
import com.example.gameservicedemo.cache.ToolsCache;
import com.example.gameservicedemo.cache.ToolsPropertyCache;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.util.ArrayList;

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
//    final
//    ToolsPropertyCache toolsPropertyCache;

//    public ToolsService(ToolsCache toolsCache, ToolsPropertyCache toolsPropertyCache) {
//        this.toolsCache = toolsCache;
//        this.toolsPropertyCache = toolsPropertyCache;
//    }

    /**
     * 屏蔽数据与业务层
     *
     * @param toolsId
     * @return
     */
    public Tools getToolsById(Integer toolsId) {
        return toolsCache.getToolsById(toolsId);
    }

//    /**
//     * 根据配置信息初始化装备
//     *
//     * @param tools
//     */
//    public void initTools(Tools tools) {
//        //加入这件工具的作用
//        //获取到配置的json字符串
//        String toolsProperties = tools.getToolsProperties();
//        Gson gson = new Gson();
//        //获取到的所有增益
//        ArrayList<ToolsProperty> toolsPropertiesList = (ArrayList<ToolsProperty>) gson.fromJson(toolsProperties, new TypeToken<ArrayList<ToolsProperty>>() {
//        }.getType());
//        toolsPropertiesList.forEach(pro->{
//            ToolsProperty toolsPropertyById = toolsPropertyCache.getToolsPropertyById(pro.getId());
//            pro.setName(toolsPropertyById.getName());
//            pro.setType(toolsPropertyById.getType());
//            pro.setDescribe(toolsPropertyById.getDescribe());
//        });
//        tools.setToolsPropertie(toolsPropertiesList);
//        log.info(MessageFormat.format("装备：{0} 的自带增益初始化完成",tools.getName()));
//    }

    /**
     * 装配某件装备
     * 1.获取玩家的背包
     * 2.判断背包中是否有此装备，判断此物品是否是可穿戴的装备
     * 3.判断穿戴位置是否有空，无空则替换装备
     * 4.穿戴完毕后改变玩家增益属性
     * @param tools
     */
    public boolean assemblingTools(PlayerBeCache player ,Tools tools) {
        return true;
    }

    /**
     * 卸载某个装备
     * 1.判断此装备是否被装配
     * 2.移除装备放入背包
     * @param tools
     */
    public void uninstallTools(PlayerBeCache player,Tools tools) {

    }
}
