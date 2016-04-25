package org.whb.ali.oper;

import java.util.List;
import java.util.logging.Logger;

import org.whb.ali.util.ClientAdapter;
import org.whb.ali.util.Formatter;

import com.aliyun.api.domain.Disk;
import com.aliyun.api.domain.Instance;
import com.aliyun.api.ecs.ecs20140526.request.CreateInstanceRequest;
import com.aliyun.api.ecs.ecs20140526.request.DeleteInstanceRequest;
import com.aliyun.api.ecs.ecs20140526.request.DescribeInstancesRequest;
import com.aliyun.api.ecs.ecs20140526.request.StartInstanceRequest;
import com.aliyun.api.ecs.ecs20140526.request.StopInstanceRequest;
import com.aliyun.api.ecs.ecs20140526.response.CreateInstanceResponse;
import com.aliyun.api.ecs.ecs20140526.response.DeleteInstanceResponse;
import com.aliyun.api.ecs.ecs20140526.response.DescribeInstancesResponse;
import com.aliyun.api.ecs.ecs20140526.response.StartInstanceResponse;
import com.aliyun.api.ecs.ecs20140526.response.StopInstanceResponse;
import com.taobao.api.ApiException;

public class InstanceOperator {
    
    private Logger logger = Logger.getLogger(this.getClass().getName());

    private ClientAdapter client;
    
    public InstanceOperator(ClientAdapter client) {
        super();
        this.client = client;
    }
    
    public Instance get(String regionId, String instanceId) throws ApiException{
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        request.setRegionId(regionId);
        request.setInstanceIds("[\"" + instanceId + "\"]");

        DescribeInstancesResponse response = client.execute(request);
        if(response.isSuccess()){
            logger.info("Describe instance: " + Formatter.beanToJsonString(response.getInstances()));
            return response.getInstances().get(0);  //promise get 0
        }else{
            throw new RuntimeException("Describe instance: Failed!!! " + response.getErrorCode());
        }
    }
    
    public List<Instance> getAll(String regionId) throws ApiException{
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        request.setRegionId(regionId);
        request.setPageSize(Long.valueOf(50));

        DescribeInstancesResponse response = client.execute(request);
        if(response.isSuccess()){
            return response.getInstances();
        }else{
            throw new RuntimeException("Describe all instance: Failed!!! " + response.getErrorCode());
        }
    }

    public Instance create(Instance model, String vswitchId, String securityGroupId, 
            String ip, String pwd, String ioOptimized, 
            Disk sysDiskModel, List<Disk> dataDiskModels) throws ApiException{
        Instance instance = getByName(model.getRegionId(), model.getInstanceName());
        if(instance != null){
            logger.warning("Create Instance: Instance already exists. " + Formatter.beanToJsonString(instance));
            return instance;
        }
        
        CreateInstanceRequest request = new CreateInstanceRequest();
        request.setInstanceName(model.getInstanceName());
        request.setRegionId(model.getRegionId());
        request.setImageId(model.getImageId());
        request.setInstanceType(model.getInstanceType());
//        request.setInternetChargeType(model.getInternetChargeType());
//        request.setInternetMaxBandwidthIn(model.getInternetMaxBandwidthIn());
//        request.setInternetMaxBandwidthOut(model.getInternetMaxBandwidthOut());
        request.setHostName(model.getHostName());
        request.setIoOptimized(ioOptimized);
        
        request.setSecurityGroupId(securityGroupId);
        request.setvSwitchId(vswitchId);
        request.setPrivateIpAddress(ip);
        
        request.setPassword(pwd);
        
        if(sysDiskModel != null){
            request.setSystemDiskCategory(sysDiskModel.getCategory());
            request.setSystemDiskDiskName(sysDiskModel.getDiskName());
        }
        
        if(dataDiskModels != null && dataDiskModels.size() != 0){
            int index = 1;
            for(Disk disk: dataDiskModels){
                if(index == 1){
                    request.setDataDisk1Category(disk.getCategory());
                    request.setDataDisk1Device(disk.getDevice());
                    request.setDataDisk1DiskName(disk.getDiskName());
//                    request.setDataDisk1Size(disk.getSize());
                    request.setDataDisk1SnapshotId(disk.getSourceSnapshotId());
                }else if(index == 2){
                    request.setDataDisk2Category(disk.getCategory());
                    request.setDataDisk2Device(disk.getDevice());
                    request.setDataDisk2DiskName(disk.getDiskName());
//                    request.setDataDisk2Size(disk.getSize());
                    request.setDataDisk2SnapshotId(disk.getSourceSnapshotId());
                }else if(index == 3){
                    request.setDataDisk3Category(disk.getCategory());
                    request.setDataDisk3Device(disk.getDevice());
                    request.setDataDisk3DiskName(disk.getDiskName());
//                    request.setDataDisk3Size(disk.getSize());
                    request.setDataDisk3SnapshotId(disk.getSourceSnapshotId());
                }else if(index == 4){
                    request.setDataDisk4Category(disk.getCategory());
                    request.setDataDisk4Device(disk.getDevice());
                    request.setDataDisk4DiskName(disk.getDiskName());
//                    request.setDataDisk4Size(disk.getSize());
                    request.setDataDisk4SnapshotId(disk.getSourceSnapshotId());
                }
                index ++;
            }
        }
        
        CreateInstanceResponse response = client.execute(request);
        if(response.isSuccess()){
            instance = getByName(model.getRegionId(), model.getInstanceName());
            while("Stopped".equals(instance.getStatus()) == false){
                logger.info("Create Instance: Waiting...... " + Formatter.beanToJsonString(instance));
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                instance = getByName(model.getRegionId(), model.getInstanceName());
            }
            logger.info("Create Instance: Success! " + Formatter.beanToJsonString(instance));
            return instance;
        }else{
            throw new RuntimeException("Create Instance: Failed!!! " + response.getErrorCode());
        }
    }
    
