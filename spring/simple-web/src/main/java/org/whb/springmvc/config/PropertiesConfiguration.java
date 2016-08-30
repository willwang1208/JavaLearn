package org.whb.springmvc.config;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.ServletContextResource;

/**
 * 加载资源文件。
 * 
 * 同时通过实现ApplicationContextAware、ServletContextAware，分别注入了ApplicationContext、ServletContext实体
 * 
 * <p>
 * 通过注解@PropertySources和@PropertySource把资源文件加载到Environment
 * </p>
 * 
 * </p>
 * @author whb
 *
 */
@Configuration
@PropertySources(value = { 
        @PropertySource(value = "classpath:/notfound.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "WEB-INF/config/db.properties")
        })
public class PropertiesConfiguration implements ApplicationContextAware, ServletContextAware {
    
    private ApplicationContext applicationContext;

    private ServletContext servletContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
    
    /**
     * 用于支持Bean属性的占位符替换，如：在注解中使用占位符@Value("${db.driver}")
     * @return
     */
    @Bean
    public PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    
    /**
     * Properties实例，通过ServletContextResource加载
     * 可以通过 @Resource(name = "dbProperties")来注入
     * @return
     * @throws IOException
     */
    @Bean(name = "dbProperties")
    public Properties dbProperties() throws IOException {
        Resource resource = new ServletContextResource(servletContext, "/WEB-INF/config/db.properties");
        Properties props = PropertiesLoaderUtils.loadProperties(resource);
        return props;
    }
    
    /**
     * 另一个Properties实例，通过ClassPathResource加载
     * @return
     */
    @Bean(name = "hibernateProperties")
    public Properties hibernateProperties() {
        Resource resource = new ClassPathResource("/hibernate.properties");
        Properties props = null;
        try {
            props = PropertiesLoaderUtils.loadProperties(resource);
        } catch (IOException e) {
        }
        return props;
    }
    
    /**
     * 可不重启刷新的国际化资源文件，如messages.properties、messages_zh_CN.properties、messages_zh_TW.properties
     * 可以通过ApplicationContext.getMessage(...)取值，因为其实现了 MessageSource 接口
     * @return
     */
    @Bean(name = "messageSource")
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
//        messageSource.setBasenames("classpath:messsages", "classpath:org/hibernate/validator/ValidationMessages");
        messageSource.setBasenames("WEB-INF/resource/messsages");
        messageSource.setUseCodeAsDefaultMessage(false);
        messageSource.setDefaultEncoding(applicationContext.getEnvironment().getProperty("encoding", "UTF-8"));
        messageSource.setCacheSeconds(60);
        return messageSource;
    }
}
