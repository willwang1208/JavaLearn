package org.whb.ali.oper;

import java.util.List;
import java.util.logging.Logger;

import org.whb.ali.util.ClientAdapter;
import org.whb.ali.util.Formatter;

import com.aliyun.api.domain.SecurityGroup;
import com.aliyun.api.ecs.ecs20140526.request.AuthorizeSecurityGroupEgressRequest;
import com.aliyun.api.ecs.ecs20140526.request.AuthorizeSecurityGroupRequest;
import com.aliyun.api.ecs.ecs20140526.request.CreateSecurityGroupRequest;
import com.aliyun.api.ecs.ecs20140526.request.DeleteSecurityGroupRequest;
import com.aliyun.api.ecs.ecs20140526.request.DescribeSecurityGroupsRequest;
import com.aliyun.api.ecs.ecs20140526.request.RevokeSecurityGroupEgressRequest;
import com.aliyun.api.ecs.ecs20140526.request.RevokeSecurityGroupRequest;
import com.aliyun.api.ecs.ecs20140526.response.AuthorizeSecurityGroupEgressResponse;
import com.aliyun.api.ecs.ecs20140526.response.AuthorizeSecurityGroupResponse;
import com.aliyun.api.ecs.ecs20140526.response.CreateSecurityGroupResponse;
import com.aliyun.api.ecs.ecs20140526.response.DeleteSecurityGroupResponse;
import com.aliyun.api.ecs.ecs20140526.response.DescribeSecurityGroupsResponse;
import com.aliyun.api.ecs.ecs20140526.response.RevokeSecurityGroupEgressResponse;
import com.aliyun.api.ecs.ecs20140526.response.RevokeSecurityGroupResponse;
import com.taobao.api.ApiException;

public class SecurityGroupOperator {
    
    private Logger logger = Logger.getLogger(this.getClass().getName());

    private ClientAdapter client;
    
    public SecurityGroupOperator(ClientAdapter client) {
        super();
        this.client = client;
    }

    public SecurityGroup create(String regionId, String vpcId, String securityGroupName) throws ApiException{
        SecurityGroup securityGroup = getByName(regionId, vpcId, securityGroupName);
        if(securityGroup != null){
            logger.warning("Create SecurityGroup: SecurityGroup already exists. " + Formatter.beanToJsonString(securityGroup));
            return securityGroup;
        }
        
        CreateSecurityGroupRequest request = new CreateSecurityGroupRequest();
        request.setRegionId(regionId);
        request.setVpcId(vpcId);
        request.setSecurityGroupName(securityGroupName);
        
        CreateSecurityGroupResponse response = client.execute(request);
        if(response.isSuccess()){
            securityGroup = getByName(regionId, vpcId, securityGroupName);
            logger.info("Create SecurityGroup: Success! " + Formatter.beanToJsonString(securityGroup));
            
            authorizeIn(regionId, securityGroup.getSecurityGroupId());
            authorizeOut(regionId, securityGroup.getSecurityGroupId());
            
            return securityGroup;
        }else{
            throw new RuntimeException("Create SecurityGroup: Failed!!! " + response.getErrorCode());
        }
    }
    
    public void destroy(String regionId, String vpcId, String securityGroupName) throws ApiException{
        SecurityGroup securityGroup = getByName(regionId, vpcId, securityGroupName);
        if(securityGroup == null){
            logger.warning("Destroy SecurityGroup: SecurityGroup not found. " + securityGroupName);
            return;
        }
        
        revokeIn(regionId, securityGroup.getSecurityGroupId());
        revokeOut(regionId, securityGroup.getSecurityGroupId());
        
        DeleteSecurityGroupRequest request = new DeleteSecurityGroupRequest();
        request.setSecurityGroupId(securityGroup.getSecurityGroupId());
        request.setRegionId(regionId);
        
        DeleteSecurityGroupResponse response = client.execute(request);
        if(response.isSuccess()){
            logger.info("Destroy SecurityGroup: Success! " + Formatter.beanToJsonString(securityGroup));
        }else{
            throw new RuntimeException("Destroy SecurityGroup: Failed!!! " + response.getErrorCode());
        }
    }
    
