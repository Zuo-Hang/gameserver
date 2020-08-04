package com.example.gamedatademo.config;

import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/03/15:07
 * @Description: mybatis的配置类
 */
@org.springframework.context.annotation.Configuration
@MapperScan(basePackages = {"com.example.gamedatademo.mapper"})
public class MyBatisConfig {
    @Bean
    public ConfigurationCustomizer configurationCustomizer(){
        return new ConfigurationCustomizer(){
            @Override
            public void customize(Configuration configuration) {
                //设置自动转成驼峰
                configuration.setMapUnderscoreToCamelCase(true);
            }
        };
    }
}
