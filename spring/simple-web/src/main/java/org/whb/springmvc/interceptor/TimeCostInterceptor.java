package org.whb.springmvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
/**
 * 计算执行时间的Interceptor
 * 
 * @author 
 *
 */
public class TimeCostInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LogManager.getLogger(TimeCostInterceptor.class);

    /**
     * 执行前将当前时刻作为开始时刻存入request
     */
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute("ExecuteTime.startTime", System.currentTimeMillis());
        return true;
    }

    /**
     * 执行后从request取出开始时刻，与当前时刻计算差值
     */
    public void postHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        long startTime = (Long) request.getAttribute("ExecuteTime.startTime");
        long executeTime = System.currentTimeMillis() - startTime;

        modelAndView.addObject("executeTime", executeTime);

        if (logger.isDebugEnabled()) {
            logger.debug("[" + handler + "] executeTime : " + executeTime + "ms");
        }
    }
}