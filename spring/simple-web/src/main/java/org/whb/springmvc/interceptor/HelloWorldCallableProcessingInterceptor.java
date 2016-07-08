package org.whb.springmvc.interceptor;

import java.util.concurrent.Callable;

import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.CallableProcessingInterceptorAdapter;
import org.whb.springmvc.util.StackTraceUtil;

public class HelloWorldCallableProcessingInterceptor extends CallableProcessingInterceptorAdapter {

    @Override
    public <T> void beforeConcurrentHandling(NativeWebRequest request, Callable<T> task) throws Exception {
        StackTraceUtil.printLocation();
    }

    @Override
    public <T> void preProcess(NativeWebRequest request, Callable<T> task) throws Exception {
        StackTraceUtil.printLocation();
    }

    @Override
    public <T> void postProcess(NativeWebRequest request, Callable<T> task, Object concurrentResult) throws Exception {
        StackTraceUtil.printLocation();
    }

    @Override
    public <T> Object handleTimeout(NativeWebRequest request, Callable<T> task) throws Exception {
        StackTraceUtil.printLocation();
        return super.handleTimeout(request, task);
    }

    @Override
    public <T> void afterCompletion(NativeWebRequest request, Callable<T> task) throws Exception {
        StackTraceUtil.printLocation();
    }

}
