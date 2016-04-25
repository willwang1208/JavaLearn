package org.whb.ali.oper;

import java.util.List;
import java.util.logging.Logger;

import org.whb.ali.util.ClientAdapter;
import org.whb.ali.util.Formatter;

import com.aliyun.api.domain.Disk;
import com.aliyun.api.ecs.ecs20140526.request.DescribeDisksRequest;
import com.aliyun.api.ecs.ecs20140526.response.DescribeDisksResponse;
import com.taobao.api.ApiException;

public class DiskOperator {
    
    private Logger logger = Logger.getLogger(this.getClass().getName());

    private ClientAdapter client;
    
    public DiskOperator(ClientAdapter client) {
        super();
        this.client = client;
    }

    public List<Disk> getInstanceDataDisks(String regionId, String instanceId) throws ApiException{
        DescribeDisksRequest request = new DescribeDisksRequest();
        request.setRegionId(regionId);
        request.setInstanceId(instanceId);
        request.setDiskType("data");
        
        DescribeDisksResponse response = client.execute(request);
        if(response.isSuccess()){
//            logger.info("Describe data disks: " + Formatter.beanToJsonString(response.getDisks()));
            return response.getDisks();
        }else{
            throw new RuntimeException("Describe data disks: Failed!!! " + response.getErrorCode());
        }
    }
    
    public Disk getInstanceSystemDisk(String regionId, String instanceId) throws ApiException{
        DescribeDisksRequest request = new DescribeDisksRequest();
        request.setRegionId(regionId);
        request.setInstanceId(instanceId);
        request.setDiskType("system");
        
        DescribeDisksResponse response = client.execute(request);
        if(response.isSuccess()){
//            logger.info("Describe system disk: " + Formatter.beanToJsonString(response.getDisks()));
            return response.getDisks().get(0);   //promise get 0
        }else{
            throw new RuntimeException("Describe system disk: Failed!!! " + response.getErrorCode());
        }
    }

}
