package org.whb.ali.oper;

import java.util.List;
import java.util.logging.Logger;

import org.whb.ali.util.ClientAdapter;
import org.whb.ali.util.Formatter;

import com.aliyun.api.domain.Vpcs;
import com.aliyun.api.ecs.ecs20140526.request.CreateVpcRequest;
import com.aliyun.api.ecs.ecs20140526.request.DeleteVpcRequest;
import com.aliyun.api.ecs.ecs20140526.request.DescribeVpcsRequest;
import com.aliyun.api.ecs.ecs20140526.response.CreateVpcResponse;
import com.aliyun.api.ecs.ecs20140526.response.DeleteVpcResponse;
import com.aliyun.api.ecs.ecs20140526.response.DescribeVpcsResponse;
import com.taobao.api.ApiException;

public class VPCOperator {
    
    private Logger logger = Logger.getLogger(this.getClass().getName());

    private ClientAdapter client;
    
    public VPCOperator(ClientAdapter client) {
        super();
        this.client = client;
    }

    public Vpcs create(Vpcs model) throws ApiException{
        Vpcs vpcs = getByName(model.getRegionId(), model.getVpcName());
        if(vpcs != null){
            logger.warning("Create VPC: VPC already exists. " + Formatter.beanToJsonString(vpcs));
            return vpcs;
        }
        
        CreateVpcRequest request = new CreateVpcRequest();
        request.setRegionId(model.getRegionId());
        request.setCidrBlock(model.getCidrBlock());
        request.setVpcName(model.getVpcName());
        
        CreateVpcResponse response = client.execute(request);
        if(response.isSuccess()){
            vpcs = getByName(model.getRegionId(), model.getVpcName());
            while("Pending".equals(vpcs.getStatus())){
                logger.info("Create VPC: Waiting...... " + Formatter.beanToJsonString(vpcs));
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                vpcs = getByName(model.getRegionId(), model.getVpcName());
            }
            logger.info("Create VPC: Success! " + Formatter.beanToJsonString(vpcs));
            return vpcs;
        }else{
            throw new RuntimeException("Create VPC: Failed!!! " + response.getErrorCode());
        }
    }
    
    public void destroy(String regionId, String vpcName) throws ApiException{
        Vpcs vpcs = getByName(regionId, vpcName);
        if(vpcs == null){
            logger.warning("Destroy VPC: VPC not found. " + vpcName);
            return;
        }
        
        DeleteVpcRequest request = new DeleteVpcRequest();
        request.setVpcId(vpcs.getVpcId());
        
        DeleteVpcResponse response = client.execute(request);
        if(response.isSuccess()){
            logger.info("Destroy VPC: Success! " + Formatter.beanToJsonString(vpcs));
        }else{
            throw new RuntimeException("Destroy VPC: Failed!!! " + response.getErrorCode());
        }
    }
    
    public Vpcs getByName(String regionId, String vpcName) throws ApiException{
        DescribeVpcsRequest request = new DescribeVpcsRequest();
        request.setRegionId(regionId);
        request.setPageSize(Long.valueOf(50));
        
        DescribeVpcsResponse response = client.execute(request);
        if(response.isSuccess()){
            List<Vpcs> list = response.getVpcs();
            if(list != null){
                for(Vpcs item : list){
                    if(vpcName.equals(item.getVpcName())){
                        return item;
                    }
                }
            }
        }
        return null;
    }
}
