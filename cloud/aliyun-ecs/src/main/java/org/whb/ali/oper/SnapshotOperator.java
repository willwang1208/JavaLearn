package org.whb.ali.oper;

import java.util.List;
import java.util.logging.Logger;

import org.whb.ali.util.ClientAdapter;
import org.whb.ali.util.Formatter;

import com.aliyun.api.domain.Snapshot;
import com.aliyun.api.ecs.ecs20140526.request.CreateSnapshotRequest;
import com.aliyun.api.ecs.ecs20140526.request.DeleteSnapshotRequest;
import com.aliyun.api.ecs.ecs20140526.request.DescribeSnapshotsRequest;
import com.aliyun.api.ecs.ecs20140526.response.CreateSnapshotResponse;
import com.aliyun.api.ecs.ecs20140526.response.DeleteSnapshotResponse;
import com.aliyun.api.ecs.ecs20140526.response.DescribeSnapshotsResponse;
import com.taobao.api.ApiException;

public class SnapshotOperator {
    
    private Logger logger = Logger.getLogger(this.getClass().getName());

    private ClientAdapter client;
    
    public SnapshotOperator(ClientAdapter client) {
        super();
        this.client = client;
    }

    public Snapshot create(Snapshot model, String regionId, boolean needWait) throws ApiException{
        CreateSnapshotRequest request = new CreateSnapshotRequest();
        request.setDiskId(model.getSourceDiskId());
        request.setSnapshotName(model.getSnapshotName());
        
        CreateSnapshotResponse response = client.execute(request);
        if(response.isSuccess()){
            String snapshotId = response.getSnapshotId();
            Snapshot snapshot = getById(regionId, snapshotId);
            if(needWait){
            	while("100%".equals(snapshot.getProgress()) == false){
                    logger.info("Create Snapshot: Waiting...... " + Formatter.beanToJsonString(snapshot));
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    snapshot = getById(regionId, snapshotId);
                }
        	}
            logger.info("Create Snapshot: Success! " + Formatter.beanToJsonString(snapshot));
            return snapshot;
        }else{
            throw new RuntimeException("Create Snapshot: Failed!!! " + response.getErrorCode());
        }
    }
    
    public void destroy(String regionId, String snapshotId) throws ApiException{
        Snapshot snapshot = getById(regionId, snapshotId);
        if(snapshot == null){
            logger.warning("Destroy Snapshot: Snapshot not found. " + snapshotId);
            return;
        }
        
        DeleteSnapshotRequest request = new DeleteSnapshotRequest();
        request.setSnapshotId(snapshotId);
        
        DeleteSnapshotResponse response = client.execute(request);
        if(response.isSuccess()){
            logger.info("Destroy Snapshot: Success! " + snapshot);
        }else{
            throw new RuntimeException("Destroy Snapshot: Failed!!! " + response.getErrorCode());
        }
    }
    
    public Snapshot getLatest(String regionId, String instanceId, String diskId) throws ApiException{
        DescribeSnapshotsRequest request = new DescribeSnapshotsRequest();
        request.setRegionId(regionId);
        request.setInstanceId(instanceId);
        request.setDiskId(diskId);
        request.setPageSize(Long.valueOf(50));
        
        Snapshot result = null;
        DescribeSnapshotsResponse response = client.execute(request);
        if(response.isSuccess()){
            List<Snapshot> list = response.getSnapshots();
            if(list != null){
                for(Snapshot item : list){
                    if(result == null){
                        result = item;
                    }else if(result.getCreationTime().compareTo(item.getCreationTime()) < 0
                            && "100%".equals(item.getProgress())){
                        result = item;
                    }
                }
            }
        }
//        if(result != null){
//            logger.info("getLatest Snapshot: Success! " + Formatter.beanToJsonString(result));
//        }
        return result;
    }
    
    public List<Snapshot> getAll(String regionId, String instanceId, String diskId) throws ApiException{
        DescribeSnapshotsRequest request = new DescribeSnapshotsRequest();
        request.setRegionId(regionId);
        request.setInstanceId(instanceId);
        request.setDiskId(diskId);
        request.setPageSize(Long.valueOf(50));
        
        DescribeSnapshotsResponse response = client.execute(request);
        if(response.isSuccess()){
            return response.getSnapshots();
        }else{
            throw new RuntimeException("Get all snapshots: Failed!!! " + response.getErrorCode());
        }
    }
    
    public Snapshot getById(String regionId, String snapshotId) throws ApiException{
        DescribeSnapshotsRequest request = new DescribeSnapshotsRequest();
        request.setRegionId(regionId);
        request.setSnapshotIds("[ \"" + snapshotId + "\" ]");
        
        DescribeSnapshotsResponse response = client.execute(request);
        if(response.isSuccess()){
            List<Snapshot> list = response.getSnapshots();
            if(list != null && list.size() != 0){
                return list.get(0);
            }
        }
        return null;
    }
}
