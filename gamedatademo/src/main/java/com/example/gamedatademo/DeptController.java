package com.example.gamedatademo;

import com.example.gamedatademo.bean.Player;
import com.example.gamedatademo.bean.User;
import com.example.gamedatademo.mapper.PlayerMapper;
import com.example.gamedatademo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/03/15:48
 * @Description:
 */
@RestController
public class DeptController {
    @Autowired
    PlayerMapper playerMapper;
    @Autowired
    UserMapper userMapper;

    @GetMapping("/dept")
    public User getRole(Integer id){
        Player player = new Player();
        player.setPlayerId(1);
        player.setBagId(5);
        player.getUpdate().add(4);
        playerMapper.updateByPlayerId(player);
        player.getUpdate().clear();
        return userMapper.selectByUserId(id);
    }


}
