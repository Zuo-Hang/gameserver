package com.example.gameservicedemo.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/20/11:19
 * @Description:
 */
public class ToolsPropertyInfoCache {
    public static final Map<Integer,String> toolsPropertyInfoCache=new HashMap<Integer, String>();
    static String[] strings={"HP增益","MP增益","物理攻击","法术攻击","物理穿透","法术穿透",
            "物理防御","法术防御","物理吸血","法术吸血","暴击率","冷却缩减","暴击效果","每5秒恢复HP","每5秒恢复MP"};
    static {
        for(int i=1;i<16;i++){
            toolsPropertyInfoCache.put(i,strings[i-1]);
        }
    }
}
