package com.example.gameservicedemo.game.skill.cache;

import com.example.gameservicedemo.game.buffer.bean.Buffer;
import com.example.gameservicedemo.util.excel.subclassexcelutil.BufferExcelUtill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/17/16:25
 * @Description: 对于buffer的缓存
 */
@Component
@Slf4j
public class BufferCache {
    /**
     * 以一个map来缓存所有的buf
     */
    private Map<Integer, Buffer> bufferCache = new HashMap<Integer, Buffer>();

    /**
     * 启动时初始化
     */
    @PostConstruct
    public void init() {
        BufferExcelUtill excelUtill = new BufferExcelUtill("C:\\java_project\\mmodemo\\gameservicedemo\\src\\main\\resources\\game_configuration_excel\\buffer.xlsx");
        Map<Integer, Buffer> map = excelUtill.getMap();
        map.values().forEach((v) -> bufferCache.put(v.getId(), v));
        log.info("buffer加载完毕！");
    }

    public Buffer getBufById(Integer id) {
        return bufferCache.get(id);
    }

}
