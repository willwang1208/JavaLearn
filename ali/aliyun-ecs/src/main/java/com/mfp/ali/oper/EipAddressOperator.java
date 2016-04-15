package com.mfp.ali.oper;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.aliyun.api.domain.Eip;
import com.aliyun.api.ecs.ecs20140526.request.AllocateEipAddressRequest;
import com.aliyun.api.ecs.ecs20140526.request.AssociateEipAddressRequest;
import com.aliyun.api.ecs.ecs20140526.request.DescribeEipAddressesRequest;
import com.aliyun.api.ecs.ecs20140526.request.ReleaseEipAddressRequest;
import com.aliyun.api.ecs.ecs20140526.request.UnassociateEipAddressRequest;
import com.aliyun.api.ecs.ecs20140526.response.AllocateEipAddressResponse;
import com.aliyun.api.ecs.ecs20140526.response.AssociateEipAddressResponse;
import com.aliyun.api.ecs.ecs20140526.response.DescribeEipAddressesResponse;
import com.aliyun.api.ecs.ecs20140526.response.ReleaseEipAddressResponse;
import com.aliyun.api.ecs.ecs20140526.response.UnassociateEipAddressResponse;
import com.mfp.ali.util.ClientAdapter;
import com.mfp.ali.util.Formatter;
import com.taobao.api.ApiException;

public class EipAddressOperator {
    
    private Logger logger = Logger.getLogger(this.getClass().getName());

    private ClientAdapter client;
    
    public EipAddressOperator(ClientAdapter client) {
        super();
        this.client = client;
    }
    
    public void createAndAssociate(Eip model) throws ApiException{
        Eip eip = create(model);
        associate(eip.getRegionId(), eip.getAllocationId(), model.getInstanceId());
    }

    public Eip create(Eip model) throws ApiException{
        AllocateEipAddressRequest request = new AllocateEipAddressRequest();
        request.setRegionId(model.getRegionId());
        request.setBandwidth(model.getBandwidth());
        request.setInternetChargeType(model.getInternetChargeType());
        
        AllocateEipAddressResponse response = client.execute(request);
        if(response.isSuccess()){
            String allocationId = response.getAllocationId();
            Eip eip = getByAllocationId(model.getRegionId(), allocationId);
            while("Available".equals(eip.getStatus()) == false){
                logger.info("Create Eip: Waiting...... " + Formatter.beanToJsonString(eip));
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                eip = getByAllocationId(model.getRegionId(), allocationId);
            }
            logger.info("Create Eip: Success! " + Formatter.beanToJsonString(eip));
            return eip;
        }else{
            throw new RuntimeException("Create Eip: Failed!!! " + response.getErrorCode());
        }
    }
    
    public void associate(String regionId, String allocationId, String instanceId) throws ApiException{
        Eip eip = getByAllocationId(regionId, allocationId);
        if(eip == null){
            logger.warning("Associate Eip: Eip not found. " + allocationId);
            return;
        }
        
        if("InUse".equals(eip.getStatus())){
            logger.warning("Associate Eip: Eip is already in use. " + Formatter.beanToJsonString(eip));
            return;
        }
        
        AssociateEipAddressRequest request = new AssociateEipAddressRequest();
        request.setAllocationId(allocationId);
        request.setInstanceId(instanceId);
        
        AssociateEipAddressResponse response = client.execute(request);
        if(response.isSuccess()){
            eip = getByAllocationId(regionId, allocationId);
            while("Associating".equals(eip.getStatus())){
                logger.info("Associate Eip: Waiting...... " + Formatter.beanToJsonString(eip));
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                eip = getByAllocationId(regionId, allocationId);
            }
            logger.info("Associate Eip: Success! " + Formatter.beanToJsonString(eip));
        }else{
            throw new RuntimeException("Associate Eip: Failed!!! " + response.getErrorCode());
        }
    }
    
    public void unassociateAndDestroy(String regionId, String allocationId, String instanceId) throws ApiException{
        unassociate(regionId, allocationId, instanceId);
        destroy(allocationId);
    }
    
    public void unassociate(String regionId, String allocationId, String instanceId) throws ApiException{
        Eip eip = getByAllocationId(regionId, allocationId);
        if(eip == null){
            logger.warning("Unassociate Eip: Eip not found. " + allocationId);
            return;
        }
        
        if("Available".equals(eip.getStatus())){
            logger.warning("Unassociate Eip: Eip is not associated. " + Formatter.beanToJsonString(eip));
            return;
        }
        
        UnassociateEipAddressRequest request = new UnassociateEipAddressRequest();
        request.setAllocationId(allocationId);
        request.setInstanceId(instanceId);
        
        UnassociateEipAddressResponse response = client.execute(request);
        if(response.isSuccess()){
            eip = getByAllocationId(regionId, allocationId);
            while("Unassociating".equals(eip.getStatus())){
                logger.info("Unassociate Eip: Waiting...... " + Formatter.beanToJsonString(eip));
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                eip = getByAllocationId(regionId, allocationId);
            }
            logger.info("Unassociate Eip: Success! " + Formatter.beanToJsonString(eip));
        }else{
            throw new RuntimeException("Unassociate Eip: Failed!!! " + response.getErrorCode());
        }
    }

    public void destroy(String allocationId) throws ApiException{
        ReleaseEipAddressRequest request = new ReleaseEipAddressRequest();
        request.setAllocationId(allocationId);
        
        ReleaseEipAddressResponse response = client.execute(request);
        if(response.isSuccess()){
            logger.info("Destroy Eip: Success! " + allocationId);
        }else{
            throw new RuntimeException("Destroy Eip: Failed!!! " + response.getErrorCode());
        }
    }
    
    @SuppressWarnings("unchecked")
    public Eip getByAllocationId(String regionId, String allocationId) throws ApiException{
        DescribeEipAddressesRequest request = new DescribeEipAddressesRequest();
        request.setRegionId(regionId);
        request.setAllocationId(allocationId);
        request.setPageSize(Long.valueOf(50));
        
        DescribeEipAddressesResponse response = client.execute(request);
        if(response.isSuccess()){
            Map<?, ?> map = Formatter.jsonToMap(response.getBody().getBytes());
            Map<?, ?> EipAddresses = (Map<?, ?>)map.get("EipAddresses");
            if(EipAddresses != null){
                List<Map<?, ?>> EipAddress = (List<Map<?, ?>>)EipAddresses.get("EipAddress");
                if(EipAddress != null && EipAddress.size() != 0){
                    Eip eip = new Eip();
                    Map<?, ?> ea = EipAddress.get(0);
                    eip.setStatus((String)ea.get("Status"));
                    eip.setInstanceId((String)ea.get("InstanceId"));
                    eip.setAllocationId((String)ea.get("AllocationId"));
                    eip.setAllocationTime((String)ea.get("AllocationTime"));
                    eip.setBandwidth((String)ea.get("Bandwidth"));
                    eip.setInternetChargeType((String)ea.get("InternetChargeType"));
                    eip.setIpAddress((String)ea.get("IpAddress"));
                    eip.setRegionId((String)ea.get("RegionId"));
                    
                    return eip;
                }
            }
            
//            List<Eip> list = response.getEipAddresses();
//            if(list != null && list.size() != 0){
//                return list.get(0);
//            }
        }
        return null;
    }
}
