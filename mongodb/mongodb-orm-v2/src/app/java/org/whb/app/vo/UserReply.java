package org.whb.app.vo;

import java.util.Date;

import org.whb.common.mongo.orm.IMongoBean;
import org.whb.common.mongo.orm.MongoElement;

public class UserReply implements IMongoBean {
    
    @MongoElement(name = "id")
    private String id;     //id

    @MongoElement(name = "a")
    private String userCode;     //回复人编码
    
    @MongoElement(name = "b")
    private Date time;      //时间
    
    @MongoElement(name = "c")
    private String content;     //回复内容
    
    @MongoElement(name = "d")
    private String pic;     //图片
    
    @MongoElement(name = "e")
    private String userName;     //回复人名称
    
    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
