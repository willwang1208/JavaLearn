package org.whb.web.initializer;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.whb.web.listener.HelloWorldServletContextAttributeListener;
import org.whb.web.listener.HelloWorldServletContextListener;
import org.whb.web.util.StackTraceUtil;

/**
 * 在META-INF/services/javax.servlet.ServletContainerInitializer文件中加入ServletContainerInitializer的实现类（包名.类名）
 * 当容器启动时会自动调用他们的onStartup方法
 * 可以用来初始化ServletContext，动态注册Listener、Filter、Servlet
 * 
 * @author 
 *
 */
public class HelloWorldServletContainerInitializer implements ServletContainerInitializer {

    public void onStartup(Set<Class<?>> set, ServletContext sc) throws ServletException {
        StackTraceUtil.printLocation();
        
        // 动态注册**Listener
        sc.addListener("org.whb.web.initializer.HelloWorldServletContainerInitializer$InnerListener");
        sc.addListener(HelloWorldServletContextListener.class);
        sc.addListener(new HelloWorldServletContextAttributeListener());
        sc.addListener("org.whb.web.listener.HelloWorldHttpSessionListener");
        sc.addListener("org.whb.web.listener.HelloWorldHttpSessionAttributeListener");
        sc.addListener("org.whb.web.listener.HelloWorldHttpSessionIdListener");
        sc.addListener("org.whb.web.listener.HelloWorldServletRequestListener");
        sc.addListener("org.whb.web.listener.HelloWorldServletRequestAttributeListener");
      
        // 动态注册Filter
        FilterRegistration.Dynamic filter = sc.addFilter("InnerFilter", InnerFilter.class);
        filter.addMappingForServletNames(
                EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD), 
                true, 
                new String[]{"InnerServlet_1", "InnerServlet_2"});
//        filter.addMappingForUrlPatterns(dispatcherTypes, isMatchAfter, urlPatterns);

        // 动态注册HttpServlet映射
        ServletRegistration.Dynamic servlet1 = sc.addServlet("InnerServlet_1", InnerServlet.class);
        servlet1.setLoadOnStartup(1);
        servlet1.addMapping("/inner/1");
        
        ServletRegistration.Dynamic servlet2 = sc.addServlet("InnerServlet_2", "org.whb.web.initializer.HelloWorldServletContainerInitializer$InnerServlet");
        servlet2.addMapping("/inner/2");
        
        sc.addServlet("InnerServlet_3", new InnerServlet());
        sc.getServletRegistrations().get("InnerServlet_3").addMapping("/inner/3");
        
        for(Class<?> clazz : set) {
            System.out.println("clazz.getName(): " + clazz.getName());
        }
    }
    
    public static class InnerListener implements ServletContextListener {

        public void contextInitialized(ServletContextEvent sce) {
            StackTraceUtil.printLocation();
        }

        public void contextDestroyed(ServletContextEvent sce) {
            StackTraceUtil.printLocation();
        }
    }

    public static class InnerFilter implements Filter {
        
        private String name;

        public void init(FilterConfig filterConfig) throws ServletException {
            StackTraceUtil.printLocation();
            name = filterConfig.getFilterName();
        }

        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            System.out.println("filter name: " + name + ". request uri: " 
                    + ((HttpServletRequest)request).getRequestURI() + StackTraceUtil.getLocation());
            chain.doFilter(request, response);
        }

        public void destroy() {
            StackTraceUtil.printLocation();
        }
    }
    
    public static class InnerServlet extends HttpServlet {

        private static final long serialVersionUID = 1L;

        @Override
        public void init() throws ServletException {
            super.init();
            StackTraceUtil.printLocation();
        }

        @Override
        public void destroy() {
            super.destroy();
            StackTraceUtil.printLocation();
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            StackTraceUtil.printLocation();
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            StackTraceUtil.printLocation();
        }
    }

}
