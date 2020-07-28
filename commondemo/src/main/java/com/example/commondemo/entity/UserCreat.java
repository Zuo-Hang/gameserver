package com.example.commondemo.entity;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/28/9:31
 * @Description:
 */
public class UserCreat {
    @Protobuf(fieldType = FieldType.STRING, order=1, required = true)
    private String commend ="user_create";
    @Protobuf(fieldType = FieldType.STRING, order=2, required = true)
    private String nickName;
    @Protobuf(fieldType = FieldType.STRING, order=3, required = true)
    private String password;
    @Protobuf(fieldType = FieldType.STRING, order=4, required = true)
    private String phoneNumber;

    public String getCommend() {
        return commend;
    }

    public void setCommend(String commend) {
        this.commend = commend;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "UserCreat{" +
                "commend='" + commend + '\'' +
                ", nickName='" + nickName + '\'' +
                ", password='" + password + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
