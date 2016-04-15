package org.whb.ali.oper;

import java.util.List;
import java.util.logging.Logger;

import org.whb.ali.util.ClientAdapter;
import org.whb.ali.util.Formatter;

import com.aliyun.api.domain.VSwitch;
import com.aliyun.api.ecs.ecs20140526.request.CreateVSwitchRequest;
import com.aliyun.api.ecs.ecs20140526.request.DeleteVSwitchRequest;
import com.aliyun.api.ecs.ecs20140526.request.DescribeVSwitchesRequest;
import com.aliyun.api.ecs.ecs20140526.response.CreateVSwitchResponse;
import com.aliyun.api.ecs.ecs20140526.response.DeleteVSwitchResponse;
import com.aliyun.api.ecs.ecs20140526.response.DescribeVSwitchesResponse;
import com.taobao.api.ApiException;

public class VSwitchOperator {
    
    private Logger logger = Logger.getLogger(this.getClass().getName());

    private ClientAdapter client;
    
    public VSwitchOperator(ClientAdapter client) {
        super();
        this.client = client;
    }

    public VSwitch create(VSwitch model) throws ApiException{
        VSwitch vSwitch = getByName(model.getVpcId(), model.getvSwitchName());
        if(vSwitch != null){
            logger.warning("Create VSwitch: VSwitch already exists. " + Formatter.beanToJsonString(vSwitch));
            return vSwitch;
        }
        
        CreateVSwitchRequest request = new CreateVSwitchRequest();
        request.setZoneId(model.getZoneId());
        request.setCidrBlock(model.getCidrBlock());
        request.setVpcId(model.getVpcId());
        request.setvSwitchName(model.getvSwitchName());
        
        CreateVSwitchResponse response = client.execute(request);
        if(response.isSuccess()){
            vSwitch = getByName(model.getVpcId(), model.getvSwitchName());
            while("Pending".equals(vSwitch.getStatus())){
                logger.info("Create VSwitch: Waiting...... " + Formatter.beanToJsonString(vSwitch));
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                vSwitch = getByName(model.getVpcId(), model.getvSwitchName());
            }
            logger.info("Create VSwitch: Success! " + Formatter.beanToJsonString(vSwitch));
            return vSwitch;
        }else{
            throw new RuntimeException("Create VSwitch: Failed!!! " + response.getErrorCode());
        }
    }
    
    public void destroy(String vpcId, String vSwitchName) throws ApiException{
        VSwitch vSwitch = getByName(vpcId, vSwitchName);
        if(vSwitch == null){
            logger.warning("Destroy VSwitch: VSwitch not found. " + vSwitchName);
            return;
        }
        _destroy(vSwitch);
    }
    
    public void destroy(String vSwitchId) throws ApiException{
        VSwitch vSwitch = new VSwitch();
        vSwitch.setvSwitchId(vSwitchId);
        _destroy(vSwitch);
    }
    
    private void _destroy(VSwitch vSwitch) throws ApiException{
        DeleteVSwitchRequest request = new DeleteVSwitchRequest();
        request.setvSwitchId(vSwitch.getvSwitchId());
        
        DeleteVSwitchResponse response = client.execute(request);
        if(response.isSuccess()){
            logger.info("Destroy VSwitch: Waiting...... " + Formatter.beanToJsonString(vSwitch));
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            logger.info("Destroy VSwitch: Success! " + Formatter.beanToJsonString(vSwitch));
        }else{
            throw new RuntimeException("Destroy VSwitch: Failed!!! " + response.getErrorCode());
        }
    }

    public VSwitch getByName(String vpcId, String vSwitchName) throws ApiException{
        DescribeVSwitchesRequest request = new DescribeVSwitchesRequest();
        request.setVpcId(vpcId);
        request.setPageSize(Long.valueOf(50));
        
        DescribeVSwitchesResponse response = client.execute(request);
        if(response.isSuccess()){
            List<VSwitch> list = response.getvSwitches();
            if(list != null){
                for(VSwitch item : list){
                    if(vSwitchName.equals(item.getvSwitchName())){
                        return item;
                    }
                }
            }
        }
        return null;
    }
}
