package com.example.gameservicedemo.game.scene.cache;

import com.example.gameservicedemo.base.IdGenerator;
import com.example.gameservicedemo.game.scene.bean.NPC;
import com.example.gameservicedemo.game.scene.bean.SceneObject;
import com.example.gameservicedemo.game.scene.service.SceneObjectService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/25/15:41
 * @Description: npc缓存
 */
@Component
public class NPCCache {
    @Autowired
    SceneObjectService sceneObjectService;

    private Map<Long, NPC> npcCache=new ConcurrentHashMap<>();
    public void putCahce(SceneObject npc){
        NPC newNpc = new NPC();
        BeanUtils.copyProperties(npc,newNpc);
        //生成一个uuid
        Long uuid = IdGenerator.getAnId();
        newNpc.setUuid(uuid);
        npcCache.put(newNpc.getUuid(), newNpc);
    }

    public NPC getNpcByUuid(Long uuid){
        return npcCache.get(uuid);
    }
}
