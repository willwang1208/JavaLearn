package org.whb.springmvc.config;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
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
 * 通过注解@PropertySources和@PropertySource配置了资源文件，加载到Environment
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
    
    @SuppressWarnings("unused")
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
     * 使 @Resource(name = "dbProperties")
     * @return
     * @throws IOException
     */
    @Bean(name = "dbProperties")
    public Properties dbProperties() throws IOException {
        Resource resource = new ServletContextResource(servletContext, "/WEB-INF/config/db.properties");
        Properties props = PropertiesLoaderUtils.loadProperties(resource);
        return props;
    }
    
    @Bean(name = "hibernateProperties")
    public Properties testConfigInDev() {
        Resource resource = new ClassPathResource("/hibernate.properties");
        Properties props = null;
        try {
            props = PropertiesLoaderUtils.loadProperties(resource);
        } catch (IOException e) {
        }
        return props;
    }
    
}
