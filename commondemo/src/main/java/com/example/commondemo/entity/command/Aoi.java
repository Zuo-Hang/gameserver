package com.example.commondemo.entity.command;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/28/21:33
 * @Description:显示场景内各种游戏对象
 */
@Data
public class Aoi {
    @Protobuf(fieldType = FieldType.INT32, order=1, required = true)
    int serviceCode =3002;
}
