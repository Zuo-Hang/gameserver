package com.example.commondemo.command;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/28/21:29
 * @Description:角色相邻场景下的移动
 */
@Data
public class Move extends BaseCommand{
    @Protobuf(fieldType = FieldType.INT32, order=1, required = true)
    int serviceCode =2002;
    /**
     * 目的地
     */
    @Protobuf(fieldType = FieldType.STRING, order=2, required = true)
    private String destination;
}
