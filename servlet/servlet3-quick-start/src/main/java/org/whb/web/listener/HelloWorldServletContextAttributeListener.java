package org.whb.web.listener;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;

import org.whb.web.util.StackTraceUtil;

public class HelloWorldServletContextAttributeListener implements ServletContextAttributeListener {

    public void attributeAdded(ServletContextAttributeEvent event) {
        StackTraceUtil.printLocation();
        System.out.println("\t" + event.getName());
        System.out.println("\t" + event.getValue());
    }

    public void attributeRemoved(ServletContextAttributeEvent event) {
        StackTraceUtil.printLocation();
        System.out.println("\t" + event.getName());
        System.out.println("\t" + event.getValue());
    }

    public void attributeReplaced(ServletContextAttributeEvent event) {
        StackTraceUtil.printLocation();
        System.out.println("\t" + event.getName());
        System.out.println("\t" + event.getValue());
    }

}
