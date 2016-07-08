package org.whb.springmvc.interceptor;

import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.DeferredResultProcessingInterceptorAdapter;
import org.whb.springmvc.util.StackTraceUtil;

public class HelloWorldDeferredResultProcessingInterceptor extends DeferredResultProcessingInterceptorAdapter {

    @Override
    public <T> void beforeConcurrentHandling(NativeWebRequest request, DeferredResult<T> deferredResult)
            throws Exception {
        StackTraceUtil.printLocation();
    }

    @Override
    public <T> void preProcess(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception {
        StackTraceUtil.printLocation();
    }

    @Override
    public <T> void postProcess(NativeWebRequest request, DeferredResult<T> deferredResult, Object concurrentResult)
            throws Exception {
        StackTraceUtil.printLocation();
    }

    @Override
    public <T> boolean handleTimeout(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception {
        StackTraceUtil.printLocation();
        return super.handleTimeout(request, deferredResult);
    }

    @Override
    public <T> void afterCompletion(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception {
        StackTraceUtil.printLocation();
    }

}
