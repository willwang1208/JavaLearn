package org.whb.web.listener;

import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;

import org.whb.web.util.StackTraceUtil;

public class HelloWorldServletRequestAttributeListener implements ServletRequestAttributeListener {

    public void attributeAdded(ServletRequestAttributeEvent event) {
        StackTraceUtil.printLocation();
        System.out.println("\t" + event.getName());
        System.out.println("\t" + event.getValue());
    }

    public void attributeRemoved(ServletRequestAttributeEvent event) {
        StackTraceUtil.printLocation();
        System.out.println("\t" + event.getName());
        System.out.println("\t" + event.getValue());
    }

    public void attributeReplaced(ServletRequestAttributeEvent event) {
        StackTraceUtil.printLocation();
        System.out.println("\t" + event.getName());
        System.out.println("\t" + event.getValue());
    }

}
