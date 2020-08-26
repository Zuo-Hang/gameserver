package com.example.gameservicedemo.game.team.cache;

import com.example.gameservicedemo.game.team.bean.Team;
import com.example.gameservicedemo.game.team.bean.TeamRequest;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/26/12:03
 * @Description: 存储所有已经创建的队伍
 */
@Component
public class TeamCache {
    private Map<Long, Team> teamCache=new ConcurrentHashMap<>();
    public void putTeam(Team team){
        teamCache.put(team.getId(),team);
    }
    public Team getTeam(Long teamId){
        return teamCache.get(teamId);
    }
    public void removeTeam(Long teamId){
        teamCache.remove(teamId);
    }


    /**
     *   组队请求的缓存,key是该次请求的id,value是这次请求
     */
    private Cache<Long, TeamRequest> teamRequestCache = CacheBuilder.newBuilder()
            // 设置60秒后移除组队请求
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .removalListener(
                    notification -> System.out.println(notification.getKey() + "组队请求被移除, 原因是" + notification.getCause())
            ).build();
    public void putTeamRequest(TeamRequest request){
        teamRequestCache.put(request.getId(),request);
    }

    public TeamRequest getTeamRequest(Long requestId){
        TeamRequest teamRequest = teamRequestCache.getIfPresent(requestId);
        return teamRequest;
    }
}
