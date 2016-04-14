package org.whb.common.mongo.wrap;

import com.mongodb.DBObject;

public interface IMongoCondition {

    public DBObject toDBObject();
    
    public void setDBObject(DBObject dbo);
    
}
