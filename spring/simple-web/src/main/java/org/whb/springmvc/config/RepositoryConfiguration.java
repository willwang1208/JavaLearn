package org.whb.springmvc.config;

import java.beans.PropertyVetoException;
import java.util.Properties;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * Spring持久层相关配置。
 * 
 * <p>
 * 注解@ComponentScan配置了扫描组件的位置（包），排除了@Controller
 * </p>
 * <p>
 * 注解@EnableTransactionManagement中引用TransactionManagementConfigurationSelector， 导入了声明性事务相关bean支持
 * </p>
 * 
 * @author whb
 *
 */
@Configuration
@ComponentScan(
        value = { "org.whb.**.repository", "org.whb.**.controller" }, 
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Controller.class) 
        })
@EnableTransactionManagement
@MapperScan(basePackages = "org.whb.springmvc.repository.mapper")
public class RepositoryConfiguration implements TransactionManagementConfigurer{
    
    /**
     * 环境配置Environment，继承了PropertyResolver。
     * 也可以直接通过ApplicationContext实例的getEnvironment()方法获得。
     */
    @Autowired
    private Environment env;
    
    @Resource(name = "dbProperties")
    private Properties properties;

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
    
    /**
     * dbcp数据源
     * @return
     */
    @Bean(name = "dbcp", destroyMethod = "close")
    public DataSource dbcpDataSource() {
        BasicDataSource source = new BasicDataSource();
        source.setDriverClassName(properties.getProperty("jdbc.dbcp.driver"));
        source.setUrl(properties.getProperty("jdbc.dbcp.url"));
        source.setUsername(properties.getProperty("jdbc.dbcp.username"));
        source.setPassword(properties.getProperty("jdbc.dbcp.password"));
        return source;
    }
    
    /**
     * c3p0数据源
     * @return
     */
    @Bean(name = "c3p0", destroyMethod = "close")
    public DataSource c3p0DataSource()  {
        ComboPooledDataSource source = new ComboPooledDataSource();
        try {
            source.setDriverClass(env.getRequiredProperty("jdbc.c3p0.driver"));
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        source.setJdbcUrl(env.getRequiredProperty("jdbc.c3p0.url"));
        source.setUser(env.getRequiredProperty("jdbc.c3p0.username"));
        source.setPassword(env.getRequiredProperty("jdbc.c3p0.password"));
        
        source.setInitialPoolSize(env.getProperty("jdbc.c3p0.InitialPoolSize", Integer.class, 4));
        source.setMaxPoolSize(env.getProperty("jdbc.c3p0.MaxPoolSize", Integer.class, 16));
        source.setMinPoolSize(env.getProperty("jdbc.c3p0.MinPoolSize", Integer.class, 4));
        source.setMaxIdleTime(env.getProperty("jdbc.c3p0.MaxIdleTime", Integer.class, 60));
        source.setAcquireIncrement(env.getProperty("jdbc.c3p0.AcquireIncrement", Integer.class, 2));
        source.setMaxStatements(env.getProperty("jdbc.c3p0.MaxStatements", Integer.class, 0));
        source.setIdleConnectionTestPeriod(env.getProperty("jdbc.c3p0.IdleConnectionTestPeriod", Integer.class, 1800));
        source.setAcquireRetryAttempts(env.getProperty("jdbc.c3p0.AcquireRetryAttempts", Integer.class, 10));
        source.setBreakAfterAcquireFailure(env.getProperty("jdbc.c3p0.BreakAfterAcquireFailure", Boolean.class, false));
        source.setTestConnectionOnCheckout(env.getProperty("jdbc.c3p0.TestConnectionOnCheckout", Boolean.class, false));
        
        return source;
    }
    
    /**
     * Mybatis的SqlSessionFactory
     * @return
     * @throws Exception
     */
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactoryBean() throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(c3p0DataSource());
        bean.setTypeAliasesPackage("org.whb.springmvc.repository.domain");
        
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        
        //mybatis全局配置文件
//        bean.setConfigLocation(resolver.getResource("classpath:META-INF/mybatis/mybatis-config.xml"));
        
        //mybatis映射文件
//        bean.setMapperLocations(resolver.getResources("classpath:META-INF/mybatis/mappers/**/*.xml"));
        bean.setMapperLocations(resolver.getResources("classpath:META-INF/mybatis/mappers/*.xml"));
        
        return bean.getObject();
    }

    /**
     * Mybatis的SqlSessionTemplate
     * 通过@Autowired将当前Configuration中的Bean以参数形式注入
     * @param sqlSessionFactory
     * @return
     */
    @Bean
    @Autowired
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    /**
     * 事务管理器
     * 通过直接调用当前Configuration中的方法注入Bean，该Bean不会重复构造
     */
    @Bean
    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return new DataSourceTransactionManager(c3p0DataSource());
    }
    
//    @Bean
//    public AnnotationSessionFactoryBean hibernateSessionFactory() {
//        AnnotationSessionFactoryBean sessionFactoryBean = new AnnotationSessionFactoryBean();
//        sessionFactoryBean.setDataSource(c3p0DataSource());
//        sessionFactoryBean.setNamingStrategy(new ImprovedNamingStrategy());
//        sessionFactoryBean.setPackagesToScan("org.whb.springmvc.repository.domain");
//        sessionFactoryBean.setHibernateProperties(properties);
//        return sessionFactoryBean;
//    }
//    
//    @Bean
//    public HibernateTransactionManager transactionManager() {
//        HibernateTransactionManager hibernateTransactionManager = new HibernateTransactionManager();
//        hibernateTransactionManager.setSessionFactory(sessionFactory().getObject());
//        return hibernateTransactionManager;
//    }

}
