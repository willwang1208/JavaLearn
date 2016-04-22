package org.whb.web.listener;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

import org.whb.web.util.StackTraceUtil;

public class HelloWorldServletRequestListener implements ServletRequestListener {

    public void requestDestroyed(ServletRequestEvent event) {
        StackTraceUtil.printLocation();
    }

    public void requestInitialized(ServletRequestEvent event) {
        StackTraceUtil.printLocation();
        System.out.println("\t" + event.getServletRequest().getContentType());
        System.out.println("\t" + event.getServletRequest().getContentLengthLong());
    }

}
