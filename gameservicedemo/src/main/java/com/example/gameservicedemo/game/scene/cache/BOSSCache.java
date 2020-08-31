package com.example.gameservicedemo.game.scene.cache;

import com.example.gameservicedemo.base.IdGenerator;
import com.example.gameservicedemo.game.copy.bean.BOSS;
import com.example.gameservicedemo.game.scene.bean.SceneObject;
import com.example.gameservicedemo.game.scene.service.SceneObjectService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/25/20:24
 * @Description:
 */
@Component
public class BOSSCache {
    @Autowired
    SceneObjectService sceneObjectService;
    private ConcurrentHashMap<Long, BOSS> bossCache=new ConcurrentHashMap<>();
    public void putCahce(SceneObject boss){
        BOSS newBoss = new BOSS();
        BeanUtils.copyProperties(boss,newBoss);
        //生成一个uuid
        Long uuid = IdGenerator.getAnId();
        newBoss.setUuid(uuid);
        bossCache.put(newBoss.getUuid(), newBoss);
    }
    //怪物刷新
    //怪物掉落
    public BOSS getBossByUUId(Long uuid) {
        return bossCache.get(uuid);
    }
}
