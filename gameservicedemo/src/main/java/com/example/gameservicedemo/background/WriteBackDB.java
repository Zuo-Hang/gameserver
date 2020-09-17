package com.example.gameservicedemo.background;

import com.example.gamedatademo.bean.Player;
import com.example.gamedatademo.mapper.BagMapper;
import com.example.gamedatademo.mapper.GuildMapper;
import com.example.gamedatademo.mapper.PlayerMapper;
import com.example.gameservicedemo.event.Event;
import com.example.gameservicedemo.game.bag.bean.BagBeCache;
import com.example.gameservicedemo.game.guild.bean.GuildBeCache;
import com.example.gameservicedemo.game.hurt.ChangePlayerInformationImp;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.service.PlayerLoginService;
import com.example.gameservicedemo.manager.TimedTaskManager;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
    GuildMapper guildMapper;
    @Autowired
    PlayerLoginService playerLoginService;
    @Autowired
    ChangePlayerInformationImp changePlayerInformationImp;
    @Autowired
    BagMapper bagMapper;

    Gson gson = new Gson();

    /**
     * 以一个set标记，避免在待回写数据库的等待期间，同一个对象提交多次回写任务
     */
    private Set<Integer> shouldWritePlayer= new ConcurrentSkipListSet();
    private Set<Integer> shouldWriteBag= new ConcurrentSkipListSet();
    private Set<Long> shouldWriteGuild= new ConcurrentSkipListSet();

    private ThreadFactory WriteBackDBThreadPoolFactory = new ThreadFactoryBuilder()
            .setNameFormat("WriteBackDBThreadPool-%d").setUncaughtExceptionHandler((t,e) -> e.printStackTrace()).build();
    private ScheduledExecutorService ScheduledThreadPool =
            Executors.newScheduledThreadPool( Runtime.getRuntime().availableProcessors()*2+1,WriteBackDBThreadPoolFactory);

    /**
     * 每隔15秒将用户的数据回写到数据库(调用了这个方法即代表向线程池提交了一个定时任务，只需在数据改变之后，提交之前将改变了的属性标志放到set中)
     * 目前只有金币的改变
     * @return
     */
    public  Future<Event> delayWriteBackPlayer(Player player){
        if(!shouldWritePlayer.contains(player.getPlayerId())){
            shouldWritePlayer.add(player.getPlayerId());
            ScheduledThreadPool.schedule(()->{
                //写回
                playerMapper.updateByPlayerId(player);
                player.getUpdate().clear();
                shouldWritePlayer.remove(player.getPlayerId());
                return null;
            },1000*2, TimeUnit.MILLISECONDS);
        }
        return null;
    }

    /**
     * 更新背包数据库
     * @param bag
     * @return
     */
    public  Future<Event> updateBagDb(BagBeCache bag){
        if(!shouldWriteBag.contains(bag.getId())){
            shouldWriteBag.add(bag.getId());
            ScheduledThreadPool.schedule(()->{
                //写回
                bagMapper.updateByBagId(bag);
                bag.getUpdate().clear();
                shouldWriteBag.remove(bag.getId());
                return null;
            },1000, TimeUnit.MILLISECONDS);
        }
        return null;
    }

    /**
     * 向player表插入数据
     * @param player 待插入数据
     * @return
     */
    public Future<Event> insertPlayer(Player player){
        ScheduledThreadPool.schedule(()->{
            playerMapper.insert(player);
        },0,TimeUnit.MILLISECONDS);
        return null;
    }

    /**
     * 持久化插入公会
     * @param guild 公会
     */
    public void insertGuild(GuildBeCache guild) {
        ScheduledThreadPool.execute(() -> {
            guild.setMember(gson.toJson(guild.getMemberIdList()));
            guild.setWarehouse(gson.toJson(guild.getWarehouseMap()));
            guild.setJoinRequest(gson.toJson(guild.getPlayerJoinRequestMap()));
            guildMapper.insert(guild);
        });
    }

    /**
     * 更新公会操作
     * @param guild
     * @return
     */
    public  Future<Event> updateGuildDb(GuildBeCache guild){
        if(!shouldWriteGuild.contains(guild.getId())){
            shouldWriteGuild.add(guild.getId());
            ScheduledThreadPool.schedule(()->{
                //写回
                guildMapper.updateByGuildId(guild);
                guild.getUpdate().clear();
                shouldWriteGuild.remove(guild.getId());
                return null;
            },1000, TimeUnit.MILLISECONDS);
        }
        return null;
    }



    /**
     * 五秒回蓝回血机制,自动金币增长
     */
    @PostConstruct
    public void recoveryHpMp(){
        TimedTaskManager.scheduleAtFixedRate(0,1000*5,()->{
            playerLoginService.getAllPlayerLoaded().forEach(id->{
                PlayerBeCache playerById = playerLoginService.getPlayerById(id);
                //防止线程安全问题---------------------------------------在这里应该调用专门更改的方法
                changePlayerInformationImp.changeHp(playerById,playerById.getToolsInfluence().get(14).getValue());
                changePlayerInformationImp.changePlayerMagic(playerById,playerById.getToolsInfluence().get(15).getValue());
                changePlayerInformationImp.changePlayerMoney(playerById,10);
            });
            log.info("-------每五秒自动恢复机制执行完毕----------");
        });
    }
}
