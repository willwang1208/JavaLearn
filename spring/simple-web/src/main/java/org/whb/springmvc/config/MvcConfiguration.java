package org.whb.springmvc.config;

import java.util.Locale;
import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
import org.springframework.web.servlet.view.groovy.GroovyMarkupConfigurer;
import org.springframework.web.servlet.view.groovy.GroovyMarkupView;
import org.springframework.web.servlet.view.groovy.GroovyMarkupViewResolver;
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;
import org.springframework.web.servlet.view.velocity.VelocityView;
import org.springframework.web.servlet.view.velocity.VelocityViewResolver;
import org.whb.springmvc.interceptor.HelloWorldCallableProcessingInterceptor;
import org.whb.springmvc.interceptor.HelloWorldDeferredResultProcessingInterceptor;

/**
 * MVC配置
 * 
 * 注解@EnableWebMvc中引用{@code DelegatingWebMvcConfiguration}，直接加上@EnableWebMvc即可导入MVC需要的相关bean;
 *    如果不使用@EnableWebMvc也可以通过继承{@code WebMvcConfigurationSupport}来实现，这样可以Override一些方法
 * 注解@ComponentScan配置了扫描组件的位置（包），自动加载其中的@Controller
 * 
 * @author whb
 *
 */
@Configuration
@ComponentScan(
        basePackages = {"org.whb.springmvc.controller"}, 
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Controller.class)
        })
//@EnableWebMvc
public class MvcConfiguration extends WebMvcConfigurationSupport{

    /**
     * jsp视图
     * 由于InternalResourceViewResolver继承UrlBasedViewResolver，所以他的Order必须在最后
     * @see org.springframework.web.servlet.view.UrlBasedViewResolver note
     * 
        <bean id="viewResolverCommon" class="org.springframework.web.servlet.view.InternalResourceViewResolver">
            <property name="viewClass" value="org.springframework.web.servlet.view.InternalResourceView" />
            <property name="prefix" value="/WEB-INF/view/jsp/"/>  
            <property name="suffix" value=".jsp"/><!--可为空,方便实现自已的依据扩展名来选择视图解释类的逻辑  -->
            <property name="order" value="100"/>
        </bean>
     * @return
     */
    @Bean(name = "internalResourceViewResolver")
    public InternalResourceViewResolver internalResourceViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(InternalResourceView.class);
        viewResolver.setPrefix("/WEB-INF/view/jsp/");
        viewResolver.setSuffix(".jsp");
        viewResolver.setOrder(100);  //InternalResourceViewResolver必须在最后，否则其他ViewResolver不起作用
        return viewResolver;
    }
    
    /**
     * freemarker视图
     * 
        <!-- Freemarker 视图解释器，依赖freemarker.jar -->
        <bean id="viewResolverFtl" class="org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver">
            <property name="viewClass" value="org.springframework.web.servlet.view.freemarker.FreeMarkerView"/>
            <property name="contentType" value="text/html;charset=UTF-8"></property>
            <property name="cache" value="true" />
            <property name="suffix" value=".ftl" />
            <property name="order" value="1"/>
            <!-- 宏命令的支持    -->
            <property name="exposeSpringMacroHelpers" value="true"/>
            <property name="requestContextAttribute" value="rc" />
        </bean>
        <!-- Freemarker配置，依赖freemarker.jar -->
        <bean id="freemarkerConfig" class="org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer">
            <property name="templateLoaderPath" value="/WEB-INF/view/freemarker/" />
            <property name="freemarkerSettings">
                <props>
                    <prop key="template_update_delay">0</prop>
                    <prop key="default_encoding">UTF-8</prop>
                    <prop key="number_format">0.##########</prop>
                    <prop key="datetime_format">yyyy-MM-dd HH:mm:ss</prop>
                    <prop key="classic_compatible">true</prop>
                    <prop key="template_exception_handler">ignore</prop>
                    <prop key="locale">zh_CN</prop>
                </props>
            </property>
        </bean>
     * @return
     */
    @Bean
    public FreeMarkerViewResolver freeMarkerViewResolver() {
        FreeMarkerViewResolver viewResolver = new FreeMarkerViewResolver();
        viewResolver.setViewClass(FreeMarkerView.class);
        viewResolver.setContentType("text/html;charset=UTF-8");
        viewResolver.setPrefix("");
        viewResolver.setSuffix(".ftl");
        viewResolver.setOrder(10);
        viewResolver.setCache(true);
        viewResolver.setExposeSpringMacroHelpers(true);
        viewResolver.setRequestContextAttribute("rc");
        return viewResolver;
    }
    @Bean
    public FreeMarkerConfigurer freeMarkerConfigurer() {
        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
        configurer.setTemplateLoaderPath("/WEB-INF/view/freemarker/");
        Properties settings = new Properties();
        settings.setProperty("default_encoding", "UTF-8");
        settings.setProperty("locale", "zh_CN");
        configurer.setFreemarkerSettings(settings);
        return configurer;
    }
    
    /**
     * groovy视图
     * 
     * @return
     */
    @Bean
    public GroovyMarkupViewResolver groovyMarkupViewResolver() {
        GroovyMarkupViewResolver viewResolver = new GroovyMarkupViewResolver();
        viewResolver.setViewClass(GroovyMarkupView.class);
        viewResolver.setPrefix("");
        viewResolver.setSuffix(".gtl");
        viewResolver.setOrder(11);
        return viewResolver;
    }
    @Bean
    public GroovyMarkupConfigurer groovyMarkupConfigurer() {
        GroovyMarkupConfigurer configurer = new GroovyMarkupConfigurer();
        configurer.setResourceLoaderPath("/WEB-INF/view/groovy/");
//        configurer.setResourceLoaderPath("classpath:view/");
        return configurer;
    }
    
    /**
     * velocity视图
     * <mvc:velocity-configurer resource-loader-path="/WEB-INF/vm/"/>
           <mvc:view-resolvers>
           <mvc:velocity cache-views="false" suffix=".vm"/>
       </mvc:view-resolvers>
     * @return
     */
    @Bean
    public VelocityViewResolver velocityViewResolver() {
        VelocityViewResolver viewResolver = new VelocityViewResolver();
        viewResolver.setViewClass(VelocityView.class);
        viewResolver.setPrefix("");
        viewResolver.setSuffix(".vm");
        viewResolver.setOrder(12);
        return viewResolver;
    }
    @Bean
    public VelocityConfigurer velocityConfigurer() {
        VelocityConfigurer configurer = new VelocityConfigurer();
        configurer.setResourceLoaderPath("/WEB-INF/view/velocity/");
//        configurer.setResourceLoaderPath("classpath:view/");
        return configurer;
    }
    
    /**
     * 上传文件解析器，依赖commons-fileupload，name必须是multipartResolver
     * 
        <bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
            <property name="defaultEncoding" value="utf-8"></property>
            <property name="maxUploadSize" value="5242880"></property>
            <property name="maxInMemorySize" value="40960"></property>
        </bean>
     * 
     * @return
     */
    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver commonsMultipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("UTF-8");
        resolver.setMaxUploadSize(5242880);
        resolver.setMaxInMemorySize(40960);
