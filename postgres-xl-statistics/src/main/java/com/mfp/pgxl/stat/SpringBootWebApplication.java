package com.mfp.pgxl.stat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
/**
 * 应用层：spring-boot
 * 展示层：thymeleaf bootstrap echarts
 * 数据库：sqlite postgres-xl
 * 数据库连接池：druid c3p0
 * 工具：guava fastjson
 * 
 * 启动脚本见：resource/shell/pgxls.sh
 * 
 * 
 * @author whb
 *
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class SpringBootWebApplication {

    /**
     * 接收运行时参数args，传递给SpringApplication.run()方法，启动内嵌应用服务器等待处理请求
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(SpringBootWebApplication.class, args);
    }

}
