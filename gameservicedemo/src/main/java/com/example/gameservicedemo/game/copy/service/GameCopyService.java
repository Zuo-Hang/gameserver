package com.example.gameservicedemo.game.copy.service;

import com.example.commondemo.base.RequestCode;
import com.example.gameservicedemo.base.IdGenerator;
import com.example.gameservicedemo.event.EventBus;
import com.example.gameservicedemo.event.model.GameCopyEvent;
import com.example.gameservicedemo.game.copy.bean.BOSS;
import com.example.gameservicedemo.game.copy.bean.GameCopyScene;
import com.example.gameservicedemo.game.player.bean.PlayerBeCache;
import com.example.gameservicedemo.game.player.service.PlayerDataService;
import com.example.gameservicedemo.game.player.service.PlayerLoginService;
import com.example.gameservicedemo.game.scene.bean.Monster;
import com.example.gameservicedemo.game.scene.bean.Scene;
import com.example.gameservicedemo.game.scene.bean.SceneObjectType;
import com.example.gameservicedemo.game.scene.bean.SceneType;
import com.example.gameservicedemo.game.scene.cache.SceneCache;
import com.example.gameservicedemo.game.scene.service.MonsterAiService;
import com.example.gameservicedemo.game.scene.service.SceneObjectService;
import com.example.gameservicedemo.game.scene.service.SceneService;
import com.example.gameservicedemo.game.team.bean.Team;
import com.example.gameservicedemo.game.team.cache.TeamCache;
import com.example.gameservicedemo.game.team.service.TeamService;
import com.example.gameservicedemo.manager.NotificationManager;
import com.example.gameservicedemo.manager.TimedTaskManager;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/27/9:55
 * @Description:
 */

@Slf4j
@Service
public class GameCopyService {
    @Autowired
    PlayerLoginService playerLoginService;
    @Autowired
    SceneCache sceneCache;
    @Autowired
    SceneObjectService sceneObjectService;
    @Autowired
    SceneService sceneService;
    @Autowired
    TeamService teamService;
    @Autowired
    TeamCache teamCache;
    @Autowired
    MonsterAiService monsterAiService;
    @Autowired
    PlayerDataService playerDataService;
    @Autowired
    NotificationManager notificationManager;

    /**
     * 以团队的形式进入副本
     *
     * @param team
     * @param copySceneId
     */
    public void enterGameCopyByTeam(Team team, Integer copySceneId) {
        Map<Long, PlayerBeCache> teamPlayer = team.getTeamPlayer();
        // 初始化好的副本场景
        GameCopyScene gameCopyScene = initGameCopy(copySceneId);

        if (Objects.isNull(gameCopyScene) || Objects.isNull(gameCopyScene.getCopySceneTime())) {
            return;
        }
        teamPlayer.values().forEach(
                player -> {
                    // 记录玩家原先的位置
                    gameCopyScene.getPlayerFrom().put(Long.valueOf(player.getId()), player.getNowAt());
                    // 进入副本
                    sceneService.moveToScene(player, gameCopyScene);
                }
        );

    }

    public void enterGameCopy(ChannelHandlerContext context, Integer copySceneId) {
        PlayerBeCache playerByContext = playerLoginService.getPlayerByContext(context);
        if (Objects.isNull(playerByContext)) {
            return;
        }
        // 初始化好的副本场景
        GameCopyScene gameCopyScene = initGameCopy(copySceneId);
        if (Objects.isNull(gameCopyScene) || Objects.isNull(gameCopyScene.getCopySceneTime())) {
            notificationManager.notifyPlayer(playerByContext, " 副本id错误 ", RequestCode.BAD_REQUEST.getCode());
            return;
        }
        // 记录玩家原先的位置
        gameCopyScene.getPlayerFrom().put(Long.valueOf(playerByContext.getId()), playerByContext.getNowAt());
        // 进入副本
        sceneService.moveToScene(playerByContext, gameCopyScene);
        return;
    }

