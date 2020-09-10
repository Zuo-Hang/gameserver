package com.example.gamedatademo.mapper;

import com.example.gamedatademo.bean.Guild;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/09/15:59
 * @Description:
 */
public interface GuildMapper {
    Integer insert(Guild guild);
    Guild selectByGuildId(Long GuildId);
    Integer updateByGuildId(Guild guild);
}
