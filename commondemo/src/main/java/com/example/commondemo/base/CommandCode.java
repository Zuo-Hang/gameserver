package com.example.commondemo.base;

import com.example.commondemo.command.UserCreat;
import com.example.commondemo.message.Message;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/31/15:41
 * @Description:
 */
public class  CommandCode {
    public Map<Integer,String> CLASS_MAP=new HashMap<>();

    public CommandCode() {
        CLASS_MAP.put(200, Message.class.getName());
        CLASS_MAP.put(900,UserCreat.class.getName());
    }
}
