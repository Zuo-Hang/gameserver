package com.example.gameservicedemo.util.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.text.DecimalFormat;
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
 * 将poi进行简单的封装，通过注解和反射，将excel中的集合和实体的set方法对应，形成执行集，再将执行集进行遍历，对bean进行封装，转化为
 * 主要支持任意实体，
 * 支持excel文件中字段的不规则排序
 */
@Slf4j
public class ReadExcelByEntity<T>{
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
            FileInputStream fileInputStream = new FileInputStream(filepath);
            if(".xls".equals(lastName)){
                wb = new HSSFWorkbook(fileInputStream);
            }else if(".xlsx".equals(lastName)){
                wb = new XSSFWorkbook(fileInputStream);
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
            //泛型方法集
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

    public Map<Integer,T> getMap() {
        setEntityMap();//----------------------------------------------------------------------------------------------
        try {

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return map;
    }

    /**
     *
     * 将excel数据内容填充到map
     * throws Exception
     */
    private void setEntityMap() {
        this.map = new HashMap<>();
        T t;
        List<String> invokeList = setInvokeList();
        sheet = wb.getSheetAt(0);
        //总行数
        int rowNum = sheet.getLastRowNum();
        row = sheet.getRow(0);
        //得到每一行单元格个数，包括其中的空单元格，这里要求表格内容的每一行都是固定的个数
        int colNum = row.getLastCellNum();  //每行单元格总数


        for (int i = 1; i <= rowNum; i++) {   //从第二行开始，遍历每一行----------------------------------------------------------------------------
            row = sheet.getRow(i);
            t = exchangeEntity(invokeList, colNum);//------------------------------------------------------------------------------
            map.put(i-1, t);  //将封装好的实体放入map
        }
    }


    private T exchangeEntity(List<String> invokeList, int colNum){
        T t ;
        try {
            DecimalFormat df = new DecimalFormat("#");          // 对长数字段进行string的转化
            String methodName;
            int j = 0;
            t = tClass.newInstance();  //每次新建一个T目标对象，下面对每个属性进行赋值
            while (j < colNum) {//设置值（以属性个数循环，获取每个单元格的值，再赋给属性）
                Object obj = getCellFormatValue(row.getCell(j));  //---------------------------------------------------------------------------
                Class clazz = typeList.get(j);
                methodName = invokeList.get(j);//----------------------------------------------------------------------------
                Method method = t.getClass().getMethod(methodName, typeList.get(j));
                // logger.debug("cell的数据 {}" , obj);
                if(obj == null || obj.equals("")){
                    try {
                        method.invoke(t, (Object) null); //Object转化，很关键
                    } catch (Exception e) {
                        return t;
                    }
                }else{
                    Object cast;
                    switch (clazz.getName()) {
                        case "java.lang.Integer": {
                            //形参需要的类型
                            if (obj.getClass().getName().equals("java.lang.Double")) {
                                String intNUm = df.format(obj);
                                cast = Integer.valueOf(intNUm);
                            } else {
                                cast = clazz.cast(obj); //可对类型进行扩展
                            }
                            break;
                        }
                        case "java.lang.Long" : {
                            String intNUm = df.format(obj);
                            cast = Long.valueOf(intNUm);
                            break;
                        }
                        case "java.lang.String": {
                            //在这里拦截“excel中读取为double,date,int,boolean”，实际的实体函数形参却是String类型的
                            if (obj.getClass().getName().equals("java.lang.Double")) {
                                cast = df.format(obj);
                            } else {
                                cast = String.valueOf(obj);
                            }
                            break;
                        }
                        default: {
                            cast = clazz.cast(obj);
                            break;
                        }
                    }
                    //logger.debug("cell中的数据"+cast);
                    method.invoke(t, cast);
                }
                j++;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
            //return t;//有异常返回一个空t
        }
        return t;
    }




    /**
     * 将标题头和实体的属性set方法对应上,形成一个执行函数集list,之后调用这个函数时，每次遍历list，然后invoke即可
     *throws Exception
     */
    private List<String> setInvokeList() {
        if(wb==null){
//            throw new Exception("Workbook对象为空！");
        }
        List<String> invokeList = new ArrayList<>();

        List<String> readExcelTitle = readExcelTitle();

        StringBuffer sb;
        //System.out.println("setInvokeList的判断"+readExcelTitle.size()+"?==?"+mapByAno.size());
        if(readExcelTitle.size()!=mapByAno.size()){
            log.error(" excel表头跟注解数量不对应");
            return null;
        }else{
            for (String obj : readExcelTitle) {
                if(mapByAno.get(obj)==null){
                    log.error(" excel表头跟注解名称不对应");
                    return null;
                }
                String fieldName = mapByAno.get(obj);
                mapByAno.remove(obj, fieldName);			//每拿一个put掉一个
                sb = new StringBuffer("set");
                String method = sb.append(fieldName.substring(0, 1).toUpperCase())
                        .append(fieldName.substring(1)).toString();
                invokeList.add(method);
            }
            if(!mapByAno.isEmpty()){
                log.error(" excel表头跟注解名称不对应");
                return null;
            }
            return invokeList;
            /* */
        }


    }


    /**
     * 得到标题头,标题头一定要是string类型，否则报错
     *throws Exception
     */
    @SuppressWarnings("deprecation")
    private List<String> readExcelTitle() {
        if(wb == null){
//            throw new Exception(" workbook对象为空");
        }
        sheet = wb.getSheetAt(0);
        row = sheet.getRow(0);
        //标题总列数
        int colNum = row.getPhysicalNumberOfCells();  //去掉对空列的计数

        List<String> list = new ArrayList<>();
        for(int i = 0;i<colNum;i++){
            if(row.getCell(i)== null||"".equals(row.getCell(i).toString())){
                list.add(null);
            }else {
                //logger.debug("工具类中 getCellFormatValue(row.getCell(i) {}",getCellFormatValue(row.getCell(i)));
                list.add((String) getCellFormatValue(row.getCell(i)));
            }
        }
        return list;
    }

    @SuppressWarnings("deprecation")
    private Object getCellFormatValue(Cell cell) {
        DecimalFormat df = new DecimalFormat("#");          // 对长数字段进行string的转化
        Object cellValue ;
        if (cell != null) {
            // 判断当前Cell的Type
            //logger.info("当前的类型cell.getCellType()"+cell.getCellType());
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC: {
                    // 如果当前Cell的Type为NUMERIC
                    cellValue = (int) cell.getNumericCellValue();
                    //logger.debug("数字类型 "+cellValue);
                    break;
                }
                case Cell.CELL_TYPE_FORMULA: {
                    // 判断当前的cell是否为Date
                    if (DateUtil.isCellDateFormatted(cell)) {
                        cellValue = cell.getDateCellValue();
                    } else {// 如果是纯数字
                        // 取得当前Cell的数值
                        cellValue = cell.getNumericCellValue();
                            /*if(cellValue.getClass().getName().equals("java.lang.Double")){
                                cellValue = df.format(cellValue);
                            }如果在此处拦截double就会使得所有的double都变成string，而有的时候我并不需要转成string */
                    }
                    break;
                }
                case Cell.CELL_TYPE_STRING: {
                    // 如果当前Cell的Type为STRING
                    // 取得当前的Cell字符串
                    cellValue = cell.getRichStringCellValue().getString();
                    // logger.debug("字符类型 "+cellValue);
                    break;
                }
                default: {
                    //  logger.debug("默认"); // 默认的Cell值
                    cellValue = "";

                }
            }
        } else {
            cellValue = "";
        }
        return cellValue;
    }






}
