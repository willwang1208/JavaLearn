package org.whb.ali.util;

import java.net.SocketTimeoutException;
import java.util.logging.Logger;

import com.aliyun.api.AliyunClient;
import com.aliyun.api.AliyunRequest;
import com.aliyun.api.AliyunResponse;
import com.taobao.api.ApiException;

public class ClientAdapter {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    private AliyunClient client;
    
    public ClientAdapter(AliyunClient client) {
        super();
        this.client = client;
    }
    
    public <T extends AliyunResponse> T execute(AliyunRequest<T> request) throws ApiException{
        try {
            return client.execute(request);
        } catch (ApiException e) {
            if(e.getCause() instanceof SocketTimeoutException){
                logger.info("Socket timeout: Waiting to retry...... " + request.getClass().getName());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                return execute(request);
            }
            throw e;
        }
    }
}