    public void destroy(String regionId, String instanceName) throws ApiException{
        Instance instance = getByName(regionId, instanceName);
        if(instance == null){
            logger.warning("Destroy Instance: Instance not found. " + instanceName);
            return;
        }
        
        while("Stopping".equals(instance.getStatus())){
            logger.info("Destroy Instance: Waiting...... " + Formatter.beanToJsonString(instance));
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            instance = getByName(regionId, instanceName);
        }
        
        DeleteInstanceRequest request = new DeleteInstanceRequest();
        request.setInstanceId(instance.getInstanceId());
        
        DeleteInstanceResponse response = client.execute(request);
        if(response.isSuccess()){
            logger.info("Destroy Instance: Success! " + Formatter.beanToJsonString(instance));
        }else{
            throw new RuntimeException("Destroy Instance: Failed!!! " + response.getErrorCode());
        }
    }
    
    public boolean start(String regionId, String instanceName) throws ApiException{
        Instance instance = getByName(regionId, instanceName);
        if(instance == null){
            logger.warning("Start Instance: Instance not found. " + instanceName);
            return false;
        }
        
        if(instance.getStatus().equals("Running")){
            logger.warning("Start Instance: Instance is already running. " + Formatter.beanToJsonString(instance));
            return false;
        }
        
        StartInstanceRequest request = new StartInstanceRequest();
        request.setInstanceId(instance.getInstanceId());

        StartInstanceResponse response = client.execute(request);
        if(response.isSuccess()){
            while("Starting".equals(instance.getStatus())){
                logger.info("Start Instance: Waiting...... " + Formatter.beanToJsonString(instance));
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                instance = getByName(regionId, instanceName);
            }
            logger.info("Start Instance: Success! " + Formatter.beanToJsonString(instance));
            return true;
        }else{
            throw new RuntimeException("Start Instance: Failed!!! " + response.getErrorCode());
        }
    }
    
    public boolean stop(String regionId, String instanceName) throws ApiException{
        Instance instance = getByName(regionId, instanceName);
        if(instance == null){
            logger.warning("Stop Instance: Instance not found. " + instanceName);
            return false;
        }
        
        if(instance.getStatus().equals("Stopped")){
            logger.warning("Stop Instance: Instance is already stopped. " + Formatter.beanToJsonString(instance));
            return false;
        }
        
        StopInstanceRequest request = new StopInstanceRequest();
        request.setInstanceId(instance.getInstanceId());

        StopInstanceResponse response = client.execute(request);
        if(response.isSuccess()){
            while("Stopping".equals(instance.getStatus())){
                logger.info("Destroy Instance: Waiting...... " + Formatter.beanToJsonString(instance));
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                instance = getByName(regionId, instanceName);
            }
            logger.info("Stop Instance: Success! " + Formatter.beanToJsonString(instance));
            return true;
        }else{
            throw new RuntimeException("Stop Instance: Failed!!! " + response.getErrorCode());
        }
    }
    
    public Instance getByName(String regionId, String instanceName) throws ApiException{
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        request.setRegionId(regionId);
        request.setPageSize(Long.valueOf(50));
        
        DescribeInstancesResponse response = client.execute(request);
        if(response.isSuccess()){
            List<Instance> list = response.getInstances();
            if(list != null){
                for(Instance item : list){
                    if(instanceName.equals(item.getInstanceName())){
                        return item;
                    }
                }
            }
        }
        return null;
    }
}
