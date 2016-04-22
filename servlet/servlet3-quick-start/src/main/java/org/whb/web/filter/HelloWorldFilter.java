package org.whb.web.filter;

import java.io.IOException;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.whb.web.util.StackTraceUtil;

/**
 * 通过注解WebFilter注册Filter
 * @author 
 *
 */

@WebFilter(filterName = "HelloWorld", 
    urlPatterns="/hello-world", 
    dispatcherTypes = {DispatcherType.REQUEST, DispatcherType.FORWARD})
public class HelloWorldFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {
        StackTraceUtil.printLocation();
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        System.out.println("request uri: " + ((HttpServletRequest)request).getRequestURI() + StackTraceUtil.getLocation());
        chain.doFilter(request, response);
    }

    public void destroy() {
        StackTraceUtil.printLocation();
    }

}
