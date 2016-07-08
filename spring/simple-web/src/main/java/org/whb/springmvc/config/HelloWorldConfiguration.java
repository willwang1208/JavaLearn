package org.whb.springmvc.config;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.whb.springmvc.entity.User;
import org.whb.springmvc.service.IHelloWorldService;
import org.whb.springmvc.service.impl.HelloWorldServiceImpl;

/**
 * HelloWorld配置
 * 
 * 一种新的替代XML文件方式的配置方式，通过注解@Configuration和@Bean实现，
 * 需要用AnnotationConfigWebApplicationContext加载来完成注册。
 * @link org.whb.springmvc.initializer.GlobalWebApplicationInitializer
 * 
 * 注解@Import导入其他Configuration
 * 
 * XML配置方式对应spring-helloworld.xml
 * 
 * @author whb
 *
 */
@Configuration
//@Import(value = {HelloWorldToBeImportedConfiguration.class, HelloWorldToBeImportedConfiguration.class})
@Import(value = HelloWorldToBeImportedConfiguration.class)
public class HelloWorldConfiguration {

    @Bean(name = "helloWorld_B_2", autowire = Autowire.BY_TYPE, initMethod = "postConstruct", destroyMethod = "preDestroy")
    public IHelloWorldService createHelloWorld2Service(){
        IHelloWorldService service = new HelloWorldServiceImpl();
        return service;
    }
    
    @Bean(name = "helloWorld_B_3", autowire = Autowire.BY_NAME)
    public IHelloWorldService createHelloWorld3Service(){
        IHelloWorldService service = new HelloWorldServiceImpl();
        return service;
    }
    
    @Bean(name = "helloWorld_B_4")
    public IHelloWorldService createHelloWorld4Service(){
        IHelloWorldService service = new HelloWorldServiceImpl();
        return service;
    }
    
    @Bean(name = "leaderUser")
    public User createLeader(){
        User leader = new User();
        leader.setName("only one leader");
        leader.setId(new Random().nextInt(1000));
        return leader;
    }
    
    @Bean
    @Scope("prototype")
    public User createFollower(){
        User follower = new User();
        follower.setName("a follower");
        follower.setId(new Random().nextInt(1000));
        return follower;
    }
}
