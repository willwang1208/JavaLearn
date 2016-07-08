package org.whb.springmvc.interceptor;

import java.lang.reflect.Method;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.whb.springmvc.util.StackTraceUtil;

public class HelloWorldAsyncUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        StackTraceUtil.printLocation();
    }

}