    private void authorizeIn(String regionId, String sourceGroupId) throws ApiException{
        AuthorizeSecurityGroupRequest request = new AuthorizeSecurityGroupRequest();
        request.setRegionId(regionId);
        request.setSecurityGroupId(sourceGroupId);
        request.setIpProtocol("all");
        request.setPortRange("-1/-1");
        request.setNicType("intranet");
        request.setSourceCidrIp("0.0.0.0/0");
        
        AuthorizeSecurityGroupResponse response = client.execute(request);
        if(response.isSuccess()){
            logger.info("AuthorizeIn SecurityGroup: Success! " + sourceGroupId);
        }else{
            throw new RuntimeException("AuthorizeIn SecurityGroup: Failed!!! " + response.getErrorCode());
        }
    }
    
    private void authorizeOut(String regionId, String sourceGroupId) throws ApiException{
        AuthorizeSecurityGroupEgressRequest request = new AuthorizeSecurityGroupEgressRequest();
        request.setRegionId(regionId);
        request.setSecurityGroupId(sourceGroupId);
        request.setIpProtocol("all");
        request.setPortRange("-1/-1");
        request.setNicType("intranet");
        request.setDestCidrIp("0.0.0.0/0");
        
        AuthorizeSecurityGroupEgressResponse response = client.execute(request);
        if(response.isSuccess()){
            logger.info("AuthorizeOut SecurityGroup: Success! " + sourceGroupId);
        }else{
            throw new RuntimeException("AuthorizeOut SecurityGroup: Failed!!! " + response.getErrorCode());
        }
    }
    
    private void revokeIn(String regionId, String sourceGroupId) throws ApiException{
        RevokeSecurityGroupRequest request = new RevokeSecurityGroupRequest();
        request.setRegionId(regionId);
        request.setSecurityGroupId(sourceGroupId);
        request.setIpProtocol("all");
        request.setPortRange("-1/-1");
        request.setNicType("intranet");
        request.setSourceCidrIp("0.0.0.0/0");
        
        RevokeSecurityGroupResponse response = client.execute(request);
        if(response.isSuccess()){
            logger.info("RevokeIn SecurityGroup: Success! " + sourceGroupId);
        }else{
            throw new RuntimeException("RevokeIn SecurityGroup: Failed!!! " + response.getErrorCode());
        }
    }
    
    private void revokeOut(String regionId, String sourceGroupId) throws ApiException{
        RevokeSecurityGroupEgressRequest request = new RevokeSecurityGroupEgressRequest();
        request.setRegionId(regionId);
        request.setSecurityGroupId(sourceGroupId);
        request.setIpProtocol("all");
        request.setPortRange("-1/-1");
        request.setNicType("intranet");
        request.setDestCidrIp("0.0.0.0/0");
        
        RevokeSecurityGroupEgressResponse response = client.execute(request);
        if(response.isSuccess()){
            logger.info("RevokeOut SecurityGroup: Success! " + sourceGroupId);
        }else{
            throw new RuntimeException("RevokeOut SecurityGroup: Failed!!! " + response.getErrorCode());
        }
    }
    
    private SecurityGroup getByName(String regionId, String vpcId, String securityGroupName) throws ApiException{
        DescribeSecurityGroupsRequest request = new DescribeSecurityGroupsRequest();
        request.setVpcId(vpcId);
        request.setRegionId(regionId);
        request.setPageSize(Long.valueOf(50));
        
        DescribeSecurityGroupsResponse response = client.execute(request);
        if(response.isSuccess()){
            List<SecurityGroup> list = response.getSecurityGroups();
            if(list != null){
                for(SecurityGroup item : list){
                    if(securityGroupName.equals(item.getSecurityGroupName())){
                        return item;
                    }
                }
            }
        }
        return null;
    }

}
