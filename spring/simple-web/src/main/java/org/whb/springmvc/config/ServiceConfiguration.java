package org.whb.springmvc.config;

import java.io.IOException;
import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import org.springframework.remoting.rmi.RmiServiceExporter;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scripting.support.ScriptFactoryPostProcessor;
import org.springframework.stereotype.Controller;
import org.whb.springmvc.interceptor.HelloWorldAsyncUncaughtExceptionHandler;
import org.whb.springmvc.service.IHelloWorldService;

/**
 * Spring上下文配置。
 * 
 * <p>
 * 注解@ComponentScan配置了扫描组件的位置（包），排除了@Controller
 * </p>
 * <p>
 * 注解@EnableAsync中引用AsyncConfigurationSelector，导入了异步执行相关bean支持（不是异步Servlet）。
 * 可以（不是必须）实现AsyncConfigurer接口来声明异步执行器和异常处理器
 * </p>
 * <p>
 * 注解@EnableCaching中引用CachingConfigurationSelector，导入了缓存相关bean支持，
 * 必须定义@Bean CacheManager，可以通过实现CachingConfigurer接口来完成。
 * </p>
 * <p>
 * 注解@Import导入了 PropertiesConfiguration、RepositoryConfiguration，这样通过AnnotationConfigWebApplicationContext.register时，
 * 只需要注册ServiceConfiguration即可。
 * </p>
 * <p>
 * 注解@EnableScheduling启用定时任务相关支持，通过给任务方法增加@Scheduled注解实现调度
 * </p>
 * 
 * @author whb
 *
 */
@Configuration
@ComponentScan(
        value = { "org.whb.**.service", "org.whb.**.controller" }, 
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Controller.class) 
        })
@EnableAsync
@EnableCaching(proxyTargetClass = true)
@EnableScheduling
@Import(value = { PropertiesConfiguration.class, RepositoryConfiguration.class })
public class ServiceConfiguration implements AsyncConfigurer{
    
    /**
     * 用于支持Groovy脚本
     * 
     * <bean class="org.springframework.scripting.support.ScriptFactoryPostProcessor"/>
     * 
     * @return
     */
    @Bean
    public ScriptFactoryPostProcessor scriptFactoryPostProcessor() {
        return new ScriptFactoryPostProcessor();
    }

    /**
     * AsyncConfigurer接口方法：异步执行器
     */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(10);
        executor.initialize();
        return executor;
    }

    /**
     * AsyncConfigurer接口方法：异常处理器
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new HelloWorldAsyncUncaughtExceptionHandler();
    }

    /**
     * 缓存管理器，用于支持缓存的实现
     * @return
     */
    @Bean
//    @Override
    public CacheManager cacheManager() {
        try {
            net.sf.ehcache.CacheManager ehcache
                    = new net.sf.ehcache.CacheManager(new ClassPathResource("META-INF/ehcache.xml").getInputStream());
            EhCacheCacheManager cacheManager = new EhCacheCacheManager(ehcache);
            return cacheManager;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /*@Bean
    @Override
    public CacheResolver cacheResolver() {
        return null;
    }

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return null;
    }

    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
        return new HelloWorldCacheErrorHandler();
    }*/

    /**
     * 将本地服务开放给外部调用，实现接口 IHelloWorldService 的远程方法调用。见 TestClient.rmiClient()
     * rmi://123.123.123.123:21001/Hello
     * @param helloWorldService
     * @return
     */
    @Bean
    @Autowired
    public RmiServiceExporter rmiServiceExporter(IHelloWorldService helloWorldService) {
       RmiServiceExporter rse = new RmiServiceExporter();
       rse.setServiceName("Hello");
       rse.setRegistryPort(21001);
       rse.setServiceInterface(IHelloWorldService.class);
       rse.setService(helloWorldService);
       return rse;
    }
    
    /**
     * 创建接口 IHelloWorldService 的远程服务的本地代理。
     * @return
     */
    @Bean
    public RmiProxyFactoryBean rmiProxy() {
        RmiProxyFactoryBean rmiProxy = new RmiProxyFactoryBean();
//        rmiProxy.setServiceUrl("rmi://123.123.1.10:18802/Service");
        rmiProxy.setServiceUrl("rmi://localhost:21001/Hello");
        rmiProxy.setServiceInterface(IHelloWorldService.class);
        rmiProxy.setLookupStubOnStartup(false);
        return rmiProxy;
    }

}
