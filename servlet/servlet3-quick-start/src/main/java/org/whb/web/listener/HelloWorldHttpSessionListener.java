package org.whb.web.listener;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.whb.web.util.StackTraceUtil;

public class HelloWorldHttpSessionListener implements HttpSessionListener {

    public void sessionCreated(HttpSessionEvent event) {
        StackTraceUtil.printLocation();
        System.out.println("\t" + event.getSession().getId());
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        StackTraceUtil.printLocation();
        System.out.println("\t" + event.getSession().getId());
    }

}
