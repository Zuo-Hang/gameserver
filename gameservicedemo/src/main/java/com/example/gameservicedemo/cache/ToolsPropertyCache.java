package com.example.gameservicedemo.cache;

import com.example.gameservicedemo.bean.shop.ToolsProperty;
import com.example.gameservicedemo.util.excel.subclassexcelutil.ToolsPropertyExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/17/21:43
 * @Description:
 */
@Component
@Slf4j
public class ToolsPropertyCache {
    private HashMap<Integer, ToolsProperty> toolsPropertyCache= new HashMap<Integer, ToolsProperty>();
    @PostConstruct
    public void init(){
        ToolsPropertyExcelUtil toolsPropertyExcelUtil = new ToolsPropertyExcelUtil("C:\\java_project\\mmodemo\\gameservicedemo\\src\\main\\resources\\game_configuration_excel\\toolsProperty.xlsx");
        toolsPropertyExcelUtil.getMap().values().forEach((v)->toolsPropertyCache.put(v.getId(),v));
        log.info("装备属性信息缓存加载完毕");
    }
    public ToolsProperty getToolsPropertyById(Integer id){
        return toolsPropertyCache.get(id);
    }
}
