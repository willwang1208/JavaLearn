package org.whb.springmvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 计算执行时间的Interceptor
 * @author 
 *
 */
public class TimeCostInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LogManager.getLogger(TimeCostInterceptor.class);

    // before the actual handler will be executed
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {

        request.setAttribute("ExecuteTime.startTime", System.currentTimeMillis());
        return true;
    }

    // after the handler is executed
    public void postHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {

        long startTime = (Long) request.getAttribute("ExecuteTime.startTime");

        long executeTime = System.currentTimeMillis() - startTime;

        // modified the exisitng modelAndView
//        modelAndView.addObject("executeTime", executeTime);

        // log it
        if (logger.isDebugEnabled()) {
            logger.debug("[" + handler + "] executeTime : " + executeTime + "ms");
        }
    }
}