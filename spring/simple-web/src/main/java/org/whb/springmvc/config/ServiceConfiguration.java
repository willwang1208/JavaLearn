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
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scripting.support.ScriptFactoryPostProcessor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.whb.springmvc.interceptor.HelloWorldAsyncUncaughtExceptionHandler;

/**
 * Spring上下文配置
 * 
 * <p>
 * 注解@ComponentScan配置了扫描组件的位置（包），排除了@Controller
 * 
 * </p>
 * <p>
 * 注解@PropertySources和@PropertySource配置了资源文件，加载到Environment
 * </p>
 * <p>
 * 注解@EnableTransactionManagement中引用TransactionManagementConfigurationSelector， 导入了声明性事务相关bean支持
 * </p>
 * <p>
 * 注解@EnableAsync中引用AsyncConfigurationSelector，导入了异步执行相关bean支持（不是异步Servlet）。
 * 可以（不是必须）实现AsyncConfigurer接口来声明异步执行器和异常处理器
 * </p>
 * <p>
 * 注解@EnableCaching中引用CachingConfigurationSelector，导入了缓存相关bean支持，
 * 必须定义@Bean CacheManager，可以通过实现CachingConfigurer接口来完成。
 * 
 * </p>
 * @author whb
 *
 */
@Configuration
@ComponentScan(
        value = { "org.whb.**.service", "org.whb.**.repository", "org.whb.**.controller" }, 
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Controller.class) 
        })
@PropertySources(value = { 
        @PropertySource(value = "classpath:/datasource.properties", ignoreResourceNotFound = true)
        })
@EnableTransactionManagement
@EnableAsync
@EnableCaching(proxyTargetClass = true)
public class ServiceConfiguration implements AsyncConfigurer{
    
    /**
     * 环境配置Environment，继承了PropertyResolver。
     * 也可以直接通过ApplicationContext实例的getEnvironment()方法获得。
     */
    @Autowired
    private Environment env;

    /*@Bean
    public DataSource driverManagerDataSource() {
        org.springframework.jdbc.datasource.DriverManagerDataSource source = 
                new org.springframework.jdbc.datasource.DriverManagerDataSource();
        source.setDriverClassName(env.getRequiredProperty("jdbc.driver"));
        source.setUrl(env.getRequiredProperty("jdbc.url"));
        source.setUsername(env.getRequiredProperty("jdbc.username"));
        source.setPassword(env.getRequiredProperty("jdbc.password"));
        return source;
    }*/
    
    /*@Bean
    public DataSource dbcpDataSource() {
        BasicDataSource source = new BasicDataSource();
        source.setDriverClassName(env.getRequiredProperty("jdbc.driver"));
        source.setUrl(env.getRequiredProperty("jdbc.url"));
        source.setUsername(env.getRequiredProperty("jdbc.username"));
        source.setPassword(env.getRequiredProperty("jdbc.password"));
        return source;
    }*/
    
//    @Bean(destroyMethod = "close")
//    public DataSource c3p0DataSource() throws PropertyVetoException {
//        ComboPooledDataSource source = new ComboPooledDataSource();
//        source.setDriverClass(env.getRequiredProperty("jdbc.driver"));
//        source.setJdbcUrl(env.getRequiredProperty("jdbc.url"));
//        source.setUser(env.getRequiredProperty("jdbc.username"));
//        source.setPassword(env.getRequiredProperty("jdbc.password"));
//        
//        source.setInitialPoolSize(env.getProperty("jdbc.c3p0.InitialPoolSize", Integer.class, 4));
//        source.setMaxPoolSize(env.getProperty("jdbc.c3p0.MaxPoolSize", Integer.class, 16));
//        source.setMinPoolSize(env.getProperty("jdbc.c3p0.MinPoolSize", Integer.class, 4));
//        source.setMaxIdleTime(env.getProperty("jdbc.c3p0.MaxIdleTime", Integer.class, 60));
//        source.setAcquireIncrement(env.getProperty("jdbc.c3p0.AcquireIncrement", Integer.class, 2));
//        source.setMaxStatements(env.getProperty("jdbc.c3p0.MaxStatements", Integer.class, 0));
//        source.setIdleConnectionTestPeriod(env.getProperty("jdbc.c3p0.IdleConnectionTestPeriod", Integer.class, 1800));
//        source.setAcquireRetryAttempts(env.getProperty("jdbc.c3p0.AcquireRetryAttempts", Integer.class, 10));
//        source.setBreakAfterAcquireFailure(env.getProperty("jdbc.c3p0.BreakAfterAcquireFailure", Boolean.class, false));
//        source.setTestConnectionOnCheckout(env.getProperty("jdbc.c3p0.TestConnectionOnCheckout", Boolean.class, false));
//        
//        return source;
//    }

    /**
     * 用于支持Bean属性的占位符替换，如：在注解中使用占位符@Value("${db.driver}")
     * @return
     */
    @Bean
    public PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    
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

    
    
    
//    @Bean
//    public RmiProxyFactoryBean service() {
//        RmiProxyFactoryBean rmiProxy = new RmiProxyFactoryBean();
//        rmiProxy.setServiceUrl("rmi://127.0.1.1:1099/Service");
//        rmiProxy.setServiceInterface(RMIService.class);
//        rmiProxy.setLookupStubOnStartup(false);
//        return rmiProxy;
//    }

    /*@Bean
    public AnnotationSessionFactoryBean hibernateSessionFactory() {
        AnnotationSessionFactoryBean sessionFactoryBean = new AnnotationSessionFactoryBean();
        sessionFactoryBean.setDataSource(c3p0DataSource());
        sessionFactoryBean.setNamingStrategy(new ImprovedNamingStrategy());
        sessionFactoryBean.setPackagesToScan("septem.model");
        sessionFactoryBean.setHibernateProperties(hProps());
        return sessionFactoryBean;
    }*/
    
//    public SqlSessionFactoryBean mybatisSessionFactory() {
//        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
//        factory.setDataSource(c3p0DataSource());
//        factory.setMapperLocations(new Resou);
//        return factory;
//    }
    

//    @Bean
//    public HibernateTransactionManager transactionManager() {
//        HibernateTransactionManager hibernateTransactionManager = new HibernateTransactionManager();
//        hibernateTransactionManager.setSessionFactory(sessionFactory().getObject());
//        return hibernateTransactionManager;
//    }

//    private Properties hProps() {
//        Properties p = new Properties();
//        p.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
//        p.put("hibernate.cache.use_second_level_cache", "true");
//        p.put("hibernate.cache.use_query_cache", "true");
//        p.put("hibernate.cache.provider_class", "org.hibernate.cache.EhCacheProvider");
//        p.put("hibernate.cache.provider_configuration_file_resource_path", "ehcache.xml");
//        p.put("hibernate.show_sql", "true");
//        p.put("hibernate.hbm2ddl.auto", "update");
//        p.put("hibernate.generate_statistics", "true");
//        p.put("hibernate.cache.use_structured_entries", "true");
//        return p;
//    }
}
