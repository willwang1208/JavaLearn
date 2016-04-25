package org.whb.ali.oper;

import java.util.List;
import java.util.logging.Logger;

import org.whb.ali.util.ClientAdapter;
import org.whb.ali.util.Formatter;

import com.aliyun.api.domain.Image;
import com.aliyun.api.ecs.ecs20140526.request.CreateImageRequest;
import com.aliyun.api.ecs.ecs20140526.request.DeleteImageRequest;
import com.aliyun.api.ecs.ecs20140526.request.DescribeImagesRequest;
import com.aliyun.api.ecs.ecs20140526.response.CreateImageResponse;
import com.aliyun.api.ecs.ecs20140526.response.DeleteImageResponse;
import com.aliyun.api.ecs.ecs20140526.response.DescribeImagesResponse;
import com.taobao.api.ApiException;

public class ImageOperator {
    
    private Logger logger = Logger.getLogger(this.getClass().getName());

    private ClientAdapter client;
    
    public ImageOperator(ClientAdapter client) {
        super();
        this.client = client;
    }

    public Image create(String regionId, String snapshotId, String imageName) throws ApiException{
        CreateImageRequest request = new CreateImageRequest();
        request.setImageName(imageName);
        request.setRegionId(regionId);
        request.setSnapshotId(snapshotId);
        
        CreateImageResponse response = client.execute(request);
        if(response.isSuccess()){
            String imageId = response.getImageId();
            Image image = getById(regionId, imageId);
            while("Creating".equals(image.getStatus())){
                logger.info("Create Image: Waiting...... " + Formatter.beanToJsonString(image));
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                image = getById(regionId, imageId);
            }
            logger.info("Create Image: Success! " + Formatter.beanToJsonString(image));
            return image;
        }else{
            throw new RuntimeException("Create Image: Failed!!! " + response.getErrorCode());
        }
    }
    
    public void destroy(String regionId, String imageId) throws ApiException{
        Image image = getById(regionId, imageId);
        if(image == null){
            logger.warning("Destroy Image: Image not found. " + imageId);
            return;
        }
        
        DeleteImageRequest request = new DeleteImageRequest();
        request.setRegionId(regionId);
        request.setImageId(imageId);
        
        String imageName = image.getImageName();
        
        DeleteImageResponse response = client.execute(request);
        if(response.isSuccess()){
        	image = getById(regionId, imageId);
        	while(image != null){
                logger.info("Destroy Image: Waiting...... " + Formatter.beanToJsonString(image));
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                image = getById(regionId, imageId);
            }
            logger.info("Destroy Image: Success! " + imageName);
        }else{
            throw new RuntimeException("Destroy Image: Failed!!! " + response.getErrorCode());
        }
    }
    
    public List<Image> getAll(String regionId, String imageName) throws ApiException{
        DescribeImagesRequest request = new DescribeImagesRequest();
        request.setRegionId(regionId);
        request.setImageName(imageName);
        request.setPageSize(Long.valueOf(50));
        
        DescribeImagesResponse response = client.execute(request);
        if(response.isSuccess()){
            return response.getImages();
        }else{
            throw new RuntimeException("Get all images: Failed!!! " + response.getErrorCode());
        }
    }
    
    public Image getLatest(String regionId, String imageName) throws ApiException{
        DescribeImagesRequest request = new DescribeImagesRequest();
        request.setRegionId(regionId);
        request.setImageName(imageName);
        request.setPageSize(Long.valueOf(50));
        
        Image result = null;
        DescribeImagesResponse response = client.execute(request);
        if(response.isSuccess()){
            List<Image> list = response.getImages();
            if(list != null){
                for(Image item : list){
                    if(result == null){
                        result = item;
                    }else if(result.getCreationTime().compareTo(item.getCreationTime()) < 0
                            && "Available".equals(item.getStatus())){
                        result = item;
                    }
                }
            }
        }
//        if(result != null){
//            logger.info("getLatest Image: Success! " + Formatter.beanToJsonString(result));
//        }
        return result;
    }
    
    public Image getById(String regionId, String imageId) throws ApiException{
        DescribeImagesRequest request = new DescribeImagesRequest();
        request.setRegionId(regionId);
        request.setImageId(imageId);
        request.setPageSize(Long.valueOf(50));
        
        DescribeImagesResponse response = client.execute(request);
        if(response.isSuccess()){
            List<Image> list = response.getImages();
            if(list != null && list.size() != 0){
                return list.get(0);
            }
        }
        return null;
    }
}
