package org.whb.common.mongo.wrap;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class QueryCondition implements IMongoCondition{
    
    public static final String EQ = "";
    public static final String NE = "$ne";
    public static final String GT = "$gt";
    public static final String GTE = "$gte";
    public static final String LT = "$lt";
    public static final String LTE = "$lte";
    public static final String ALL = "$all";
    public static final String EXISTS = "$exists";
    public static final String IN = "$in";
    public static final String NIN = "$nin";
    public static final String MOD = "$mod";
    public static final String SIZE = "$size";
    public static final String NOT = "$not";
    public static final String ELEM_MATCH = "$elemMatch";
    public static final String OR = "$or";
    public static final String NOR = "$nor";
    
    BasicDBObject dbo = new BasicDBObject();
    
    public QueryCondition eq(String key, Object value){
        addCondition(EQ, key, value);
        return this;
    }
    
    public QueryCondition ne(String key, Object value){
        addCondition(NE, key, value);
        return this;
    }
    
    public QueryCondition gt(String key, Object value){
        addCondition(GT, key, value);
        return this;
    }
    
    public QueryCondition gte(String key, Object value){
        addCondition(GTE, key, value);
        return this;
    }
    
    public QueryCondition lt(String key, Object value){
        addCondition(LT, key, value);
        return this;
    }
    
    public QueryCondition lte(String key, Object value){
        addCondition(LTE, key, value);
        return this;
    }
    
    public QueryCondition all(String key, Object value){
        addCondition(ALL, key, value);
        return this;
    }
    
    public QueryCondition exists(String key, Object value){
        addCondition(EXISTS, key, value);
        return this;
    }
    
    public QueryCondition in(String key, Object value){
        addCondition(IN, key, value);
        return this;
    }
    
    public QueryCondition nin(String key, Object value){
        addCondition(NIN, key, value);
        return this;
    }
    
    public QueryCondition mod(String key, Object value){
        addCondition(MOD, key, value);
        return this;
    }
    
    public QueryCondition size(String key, Object value){
        addCondition(SIZE, key, value);
        return this;
    }
    
    public QueryCondition not(String key, Object value){
        addCondition(NOT, key, value);
        return this;
    }
    
    public QueryCondition elemMatch(String key, DBObject value){
        addCondition(ELEM_MATCH, key, value);
        return this;
    }
    
    public void or(DBObject value){
        if(dbo.get(OR) == null){
            dbo.put(OR, new BasicDBList());
        }
        ((BasicDBList)dbo.get(OR)).add(value);
    }
    
    public void nor(DBObject value){
        if(dbo.get(NOR) == null){
            dbo.put(NOR, new BasicDBList());
        }
        ((BasicDBList)dbo.get(NOR)).add(value);
    }
    
    public void addCondition(String option, String key, Object value){
        if(EQ.equals(option)){
            dbo.put(key, value);
        } else {
            if(dbo.get(key) == null){
                dbo.put(key, new BasicDBObject(option, value));
            }else{
                ((BasicDBObject)dbo.get(key)).append(option, value);
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
