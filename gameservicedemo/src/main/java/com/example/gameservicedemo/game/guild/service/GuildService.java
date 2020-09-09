package com.example.gameservicedemo.game.guild.service;

import com.example.gameservicedemo.game.guild.cache.GuildCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/09/17:07
 * @Description:
 */
@Slf4j
@Service
public class GuildService {
    @Autowired
    GuildCache guildCache;
}