//        resolver.setMaxUploadSizePerFile(5242880);
//        resolver.setUploadTempDir();
        return resolver;
    }
    
    /**
     * 按Cookie解析区域的解析器
     * 此外还有 AcceptHeaderLocaleResolver、SessionLocaleResolver、FixedLocaleResolver
     * 获取Local: Locale locale = LocaleContextHolder.getLocale();
     * @return
     */
    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
        cookieLocaleResolver.setCookieName("language");
        cookieLocaleResolver.setCookieMaxAge(-1);
        cookieLocaleResolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        return cookieLocaleResolver;
    }
    
    /**
     * Override：开启异步Servlet支持
     */
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setDefaultTimeout(10000);
        configurer.setTaskExecutor(new SimpleAsyncTaskExecutor());
        configurer.registerCallableInterceptors(new HelloWorldCallableProcessingInterceptor());
        configurer.registerDeferredResultInterceptors(new HelloWorldDeferredResultProcessingInterceptor());
    }
    
    /**
     * Override：注册ResourceHandler
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        /**
         * <mvc:resources mapping="/static/**" location="/WEB-INF/static/"/>
         */
        registry.addResourceHandler("/static/**").addResourceLocations("/WEB-INF/static/");
    }
    
    /**
     * Override：注册Interceptor
     */
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("language");
        registry.addInterceptor(localeChangeInterceptor);
    }
    
//  @Bean
//  public DefaultServletHttpRequestHandler createDefaultServletHttpRequestHandler() {
//      return new DefaultServletHttpRequestHandler();
//  }
  
//  @Override
//  protected Validator getValidator() {
//      LocalValidatorFactoryBean localValidatorFactoryBean =
//              new LocalValidatorFactoryBean();
//      localValidatorFactoryBean.setProviderClass(HibernateValidator.class);
//      localValidatorFactoryBean.setValidationMessageSource(messageSource());
//      return localValidatorFactoryBean;
//  }
}
