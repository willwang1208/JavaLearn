package org.whb.springmvc.service;

import org.springframework.util.concurrent.ListenableFuture;

public interface IAsyncService {

    public void doAsyncTask();
    
    public void throwAsyncException();
    
    public ListenableFuture<String> getAsyncCallback();
}
