package com.example.commondemo.entity;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hang hang
 * @Date: 2020/07/23/17:37
 * @Description:消息实体类 用于netty通信的消息格式
 *
 * 服务器和客户端通信，要基于一定的格式，这个格式成为通信协议。我们的通信协议是这样的，下面的内容按顺序排列：
 *     1.一个字节：0xFF ，是一个开始标志
 *     2.四个字节：消息长度，Netty需要这个字段处理TCP粘包拆包问题
 *     3.四个字节：msgId，服务端根据这个字节分发请求
 *     4.一个字节：type，表示消息的类型，1代表JSON格式
 *     5.一个字节：flag，表示消息处理是否成功（1表示成功，0表示失败）
 *     6.其余字节：消息内容
 *
 */
public class Message {

}
