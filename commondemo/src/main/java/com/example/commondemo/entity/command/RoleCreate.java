package com.example.commondemo.entity.command;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/28/21:12
 * @Description:创建角色
 */
@Data
public class RoleCreate {
    @Protobuf(fieldType = FieldType.INT32, order=1, required = true)
    int serviceCode =9001;
    @Protobuf(fieldType = FieldType.STRING, order=2, required = true)
    private String roleName;
    @Protobuf(fieldType = FieldType.INT32, order=3, required = true)
    private int occupationCode;
}
