package org.whb.web.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.whb.web.util.StackTraceUtil;

public class HelloWorldServletContextListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent event) {
        StackTraceUtil.printLocation();
        System.out.println("\t" + event.getServletContext().getContextPath());
    }

    public void contextDestroyed(ServletContextEvent event) {
        StackTraceUtil.printLocation();
        System.out.println("\t" + event.getServletContext().getContextPath());
    }

}
