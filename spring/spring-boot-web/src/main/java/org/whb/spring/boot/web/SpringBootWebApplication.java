package org.whb.spring.boot.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
/**
 * 应用启动类
 * <p>
 * 注解 @SpringBootApplication 包含@Configuration、@EnableAutoConfiguration、@ComponentScan的作用
 * </p>
 * <p>
 * 如果不使用 DataSource，可以重新设置注解 @EnableAutoConfiguration 排除 DataSourceAutoConfiguration
 * @EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
 * </p>
 * 
 * 打包时使用Spring Boot为Maven和Gradle提供的插件来生成包含各种依赖的Jar包，以后直接运行java -jar即可
 * 
 * <p>
 * 默认从classpath下的/config目录或者classpath的根目录查找application.properties或application.yml进行加载
 * 
 * 也可以在运行开始时加载指定的配置文件
 *    java -jar  ***.jar  --spring.config.location=classpath:/myproject.properties
 *    java -jar  ***.jar  --spring.config.location=classpath:/default.properties,classpath:/override.properties
 *    java -jar  ***.jar  --spring.config.location=file:application.properties
 *    java -jar  ***.jar  --spring.config.location=file:/opt/server/application.properties
 *    java -jar  app.jar  --name="Spring" --server.port=9090
 *    
 * 也可以先加载application.properties，而后用application-dev.properties增量替换
 *    java -jar  ***.jar  --spring.config.location=classpath:/default.properties --spring.profiles.active=dev
 *    
 * 也可以在运行开始时通过传入参数改变一些属性值
 *    java -jar  app.jar  --name="Spring" --server.port=9090
 * </p>
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
