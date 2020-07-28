package com.example.commondemo.entity;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/07/28/14:49
 * @Description:传输协议的定义
 *  |------------|-----------|----------|-----------|-----------|-----------|
 *  |   header   | classLen  |    len   | className |  data     |  tail     |
 *  |------------|-----------|----------|-----------|-----------|-----------|
 */
public class TcpProtocol {
    /**
     * 开始
     */
    private final byte header=0x58;
    /**
     * 类名长度
     */
    private byte classLen;
    /**
     * data长度
     */
    private int len;
    /**
     * 类名
     */
    private byte[] className;
    /**
     * 数据
     */
    private byte [] data;
    /**
     * 结束标志
     */
    private final byte tail=0x63;

    public byte getHeader() {
        return header;
    }

    public byte getClassLen() {
        return classLen;
    }

    public void setClassLen(byte classLen) {
        this.classLen = classLen;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public byte[] getClassName() {
        return className;
    }

    public void setClassName(byte[] className) {
        this.className = className;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte getTail() {
        return tail;
    }
}
