package com.example.gameservicedemo.util.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/10/15:39
 * @Description: 按照实体类读取Excel中的数据并封装成实体类对象
 */
@Slf4j
public class ReadExcelByEntity {
    /** 根据poi的定义，每个Excel文件都会被拆解成一个Workbook对象*/
    private Workbook wb;
    /** Excel的每一页被拆解成一个Sheet对象*/
    private Sheet sheet;
    /** Excel中的一行*/
    private Row row;
    /** 最终结果集*/
    private Map<Integer, T> map;
    /** 泛型类型*/
    private Class<T> tClass;
    /** 转化类型*/
    private List<Class> typeList;
    private Map<String,String> mapByAno=new HashMap<>();

    /**
     * 构造工具类
     */
    @SuppressWarnings("unchecked")
    public ReadExcelByEntity(String filepath) {
        if(filepath == null){
            log.error("Excel文件名为空");
            return;
        }
        //判断文件类型，生成相应的WorkBook
        String lastName = filepath.substring(filepath.lastIndexOf("."));
        try {
            ClassPathResource resource = new ClassPathResource(filepath);
            InputStream is = resource.getInputStream();
            if(".xls".equals(lastName)){
                wb = new HSSFWorkbook(is);
            }else if(".xlsx".equals(lastName)){
                wb = new XSSFWorkbook(is);
            }else{
                wb = null;
            }
        } catch (FileNotFoundException e) {
            log.error("文件找不到FileNotFoundException", e);
        } catch (IOException e) {
            log.error("IOException", e);
        }

        tClass = (Class<T>)((ParameterizedType)this.getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
        try {
            @SuppressWarnings("unused")
            T t = tClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        if(this.tClass != null){
            //反射获取类的属性
            Field[] fields = tClass.getDeclaredFields();
            typeList = new ArrayList<>();
            for(Field f: fields){
                //设置强制访问
                f.setAccessible(true);
                EntityName annotation = f.getAnnotation(EntityName.class);
                if(annotation != null && !annotation.id()){
                    //对true的字段进行拦截
                    mapByAno.put(annotation.column(),f.getName());
                    typeList.add(f.getType());
                }
            }
        }
    }
}
