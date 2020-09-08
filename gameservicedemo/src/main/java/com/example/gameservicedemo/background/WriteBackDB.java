package com.example.gameservicedemo.background;

import com.example.gamedatademo.bean.Bag;
import com.example.gamedatademo.bean.Player;
import com.example.gamedatademo.mapper.BagMapper;
import com.example.gamedatademo.mapper.PlayerMapper;
import com.example.gameservicedemo.event.Event;
import com.example.gameservicedemo.game.bag.bean.BagBeCache;
import com.example.gameservicedemo.game.hurt.ChangePlayerInformationImp;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.cache.PlayerCache;
import com.example.gameservicedemo.manager.TimedTaskManager;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/12/16:50
 * @Description:
 */
@Component
@Slf4j
public class WriteBackDB{
    @Autowired
    PlayerMapper playerMapper;
    @Autowired
    PlayerCache playerCache;
    @Autowired
    ChangePlayerInformationImp changePlayerInformationImp;
    @Autowired
    BagMapper bagMapper;

    /**
     * 以一个set标记，避免在待回写数据库的等待期间，同一个对象提交多次回写任务
     */
    private Set<Integer> shouldWrite= new ConcurrentSkipListSet();

    private  ThreadFactory WriteBackDBThreadPoolFactory = new ThreadFactoryBuilder()
            .setNameFormat("WriteBackDBThreadPool-%d").setUncaughtExceptionHandler((t,e) -> e.printStackTrace()).build();
    private  ScheduledExecutorService ScheduledThreadPool =
            Executors.newScheduledThreadPool( Runtime.getRuntime().availableProcessors()*2+1,WriteBackDBThreadPoolFactory);

    /**
     * 每隔15秒将用户的数据回写到数据库(调用了这个方法即代表向线程池提交了一个定时任务，只需在数据改变之后，提交之前将改变了的属性标志放到set中)
     * 目前只有金币的改变
     * @return
     */
    public  Future<Event> delayWriteBackPlayer(Player player){
        if(!shouldWrite.contains(player.getPlayerId())){
            shouldWrite.add(player.getPlayerId());
            ScheduledThreadPool.schedule(()->{
                //写回
                playerMapper.updateByPlayerId(player);
                player.getUpdate().clear();
                shouldWrite.remove(player.getPlayerId());
                return null;
            },1000*2, TimeUnit.MILLISECONDS);
        }
        return null;
    }

    public  Future<Event> delayWriteBackBag(BagBeCache bag){
            ScheduledThreadPool.schedule(()->{
                //写回
                Gson gson = new Gson();
                bag.setTools(gson.toJson(bag.getToolsMap().values()));
                bag.setItems(gson.toJson(bag.getItemMap().values()));
                bagMapper.updateByBagId(bag);
                return null;
            },0, TimeUnit.MILLISECONDS);
        return null;
    }

    /**
     * 五秒回蓝回血机制,自动金币增长
     */
    @PostConstruct
    public void recoveryHpMp(){
        TimedTaskManager.scheduleAtFixedRate(0,1000*5,()->{
            Map<Channel, PlayerBeCache> allPlayerCache = playerCache.getAllPlayerCache();
            allPlayerCache.values().forEach(v->{
                //防止线程安全问题---------------------------------------在这里应该调用专门更改的方法
                changePlayerInformationImp.changeHp(v,v.getToolsInfluence().get(14).getValue());
                changePlayerInformationImp.changePlayerMagic(v,v.getToolsInfluence().get(15).getValue());
                changePlayerInformationImp.changePlayerMoney(v,10);
            });
            log.info("-------每五秒自动恢复机制执行完毕----------");
        });
    }
}
