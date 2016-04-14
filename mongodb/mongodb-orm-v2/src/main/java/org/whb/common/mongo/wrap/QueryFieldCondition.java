package org.whb.common.mongo.wrap;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class QueryFieldCondition implements IMongoCondition{

    BasicDBObject dbo = new BasicDBObject();
    
    public void include(String... keys){
        for(String key: keys){
            if(key != null){
                dbo.append(key, 1);
            }
        }
    }
    
    public void exclude(String... keys){
        for(String key: keys){
            if(key != null){
                dbo.append(key, 0);
            }
        }
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
