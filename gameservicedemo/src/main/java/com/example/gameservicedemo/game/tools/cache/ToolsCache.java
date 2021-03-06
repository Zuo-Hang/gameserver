package com.example.gameservicedemo.game.tools.cache;

import com.example.gameservicedemo.game.tools.bean.Tools;
import com.example.gameservicedemo.game.tools.bean.ToolsProperty;
import com.example.gameservicedemo.util.excel.subclassexcelutil.ToolsExcelUtil;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/17/21:16
 * @Description: 所有可以购买的物品的缓存（包括装备，药品，补给）
 */
@Component
@Slf4j
public class ToolsCache {
    private HashMap<Integer, Tools> toolsCache= new HashMap<Integer, Tools>();
    @PostConstruct
    public void init(){
        ToolsExcelUtil toolsExcelUtil = new ToolsExcelUtil("C:\\java_project\\mmodemo\\gameservicedemo\\src\\main\\resources\\game_configuration_excel\\tools.xlsx");
        Map<Integer, Tools> map = toolsExcelUtil.getMap();
        map.values().forEach((v)->{
            //将这个装备的效果加载
            initTools(v);
            toolsCache.put(v.getId(),v);
        });
        log.info("物品缓存完毕");
    }

    /**
     * 根据配置初始化工具的增益列表
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
        tools.setToolsPropertie(toolsPropertiesList);
        log.info(MessageFormat.format("装备：{0} 的自带增益初始化完成",tools.getName()));
    }

    /**
     * 按id获取物品
     * @param toolsId
     * @return
     */
    public Tools getToolsById(Integer toolsId){
        return toolsCache.get(toolsId);
    }
}
