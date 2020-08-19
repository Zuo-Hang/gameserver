package com.example.gameservicedemo.bean;

import com.example.gameservicedemo.util.excel.EntityName;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/17/16:22
 * @Description: buffer实体类 表示某状态的持续
 */
@Data
public class Buffer {

    @EntityName(column = "id")
    private Integer id;

    @EntityName(column = "名字")
    private String name;

    @EntityName(column = "类型")
    private Integer type;

    @EntityName(column = "能否覆盖")
    private Integer cover;

    @EntityName(column = "hp效果")
    private Integer hp;

    @EntityName(column = "mp效果")
    private Integer mp;

    @EntityName(column = "效果")
    private Integer effect;

    @EntityName(column = "持续时间")
    private Integer duration;

    @EntityName(column = "间隔时间")
    private Integer intervalTime;

    @EntityName(column = "次数")
    private Integer times;
    @EntityName(column = "描述")
    private String describe;
    @EntityName(column = "cd")
    private Integer cd;


    private long startTime;


}
