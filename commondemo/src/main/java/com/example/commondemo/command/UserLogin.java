package com.example.commondemo.command;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/28/21:18
 * @Description:用户登录
 */
@Data
public class UserLogin extends BaseCommand{
    @Protobuf(fieldType = FieldType.INT32, order=1, required = true)
    int serviceCode =1001;
    @Protobuf(fieldType = FieldType.STRING, order=2, required = true)
    private String userName;
    @Protobuf(fieldType = FieldType.STRING, order=3, required = true)
    private String passWord;
}
