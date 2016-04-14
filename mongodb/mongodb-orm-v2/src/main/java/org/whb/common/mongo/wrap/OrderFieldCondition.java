package org.whb.common.mongo.wrap;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class OrderFieldCondition implements IMongoCondition{

    BasicDBObject dbo = new BasicDBObject();
    
    public void asc(String key){
        dbo.append(key, 1);
    }
    
    public void desc(String key){
        dbo.append(key, -1);
    }
    
    @Override
    public DBObject toDBObject(){
        return dbo;
    }
    
    @Override
    public void setDBObject(DBObject dbo) {
        this.dbo = (BasicDBObject)dbo;
    }
}
