package com.example.gameservicedemo.service;

import com.example.gameservicedemo.bean.RoleType;
import com.example.gameservicedemo.cache.RoleTypeCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/13/20:23
 * @Description: 角色类型服务
 */
@Component
public class RoleTypeService {
    @Autowired
    RoleTypeCache roleTypeCache;

    /**
     * 获取角色类型
     * @param roleTypeId
     * @return
     */
    public RoleType getRoleTypeById(Integer roleTypeId){
        return roleTypeCache.getRoleType(roleTypeId);
    }

    public List<RoleType> getAllRoleType(){
        Map<Integer, RoleType> roleTypeMap = roleTypeCache.asMap();
        ArrayList<RoleType> roleTypes = new ArrayList<>();
        for(RoleType roleType:roleTypeMap.values()){
            roleTypes.add(roleType);
        }
        return roleTypes;
    }
}
