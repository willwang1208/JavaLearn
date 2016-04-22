package org.whb.web.listener;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

import org.whb.web.util.StackTraceUtil;

public class HelloWorldHttpSessionAttributeListener implements HttpSessionAttributeListener {

    public void attributeAdded(HttpSessionBindingEvent event) {
        StackTraceUtil.printLocation();
        System.out.println("\t" + event.getName());
        System.out.println("\t" + event.getValue());
    }

    public void attributeRemoved(HttpSessionBindingEvent event) {
        StackTraceUtil.printLocation();
        System.out.println("\t" + event.getName());
        System.out.println("\t" + event.getValue());
    }

    public void attributeReplaced(HttpSessionBindingEvent event) {
        StackTraceUtil.printLocation();
        System.out.println("\t" + event.getName());
        System.out.println("\t" + event.getValue());
    }

}
