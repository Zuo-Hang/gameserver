package com.example.gameservicedemo.cache;

import com.example.gameservicedemo.bean.shop.Tools;
import com.example.gameservicedemo.service.ToolsService;
import com.example.gameservicedemo.util.excel.subclassexcelutil.ToolsExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/17/21:16
 * @Description:
 */
@Component
@Slf4j
public class ToolsCache {

    @Autowired
    public ToolsService toolsService;

    private HashMap<Integer, Tools> toolsCache= new HashMap<Integer, Tools>();
    @PostConstruct
    public void init(){
        ToolsExcelUtil toolsExcelUtil = new ToolsExcelUtil("C:\\java_project\\mmodemo\\gameservicedemo\\src\\main\\resources\\game_configuration_excel\\tools.xlsx");
        toolsExcelUtil.getMap().values().forEach((v)->{
            //将这个装备的效果加载
            toolsService.initTools(v);
            toolsCache.put(v.getId(),v);
        });
        log.info("物品缓存完毕");
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
