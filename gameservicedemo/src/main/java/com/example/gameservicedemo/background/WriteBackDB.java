package com.example.gameservicedemo.background;

import com.example.gamedatademo.bean.Player;
import com.example.gamedatademo.mapper.BagMapper;
import com.example.gamedatademo.mapper.PlayerMapper;
import com.example.gameservicedemo.event.Event;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.cache.PlayerCache;
import com.example.gameservicedemo.manager.TimedTaskManager;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
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
    BagMapper bagMapper;

    /**
     * 以一个set标记，避免在待回写数据库的等待期间，同一个对象提交多次回写任务
     */
    private Set<Player> shouldWrite= new ConcurrentSkipListSet();

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
        if(!shouldWrite.contains(player)){
            shouldWrite.add(player);
            ScheduledThreadPool.schedule(()->{
                //写回
                playerMapper.updateByPlayerId(player);
                player.getUpdate().clear();
                shouldWrite.remove(player);
                return null;
            },1000*2, TimeUnit.MILLISECONDS);
        }
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
                synchronized (v){
                    //自动金币增长
                    v.setMoney(v.getMoney()+10);
                    //如果残血，自动回血
                    if(v.getHp()<v.getMaxHp()){
                        //按自身的每五秒回血值回血
                        Integer shouldAdd=v.getHp()+v.getToolsInfluence().get(14).getValue();
                        if(shouldAdd>v.getMaxHp()){//不能超过自身最大血量
                            shouldAdd=v.getMaxHp();
                        }
                        v.setHp(shouldAdd);
                    }
                    //如果残蓝，回蓝
                    if(v.getMp()<v.getMaxMp()){
                        //按自身的每五秒回血值回血
                        Integer shouldAdd=v.getMp()+v.getToolsInfluence().get(15).getValue();
                        if(shouldAdd>v.getMaxMp()){//不能超过自身最大魔法值
                            shouldAdd=v.getMaxMp();
                        }
                        v.setMp(shouldAdd);
                    }
                }
            });
            log.info("-------每五秒自动恢复机制执行完毕----------");
        });
    }
}
