package org.whb.app.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.whb.common.mongo.orm.IMongoBean;
import org.whb.common.mongo.orm.MongoCollection;
import org.whb.common.mongo.orm.MongoElement;
import org.whb.common.mongo.orm.MongoElement.IndexType;

@MongoCollection(name = "user_dialog", needShard = true, note = "玩家与管理员的对话")
public class UserDialog implements IMongoBean {

    @MongoElement(name = "_id")
    private String id;    //主键
    
    @MongoElement(name = "a")
    private String title;   //标题，源自玩家提问内容
    
    @MongoElement(name = "b")
    private String type;    //分类
    
    @MongoElement(name = "c")
    private Date time;    //创建时间
    
    @MongoElement(name = "d", indexType = IndexType.Positive)
    private String userCode;    //用户编码
    
    @MongoElement(name = "e")
    private int state;     //问题状态，0未处理，1处理中，2已处理，3已完成
    
    @MongoElement(name = "f")
    private int rate;      //评价
    
    @MongoElement(name = "g")
    private List<UserReply> replies;     //对话列表
    
    @MongoElement(name = "uc")
    private Date uptime;    //更新时间戳
    
    @MongoElement(name = "mu")
    private String managerCode;    //经办人
    
    @MongoElement(name = "muc")
    private Date manauptime;    //管理员更新时间戳
    
    @MongoElement(name = "h")
    private String version;  //版本号
    
    @MongoElement(name = "i")
    private String platform; //安装渠道
    
    @MongoElement(name = "j")
    private String dmodel;//设备类型
    
    @MongoElement(name = "z")
    private String managerTag;  //抓取标记
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public List<UserReply> getReplies() {
        if(replies == null){
            replies = new ArrayList<UserReply>();
        }
        return replies;
    }

    public void setReplies(List<UserReply> replies) {
        this.replies = replies;
    }

    public Date getUptime() {
        return uptime;
    }

    public void setUptime(Date uptime) {
        this.uptime = uptime;
    }

    public String getManagerCode() {
        return managerCode;
    }

    public void setManagerCode(String managerCode) {
        this.managerCode = managerCode;
    }

    public Date getManauptime() {
        return manauptime;
    }

    public void setManauptime(Date manauptime) {
        this.manauptime = manauptime;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getDmodel() {
        return dmodel;
    }

    public void setDmodel(String dmodel) {
        this.dmodel = dmodel;
    }

    public String getManagerTag() {
        return managerTag;
    }

    public void setManagerTag(String managerTag) {
        this.managerTag = managerTag;
    }

}
