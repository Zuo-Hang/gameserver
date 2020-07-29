package com.example.commondemo.entity;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/23/17:37
 * @Description:消息实体类,服务端返回的消息
 */
@Data
public class Message {
    /**
     * 命令执行结果码
     */
    @Protobuf(fieldType = FieldType.INT32, order=1, required = true)
    int requestCode;
    /**
     * 返回的显示结果
     */
    @Protobuf(fieldType = FieldType.STRING, order=2, required = true)
    String message;

    public Message(int requestCode, String message) {
        this.requestCode = requestCode;
        this.message = message;
    }
    public Message(){}
}
