package com.example.commondemo.entity;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/28/17:24
 * @Description:
 */
public class ReturnUser {
    @Protobuf(fieldType = FieldType.STRING, order=1, required = true)
    public String name;
    @Protobuf(fieldType = FieldType.INT32, order=2, required = true)
    public int id;
    @Protobuf(fieldType = FieldType.STRING, order=3, required = true)
    public List<String> stringList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getStringList() {
        return stringList;
    }

    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }

    @Override
    public String toString() {
        return "ReturnUser{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", stringList=" + stringList +
                '}';
    }
}
