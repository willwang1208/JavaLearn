package org.whb.springmvc.initializer;

import java.io.File;
import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.web.WebLoggerContextUtils;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.whb.springmvc.config.HelloWorldConfiguration;
import org.whb.springmvc.config.MvcConfiguration;
import org.whb.springmvc.config.ServiceConfiguration;
import org.whb.springmvc.listener.HelloWorldContextLoaderListener;

/**
 * 初始化@Configuration等，对应web.xml中的工作
 * 
 * spring webapp 初始化时调用onStartup方法
 * SpringServletContainerInitializer.onStartup时会找到所有WebApplicationInitializer，
 * 并根据@Order排序，然后依次执行他们的onStartup
 * 
 * @author whb
 * @see org.springframework.web.WebApplicationInitializer.SpringServletContainerInitializer
 */
public class GlobalWebApplicationInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        System.out.println(">> GlobalWebApplicationInitializer.onStartup");
        
        /**
         * context参数：日志配置，对应web.xml中的
         * 
            <context-param>
                <param-name>log4jConfiguration</param-name>
                <param-value>/WEB-INF/config/log4j2.xml</param-value>
            </context-param>
         * 
         * 由于加载顺序原因，不能通过setInitParameter完成，需要重新加载
         * 
         * @see org.apache.logging.log4j.web.Log4jServletContextListener
         * @see org.apache.logging.log4j.web.Log4jServletContainerInitializer
         */
//      servletContext.setInitParameter("log4jConfiguration", "/WEB-INF/config/log4j2.xml");
        File log4j2 = new File(servletContext.getRealPath("/WEB-INF/config/log4j2.xml"));
        LoggerContext loggerContext = WebLoggerContextUtils.getWebLoggerContext(servletContext);
        loggerContext.setConfigLocation(log4j2.toURI());
        
        /**
         * Spring上下文，对应web.xml中的
         * 
            <context-param>
                <param-name>contextConfigLocation</param-name>
                <param-value>
                    /WEB-INF/config/spring-helloworld.xml,
                    /WEB-INF/config/spring-common.xml,
                    /WEB-INF/config/spring-batch.xml
                </param-value>
            </context-param>
            
            <listener>
                <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
            </listener>
         */
        AnnotationConfigWebApplicationContext root = new AnnotationConfigWebApplicationContext();
        root.register(HelloWorldConfiguration.class, ServiceConfiguration.class);

        /** 设置Configuration后，注册监听ContextLoaderListener */
        /** 可以直接使用，也可以继承重写，通常只能有一个root content */
//      servletContext.addListener(new ContextLoaderListener(root));
        servletContext.addListener(new HelloWorldContextLoaderListener(root));
        
//      HelloWorldWebApplicationContext another_root = new HelloWorldWebApplicationContext();
//      servletContext.addListener(new ContextLoaderListener(another_root));
        
        /**
         * MVC配置，对应web.xml中的
         * 
            <servlet>
                <servlet-name>spring_dispatcher</servlet-name>
                <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
                <init-param>
                    <param-name>contextConfigLocation</param-name>
                    <param-value>/WEB-INF/config/spring-mvc.xml</param-value>
                </init-param>
                <load-on-startup>1</load-on-startup>
            </servlet>

            <servlet-mapping>
                <servlet-name>spring_dispatcher</servlet-name>
                <url-pattern>/mvc/*</url-pattern>
            </servlet-mapping>
            <servlet-mapping>
                <servlet-name>spring_dispatcher</servlet-name>
                <url-pattern>*.do</url-pattern>
            </servlet-mapping>
         */
        AnnotationConfigWebApplicationContext mvc = new AnnotationConfigWebApplicationContext();
        mvc.register(MvcConfiguration.class);

        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("spring_dispatcher", new DispatcherServlet(mvc));
        dispatcher.setLoadOnStartup(1);
        dispatcher.setAsyncSupported(true);    //开启异步支持
        dispatcher.addMapping("/mvc/*", "*.do");
        
        /*GroovyWebApplicationContext gmvc = new GroovyWebApplicationContext();
        ServletRegistration.Dynamic dispatcher_groovy = servletContext.addServlet("spring_dispatcher_groovy", new DispatcherServlet(gmvc));
        dispatcher_groovy.setLoadOnStartup(2);
        dispatcher_groovy.addMapping("/gmvc/*");*/
        

        /**
         * Filter: Hibernate OpenSessionInViewFilter
         */
        /*OpenSessionInViewFilter osivFilter = new OpenSessionInViewFilter();
        FilterRegistration.Dynamic hfr = servletContext.addFilter("hibernateOpenSessionInViewFilter", osivFilter);
        hfr.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE), false, "/");*/
          
        /**
         * Filter: CharacterEncodingFilter
         */
        CharacterEncodingFilter ceFilter = new CharacterEncodingFilter();
        ceFilter.setEncoding("UTF-8");
        FilterRegistration.Dynamic cefr = servletContext.addFilter("characterEncodingFilter", ceFilter);
        cefr.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/");
    }
}