    /**
     * 初始化副本
     *
     * @param gameCopySceneId
     * @return
     */
    GameCopyScene initGameCopy(Integer gameCopySceneId) {
        Scene scene = sceneCache.getScene(gameCopySceneId);
        if (!scene.getType().equals(SceneType.INSTANCE_SCENE.getCode())) {
            return null;
        }
        GameCopyScene gameCopyScene = new GameCopyScene();
        //这里是浅拷贝，内部所使用的集合为同一个集合,所以要忽略一些属性，不进行拷贝
        BeanUtils.copyProperties(scene, gameCopyScene, new String[]{ "monsters"});
        // 加载怪物和boss
        String gameObjectIds = gameCopyScene.getGameObjectIds();
        Arrays.stream(gameObjectIds.split(","))
                .map(Integer::valueOf)
                .map(sceneObjectService::getSceneObject)
                .forEach(sceneObject -> {
                            // 加载boss进怪物列表
                            if (sceneObject.getRoleType().equals(SceneObjectType.BOSS.getType())) {
                                BOSS boss = new BOSS();
                                BeanUtils.copyProperties(sceneObject, boss);
                                boss.setUuid(IdGenerator.getAnId());
                                gameCopyScene.getBossList().add(boss);
                            }
                        }
                );
        // 加载第一个boss
        BOSS firstBoss = nextBoss(gameCopyScene);
        gameCopyScene.setGuardBoss(firstBoss);
        // 开启场景心跳
        startTick(gameCopyScene);
        return gameCopyScene;
    }

    /**
     * 切换BOSS
     *
     * @param gameCopyScene
     * @return
     */
    private BOSS nextBoss(GameCopyScene gameCopyScene) {
        List<BOSS> monsterList = gameCopyScene.getBossList();
        BOSS nextBoss = null;
        if (monsterList.size() > 0) {
            nextBoss = monsterList.remove(0);
            log.debug("下一个boss {} ,当前boss列表 {}", nextBoss.getName(), monsterList);
            // 将Boss放入怪物集合中
            gameCopyScene.getMonsters().put(nextBoss.getUuid(), nextBoss);
            // 设置当前守关Boos
            gameCopyScene.setGuardBoss(nextBoss);
            // boss出场台词
            notificationManager.notifyScene(gameCopyScene, MessageFormat.format("\n {0} 说： {1} \n \n",
                    nextBoss.getName(), nextBoss.getTalk()), RequestCode.WARNING.getCode());
        }
        return nextBoss;
    }

