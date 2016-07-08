package org.whb.springmvc.service.impl;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.whb.springmvc.service.IAsyncService;
/**
 * 异步服务
 * 通过注解@Async配置异步Service，需要在上下文配置中依赖@EnableAsync。
 * 注解@Async在类上添加使所有方法都是异步，方法如果有返回值必须是ListenableFuture/Future
 * 
 * @see org.whb.springmvc.config.ServiceConfiguration
 * @see org.springframework.core.task.AsyncListenableTaskExecutor
 * @author 
 *
 */
@Service
@Async
public class AsyncServiceImpl implements IAsyncService{

//    @Async
    public void doAsyncTask() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("AsyncServiceImpl.doAsyncTask Done");
    }
    
    public void throwAsyncException() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("An AsyncException Occurs");
    }
    
    public ListenableFuture<String> getAsyncCallback() {
        return new AsyncResult<String>("123");
    }
}
