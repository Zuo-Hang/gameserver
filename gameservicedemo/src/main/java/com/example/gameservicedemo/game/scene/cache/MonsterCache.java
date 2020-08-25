package com.example.gameservicedemo.game.scene.cache;

import com.example.gameservicedemo.game.scene.bean.Monster;
import com.example.gameservicedemo.game.scene.bean.NPC;
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
 * @Date: 2020/08/25/15:41
 * @Description: 怪物缓存  生成的怪物
 */
@Component
public class MonsterCache {
    @Autowired
    SceneObjectService sceneObjectService;
    private ConcurrentHashMap<Long, Monster> monsterCache=new ConcurrentHashMap<>();
    public void putCahce(SceneObject monster){
        Monster newMonster = new Monster();
        BeanUtils.copyProperties(monster,newMonster);
        //生成一个uuid
        Long uuid = sceneObjectService.generateObjectId();
        newMonster.setUuid(uuid);
        monsterCache.put(newMonster.getUuid(), newMonster);
    }
    //怪物刷新
    //怪物掉落
    public Monster getMonsterByUUId(Long uuid) {
        return monsterCache.get(uuid);
    }
}
