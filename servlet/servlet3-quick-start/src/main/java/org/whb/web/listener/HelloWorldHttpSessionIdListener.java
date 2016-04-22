package org.whb.web.listener;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionIdListener;

import org.whb.web.util.StackTraceUtil;

public class HelloWorldHttpSessionIdListener implements HttpSessionIdListener {

    public void sessionIdChanged(HttpSessionEvent event, String oldSessionId) {
        StackTraceUtil.printLocation();
        System.out.println("\t" + oldSessionId);
        System.out.println("\t" + event.getSession().getId());
    }

}
