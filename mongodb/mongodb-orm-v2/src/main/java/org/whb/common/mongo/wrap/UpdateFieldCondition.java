package org.whb.common.mongo.wrap;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class UpdateFieldCondition implements IMongoCondition {

    public static final String SET = "$set";
    public static final String UNSET = "$unset";
    public static final String PUSH = "$push";
    public static final String PUSH_ALL = "$pushAll";
    public static final String PULL = "$pull";
    public static final String POP = "$pop";
    public static final String ADD_TO_SET = "$addToSet";
    public static final String INC = "$inc";
    
    BasicDBObject dbo = new BasicDBObject();
    
    public void set(String key, Object value){
        addCondition(SET, key, value);
    }
    
    public void unset(String key, Object value){
        addCondition(UNSET, key, value);
    }
    
    public void push(String key, Object value){
        addCondition(PUSH, key, value);
    }
    
    public void pushAll(String key, Object value){
        addCondition(PUSH_ALL, key, value);
    }
    
    public void pull(String key, Object value){
        addCondition(PULL, key, value);
    }
    
    public void pop(String key, Object value){
        addCondition(POP, key, value);
    }
    
    public void inc(String key, Object value){
        addCondition(INC, key, value);
    }
    
    public void addToSet(String key, Object value){
        addCondition(ADD_TO_SET, key, value);
    }
    
    public void addCondition(String option, String key, Object value){
        if(dbo.get(option) == null){
            dbo.put(option, new BasicDBObject(key, value));
        }else{
            ((BasicDBObject)dbo.get(option)).append(key, value);
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
