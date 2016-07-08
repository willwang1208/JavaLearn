package org.whb.springmvc.listener;

import java.io.IOException;

import javax.servlet.ServletContextEvent;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.scripting.groovy.GroovyScriptFactory;
import org.springframework.scripting.support.ScriptFactoryPostProcessor;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.whb.springmvc.util.StackTraceUtil;

/**
 * 上下文加载监听
 * 除了@Configuration中的@Bean配置外，也可以在这里获取ApplicationContext注册其他Bean
 * 例如：这里实现了扫描载入groovy脚本的方法
 * @author 
 *
 */
public class HelloWorldContextLoaderListener extends ContextLoaderListener {
    
    public static final String PACKAGE = "org.whb";

    public HelloWorldContextLoaderListener() {
        super();
    }

    public HelloWorldContextLoaderListener(WebApplicationContext context) {
        super(context);
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        super.contextInitialized(event);
        StackTraceUtil.getLocation();

        ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
        try {
            loadGroovyScripts((DefaultListableBeanFactory) context.getAutowireCapableBeanFactory(), PACKAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        super.contextDestroyed(event);
        StackTraceUtil.getLocation();
    }

    /**
     * 扫描载入groovy脚本
     * 需要 @Bean ScriptFactoryPostProcessor {@link org.whb.springmvc.config.ServiceConfiguration}
     * 
     * @param beanFactory
     * @param basePackage
     * @throws IOException
     */
    protected void loadGroovyScripts(DefaultListableBeanFactory beanFactory, String basePackage) throws IOException {
        String resourcePath = ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage));
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resourcePath + "/**/*.groovy";
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources(packageSearchPath);
        for (Resource resource : resources) {
            if (resource.isReadable()) {
                GenericBeanDefinition definition = new GenericBeanDefinition();
                // 脚本工厂类
                definition.setBeanClassName(GroovyScriptFactory.class.getName());
                // 刷新时间
                definition.setAttribute(ScriptFactoryPostProcessor.REFRESH_CHECK_DELAY_ATTRIBUTE, 500);
                // 语言脚本
                definition.setAttribute(ScriptFactoryPostProcessor.LANGUAGE_ATTRIBUTE, "groovy");
                // 文件目录
                definition.getConstructorArgumentValues().addIndexedArgumentValue(0,
                        "file:/" + resource.getFile().getPath());

                String beanName = resource.getFile().getName();

                beanName = beanName.substring(0, beanName.indexOf("."));

                beanFactory.registerBeanDefinition(beanName, definition);

                System.out.println("Groovy script: " + beanName);
            }
        }
    }
}
