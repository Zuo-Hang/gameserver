package com.example.gamedatademo;

import com.example.gamedatademo.bean.User;
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
    UserMapper userMapper;

    @GetMapping("/dept")
    public User getRole(Integer id){
        return userMapper.selectByUserId(id);
    }


}
