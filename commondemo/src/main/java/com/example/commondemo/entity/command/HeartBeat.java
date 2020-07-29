package com.example.commondemo.entity.command;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/29/17:57
 * @Description:心跳
 */
public class HeartBeat extends BaseCommand{
    @Protobuf(fieldType = FieldType.INT32, order=1, required = true)
    int serviceCode =0;
}
