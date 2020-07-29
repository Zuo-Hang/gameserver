package com.example.commondemo.entity.command;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/29/17:51
 * @Description:展示所有的命令
 */
@Data
public class ShowCmd extends BaseCommand{
    @Protobuf(fieldType = FieldType.INT32, order=1, required = true)
    int serviceCode =100000;
}
