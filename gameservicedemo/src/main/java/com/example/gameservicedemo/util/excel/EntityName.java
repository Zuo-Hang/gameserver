package com.example.gameservicedemo.util.excel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/10/15:39
 * @Description: 自定义注解，用于运行时反射
 */
@Target({FIELD})//使用在属性上
@Retention(RetentionPolicy.RUNTIME)//指定注解在运行时有效
public @interface EntityName {
	
	 /** 
     * 是否为序列号
     */  
    boolean id() default false;  
	/**
	 * 字段名称
	 */
	String column()default "";
	/** 
     * 排序字段
     */  
    Class clazz()default String.class;
}
