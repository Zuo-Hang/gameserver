package com.example.commondemo.base;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/28/14:49
 * @Description:传输协议的定义
 *  |------------|-----------|----------|
 *  |   len      |serviceCode|    data  |
 *  |------------|-----------|----------|
 */
@Data
public class TcpProtocol {
    /**
     * 包长度
     */
    private int len;
    /**
     * 类名
     */
    private int serviceCode;
    /**
     * 数据
     */
    private byte [] data;
}
