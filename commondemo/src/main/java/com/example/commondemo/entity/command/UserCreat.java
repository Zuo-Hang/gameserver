package com.example.commondemo.entity.command;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/28/9:31
 * @Description:创建用户
 */
@Data
public class UserCreat {
    @Protobuf(fieldType = FieldType.INT32, order=1, required = true)
    int serviceCode =900;
    @Protobuf(fieldType = FieldType.STRING, order=2, required = true)
    private String nickName;
    @Protobuf(fieldType = FieldType.STRING, order=3, required = true)
    private String password;
    @Protobuf(fieldType = FieldType.STRING, order=4, required = true)
    private String phoneNumber;
}