    /**
     * 开启副本任务检测
     *
     * @param gameCopyScene
     */
    private void startTick(GameCopyScene gameCopyScene) {
        // 副本60ms进行心跳一次
        ScheduledFuture<?> attackTask = gameCopyScene.getSingleThreadSchedule().scheduleWithFixedDelay(() -> {
            Monster guardBoss = gameCopyScene.getGuardBoss();
            Map<Long, Monster> monsterMap = gameCopyScene.getMonsters();
            if (guardBoss == null && gameCopyScene.getBossList().size() == 0) {
                // 所有Boss死亡，通知场景内所有玩家挑战成功
                notificationManager.notifyScene(gameCopyScene,MessageFormat.format(
                        "恭喜你挑战副本{0}成功 ", gameCopyScene.getName()),RequestCode.SUCCESS.getCode());
                // 退出副本
                gameCopyScene.getPlayers().values().forEach(p -> {
                    exitGameCopy(p, gameCopyScene);
                    //发布副本通关事件
                    EventBus.publish(new GameCopyEvent(p,gameCopyScene));
                });
            }
            Optional.ofNullable(guardBoss).ifPresent(
                    boss -> {
                        if (boss.getHp() <= 0) {
                            // 如果守关boss死亡，下一个Boss出场，将守关boos移除怪物列表
                            gameCopyScene.setGuardBoss(nextBoss(gameCopyScene));
                            monsterMap.remove(guardBoss.getUuid());
                        } else {
                            // 如果boss尚未死亡，检测玩家玩家状态
                            if (!gameCopyScene.getFail()) {
                                // 设置怪物的攻击目标
                                if (Objects.isNull(boss.getTarget())) {
                                    gameCopyScene.getPlayers().values().stream().findAny()
                                            .ifPresent(boss::setTarget);
                                }
                                gameCopyScene.getPlayers().values().forEach(
                                        player -> {
                                            //如果玩家死亡则通知并移除玩家
                                            if (playerDataService.checkIsDead(player)) {
                                                notificationManager.notifyPlayer(player, "很遗憾，你挑战副本失败", RequestCode.BAD_REQUEST.getCode());
                                                exitGameCopy(player, gameCopyScene);
                                            }
                                        }
                                );
                            }
                        }
                    }
            );
            gameCopyScene.getMonsters().values().forEach(m -> {
                if (Objects.nonNull(m.getTarget())) {
                    //在这里进行了持续循环攻击
                    monsterAiService.startAI(m, gameCopyScene);
                }
            });
        }, 20, 60, TimeUnit.MILLISECONDS);
        gameCopyScene.setAttackTask(attackTask);
        // 副本关闭通知,提前10000毫秒（10秒）通知
        TimedTaskManager.schedule(gameCopyScene.getCopySceneTime() - 10000,
                () -> gameCopyScene.getPlayers().values().forEach(
                        p -> notificationManager.notifyPlayer(p, "副本将于十秒后关闭，请准备好传送。", RequestCode.WARNING.getCode())
                )
        );
        // 副本存活时间到期， 销毁副本，传送玩家出副本。 根据副本存在时间销毁副本定时器
        TimedTaskManager.schedule(gameCopyScene.getCopySceneTime(), () -> {
            if(gameCopyScene.getBossList().size()!=0){
                notificationManager.notifyScene(gameCopyScene,"挑战副本失败！",RequestCode.BAD_REQUEST.getCode());
            }
            gameCopyScene.getPlayers().values().forEach(p -> exitGameCopy(p, gameCopyScene));
            attackTask.cancel(false);
        });
    }
    /**
     * 是否在副本中
     *
     * @param context
     * @return
     */
    public boolean isInGameCopy(ChannelHandlerContext context) {
        PlayerBeCache playerByContext = playerLoginService.getPlayerByContext(context);
        if (Objects.isNull(playerByContext)) {
            notificationManager.notifyByCtx(context, "你还未加载角色！", RequestCode.BAD_REQUEST.getCode());
            return false;
        }
        return playerByContext.getSceneNowAt().getType().equals(SceneType.COMMON_SCENE.getCode());
    }

    /**
     * 退出副本
     *
     * @param player
     * @param scene
     */
    public void exitGameCopy(PlayerBeCache player, GameCopyScene scene) {
        scene.getPlayers().remove(player.getId());
        //对队伍的状态处理
        Long teamId = player.getTeamId();
        if(Objects.nonNull(teamId)){
            teamService.leaveTeam(player.getContext());
        }
        notificationManager.notifyScene(scene,MessageFormat.format("{0},离开了副本{1}",
                player.getName(),scene.getName()),RequestCode.WARNING.getCode());
        //副本的处理
        //场景内无人时，关闭场景任务
        if(scene.getPlayers().size()==0){
            scene.getAttackTask().cancel(false);
        }
        // 返回原来的场景
        player.setNowAt(scene.getPlayerFrom().get(Long.valueOf(player.getId())));
        sceneService.initPlayerScene(player);
        log.debug("返回原来的厂场景{}", player.getSceneNowAt().getName());
        notificationManager.notifyPlayer(player, "你已经退出副本", RequestCode.SUCCESS.getCode());
    }

    /**
     * 查看所有副本列表
     *
     * @param context
     */
    public void showGameCopy(ChannelHandlerContext context) {
        List<Scene> sceneList = sceneCache.getAllScene()
                .stream()
                .filter(s -> s.getType().equals(SceneType.INSTANCE_SCENE.getCode()))
                .collect(Collectors.toList());
        StringBuilder sb = new StringBuilder();
        sb.append("副本列表： \n");
        sceneList.forEach(scene -> sb.append(MessageFormat.format("副本id ：{0}  副本名称：{1} \n",
                scene.getId(), scene.getName())));
        notificationManager.notifyByCtx(context, sb, RequestCode.SUCCESS.getCode());
    }
}
