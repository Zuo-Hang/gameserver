package com.example.gameservicedemo.game.buffer.service;

import com.example.commondemo.base.RequestCode;
import com.example.gameservicedemo.game.buffer.bean.Buffer;
import com.example.gameservicedemo.game.player.service.PlayerDataService;
import com.example.gameservicedemo.base.bean.Creature;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.buffer.cache.BufferCache;
import com.example.gameservicedemo.manager.NotificationManager;
import com.example.gameservicedemo.manager.TimedTaskManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/17/17:04
 * @Description:
 */
@Service
@Slf4j
public class BufferService {
    @Autowired
    BufferCache bufferCache;
    @Autowired
    NotificationManager notificationManager;
    @Autowired
    PlayerDataService playerDataService;

    /**
     * 开始一个Buf
     *
     * @param creature
     * @param buffer
     * @return
     */
    public boolean startBuffer(Creature creature, Buffer buffer) {
        if (Objects.isNull(buffer)) {
            return false;
        }
        //新建一个buf对象，用于被生物的bufList存储
        Buffer playerBuffer = new Buffer();
        BeanUtils.copyProperties(buffer, playerBuffer);
        //设置开始时间
        playerBuffer.setStartTime(System.currentTimeMillis());
        creature.getBufferMap().put(playerBuffer.getId(), playerBuffer);
        // 如果是buffer有不良效果
        if (buffer.getEffect() != 0) {
            creature.setState(buffer.getEffect());
        }
        // 如果buffer有持续时间
        if (buffer.getDuration() != -1) {
            // 如果间隔时间不为-1，即buffer间隔触发
            if (buffer.getIntervalTime() != -1) {
                //Future为这个任务的执行结果
                Future cycleTask = TimedTaskManager.scheduleAtFixedRate(0, buffer.getIntervalTime(),
                        //调用lambda表达式传入一个Runable接口的实现
                        () -> {

                            if (Objects.nonNull(buffer.getHp()) && (buffer.getHp() != 0)) {
                                if ((creature.getHp() + buffer.getHp()) >= 0&& (creature.getHp() + buffer.getHp()) <= creature.getMaxHp() ) {
                                    creature.setHp(creature.getHp() + buffer.getHp());
                                    if(creature instanceof PlayerBeCache){
                                        notificationManager.notifyPlayer((PlayerBeCache) creature, MessageFormat.format(
                                                "你身上的buffer {0}  对你造成影响, hp:{1},当前hp:{2}\n",
                                                buffer.getName(), buffer.getHp(), creature.getHp()
                                        ), RequestCode.SUCCESS.getCode());
                                    }
                                } else if ((creature.getHp() + buffer.getHp() )>= creature.getMaxHp()) {
                                    creature.setHp(creature.getMaxHp());
                                } else {
                                    creature.setHp(0);
                                }
                                playerDataService.isPlayerDead((PlayerBeCache) creature, null);
                            }
                            if (Objects.nonNull(buffer.getMp()) && buffer.getMp() != 0 && creature instanceof PlayerBeCache) {
                                PlayerBeCache player = (PlayerBeCache) creature;
                                if (player.getMp() + buffer.getMp() > 0 && player.getMp() + buffer.getMp() < player.getMaxMp()) {
                                    player.setMp(player.getMp() + buffer.getMp());
                                    notificationManager.notifyPlayer(player, MessageFormat.format(
                                            "你身上的buffer {0}  对你造成影响, mp:{1},当前mp:{2}\n",
                                            buffer.getName(), buffer.getMp(), creature.getMp()
                                    ), RequestCode.SUCCESS.getCode());
                                } else if (player.getMaxMp() + buffer.getMp() >= player.getMaxMp()) {
                                    player.setMp(player.getMaxMp());
                                } else {
                                    player.setMp(0);
                                }
                            }
                            playerDataService.showPlayerInfo((PlayerBeCache) creature);
                        }
                );
                TimedTaskManager.scheduleWithData(buffer.getDuration(), () -> {
                    cycleTask.cancel(true);
                    return null;
                });
            }
            // buffer cd 处理
            TimedTaskManager.scheduleWithData(buffer.getDuration(),
                    () -> {
                        // 过期移除buffer
                        creature.getBufferMap().remove(playerBuffer.getId());
                        // 恢复正常状态
                        creature.setState(1);
                        // 如果是玩家，进行通知
                        if (creature instanceof PlayerBeCache) {
                            notificationManager.notifyPlayer((PlayerBeCache) creature, MessageFormat.format(
                                    "你身上的buffer {0}  结束\n", buffer.getName()
                            ), RequestCode.SUCCESS.getCode());
                            // 检测玩家是否死亡
                            playerDataService.isPlayerDead((PlayerBeCache) creature, null);
                        }
                        log.debug(" buffer过期清除定时器 {}", new Date());
                        return null;
                    });
        } else {
            // 永久buffer
        }
        return true;
    }

    /**
     * 按照id获取Buf
     *
     * @param bufferId id
     * @return buf
     */
    public Buffer getBuffer(Integer bufferId) {
        return bufferCache.getBufById(bufferId);
    }

    /**
     * 获取buffer的详细信息字符串
     *
     * @param buffer
     * @return
     */
    public String bufferInfo(Buffer buffer) {
        return new StringBuilder(MessageFormat.format("名称：{0}  效果：{1} 持续时间：{2} cd时间：{3} 描述：{4}",
                buffer.getName(),
                buffer.getEffect(),
                buffer.getDuration(),
                buffer.getIntervalTime(),
                buffer.getDescribe()
        )).toString();
    }
}
