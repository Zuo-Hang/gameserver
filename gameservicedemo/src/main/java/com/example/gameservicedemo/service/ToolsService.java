package com.example.gameservicedemo.service;

import com.example.gameservicedemo.bean.shop.Tools;
import com.example.gameservicedemo.bean.shop.ToolsProperty;
import com.example.gameservicedemo.cache.ToolsCache;
import com.example.gameservicedemo.cache.ToolsPropertyCache;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/17/21:51
 * @Description: 处理装备等物品的服务
 */
@Component
@Slf4j
public class ToolsService {
    @Autowired
    ToolsCache toolsCache;
    @Autowired
    ToolsPropertyCache toolsPropertyCache;

    /**
     * 屏蔽数据与业务层
     *
     * @param toolsId
     * @return
     */
    public Tools getToolsById(Integer toolsId) {
        return toolsCache.getToolsById(toolsId);
    }

    /**
     * 根据配置信息初始化装备
     *
     * @param tools
     */
    public void initTools(Tools tools) {
        //加入这件工具的作用
        //获取到配置的json字符串
        String toolsProperties = tools.getToolsProperties();
        Gson gson = new Gson();
        //获取到的所有增益
        ArrayList<ToolsProperty> toolsPropertiesList = (ArrayList<ToolsProperty>) gson.fromJson(toolsProperties, new TypeToken<ArrayList<ToolsProperty>>() {
        }.getType());
        toolsPropertiesList.forEach(pro->{
            ToolsProperty toolsPropertyById = toolsPropertyCache.getToolsPropertyById(pro.getId());
            pro.setName(toolsPropertyById.getName());
            pro.setType(toolsPropertyById.getType());
            pro.setDescribe(toolsPropertyById.getDescribe());
        });
        tools.setToolsPropertie(toolsPropertiesList);
        log.info(MessageFormat.format("装备：{0} 的自带增益初始化完成",tools.getName()));
    }

    /**
     * 装配某件装备
     *
     * @param tools
     */
    public boolean assemblingTools(Tools tools) {
        return true;
    }

    /**
     * 卸载某个装备
     *
     * @param tools
     */
    public void uninstallTools(Tools tools) {

    }
}
