package org.whb.app.vo;

import org.whb.common.mongo.orm.IMongoBean;
import org.whb.common.mongo.orm.MongoCollection;
import org.whb.common.mongo.orm.MongoElement;

@MongoCollection(name = "global_counter")
public class GlobalCounter implements IMongoBean {
    
    public static final String ID = "_id";
    public static final String Sequence = "a";

    @MongoElement(name = ID)
    private String id;    //主键
    
    @MongoElement(name = Sequence)
    private int sequence;    //序号

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }   
    
}
