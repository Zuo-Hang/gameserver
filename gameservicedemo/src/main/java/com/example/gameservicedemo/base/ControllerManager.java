package com.example.gameservicedemo.base;

import com.example.commondemo.command.BaseCommand;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/29/18:16
 * @Description:处理、执行所有命令类和控制类的对应关系
 */
public class ControllerManager {
    /**
     * serviceCode标志和服务之间的映射
     */
    private final static Map<Integer, BaseController> CONTROLLER_MAP = new ConcurrentHashMap<>();

    /**
     * 向映射中添加serviceCode和controller的对应关系
     *
     * @param serviceCode
     * @param controller
     */
    public static void add(Integer serviceCode, BaseController controller) {
        CONTROLLER_MAP.put(serviceCode, controller);
    }

    public BaseController getController(Integer serviceCode) {
        return CONTROLLER_MAP.get(serviceCode);
    }

    /**
     * 执行任务，在这里会将任务由一个单例线程池顺序执行。
     * @param controller    要执行的任务
     * @param ctx   上下文
     * @param msg 信息对象
     */
    public void execute(BaseController controller, ChannelHandlerContext ctx, BaseCommand msg) {
        //Player player = playerDataService.getPlayerByCtx(ctx);

        // 玩家在场景内则用场景的执行器执行
//        Optional.ofNullable(player).map(Player::getCurrentScene).ifPresent(
//                scene -> scene.getSingleThreadSchedule().execute(
//                        () -> {
//                            log.debug("{} 的 {} 在执行命令 {}",
//                                    scene.getName(),player.getName(),msg.getContent());
//                            try {
//                                controller.handle(ctx,msg);
//                            } catch (Exception e) {
//                                NotificationManager.notifyByCtx(ctx,"这个功能暂时无法使用，请忽略本功能");
//                                e.printStackTrace();
//                            }
//                        })
//        );
//
//        // 如果用户尚未加载角色
//        if (Objects.isNull(player) || Objects.isNull(player.getCurrentScene())) {
//            controller.handle(ctx,msg);
        }
}
