package org.whb.common.mongo;

import static org.whb.common.mongo.orm.MongoConstants.ID;
import static org.whb.common.mongo.orm.MongoConstants.NEG_ONE;
import static org.whb.common.mongo.orm.MongoConstants.ZERO;

import java.util.List;
import java.util.Map;

import org.bson.BasicBSONObject;
import org.whb.common.mongo.orm.BeanMongoMap;
import org.whb.common.mongo.orm.IMongoBean;
import org.whb.common.mongo.orm.MongoBeanUtil;
import org.whb.common.mongo.orm.MongoCollection;
import org.whb.common.mongo.wrap.IMongoCondition;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public abstract class AbstractOrmMongoDAO {
    
    public abstract DB getDB();
    
    public DBCollection getCollection(String name){
        return getDB().getCollection(name);
    }
    
    @SuppressWarnings("unchecked")
    public <T extends IMongoBean> T get(Class<T> clazz, Object id) {
        MongoCollection amc = MongoBeanUtil.getMongoCollectionAnnotation(clazz);
        if (amc != null) {
            BasicBSONObject dbo = (BasicBSONObject) getCollection(amc.name()).findOne(id);
            if(dbo != null){
                return (T)MongoBeanUtil.parse(clazz, (BasicDBObject)dbo);
            }
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public <T extends IMongoBean> T getIfNullBuild(Class<T> clazz, Object id) {
        T t = get(clazz, id);
        if(t == null){
            BasicBSONObject dbo = new BasicBSONObject();
            dbo.append(ID, id);
            return (T)MongoBeanUtil.parse(clazz, dbo);
        }
        return t;
    }
    
    @SuppressWarnings("unchecked")
    public <T extends IMongoBean> T get(Class<T> clazz, Object id, Map<String, Object> fields) {
        MongoCollection amc = MongoBeanUtil.getMongoCollectionAnnotation(clazz);
        if (amc != null) {
            BasicBSONObject dbo = (BasicBSONObject) getCollection(amc.name()).findOne(
                    new BasicDBObject(ID, id), null);
            if(dbo != null){
                return (T)MongoBeanUtil.parse(clazz, (BasicDBObject)dbo);
            }
        }
        return null;
    }

    public <T extends IMongoBean> void save(T bean) {
        MongoCollection amc = MongoBeanUtil.getMongoCollectionAnnotation(bean.getClass());
        if (amc != null) {
            BeanMongoMap map = MongoBeanUtil.create(bean);
            getCollection(amc.name()).save(map.toBson());
        }
    }
    
    public <T extends IMongoBean> void insert(T bean) {
        this.insert(bean, null);
    }
    
    public <T extends IMongoBean> void insert(T bean, Object id) {
        MongoCollection amc = MongoBeanUtil.getMongoCollectionAnnotation(bean.getClass());
        if (amc != null) {
            BeanMongoMap map = MongoBeanUtil.create(bean);
            BasicDBObject dbo = map.toBson();
            if(id != null){
                dbo.remove(ID);
                dbo.put(ID, id);
            }
            getCollection(amc.name()).insert(dbo);
        }
    }
    
    public <T extends IMongoBean> void delete(Class<T> clazz, Object id) {
        MongoCollection amc = MongoBeanUtil.getMongoCollectionAnnotation(clazz);
        if (amc != null) {
            getCollection(amc.name()).remove(new BasicDBObject(ID, id));
        }
    }
    
    public <T extends IMongoBean> void delete(Class<T> clazz, IMongoCondition query) {
        MongoCollection amc = MongoBeanUtil.getMongoCollectionAnnotation(clazz);
        if (amc != null) {
            getCollection(amc.name()).remove(query.toDBObject());
        }
    }
    
    public <T extends IMongoBean> void update(Class<T> clazz, Object id, IMongoCondition fields) {
        MongoCollection amc = MongoBeanUtil.getMongoCollectionAnnotation(clazz);
        if (amc != null) {
            getCollection(amc.name()).update(new BasicDBObject(ID, id), fields.toDBObject());
        }
    }
    
    public <T extends IMongoBean> void update(Class<T> clazz, IMongoCondition query, IMongoCondition fields) {
        MongoCollection amc = MongoBeanUtil.getMongoCollectionAnnotation(clazz);
        if (amc != null) {
            getCollection(amc.name()).update(query.toDBObject(), fields.toDBObject(), false, true);
        }
    }
    
    public <T extends IMongoBean> DBCursorAdapter<T> find(Class<T> clazz) {
        return this.find(clazz, null, null, null, NEG_ONE, NEG_ONE);
    }
    
    public <T extends IMongoBean> DBCursorAdapter<T> find(Class<T> clazz, IMongoCondition query) {
        return this.find(clazz, query, null, null, NEG_ONE, NEG_ONE);
    }
    
    public <T extends IMongoBean> DBCursorAdapter<T> find(Class<T> clazz, IMongoCondition query, IMongoCondition fields) {
        return this.find(clazz, query, fields, null, NEG_ONE, NEG_ONE);
    }
    
    public <T extends IMongoBean> DBCursorAdapter<T> find(Class<T> clazz, 
            IMongoCondition query, IMongoCondition fields, IMongoCondition order) {
        return this.find(clazz, query, fields, order, NEG_ONE, NEG_ONE);
    }
    
    public <T extends IMongoBean> DBCursorAdapter<T> find(Class<T> clazz, 
            IMongoCondition query, IMongoCondition fields, IMongoCondition order, int skip, int limit) {
        MongoCollection amc = MongoBeanUtil.getMongoCollectionAnnotation(clazz);
        if (amc != null) {
            DBObject querydbo;
            if(query == null){
                querydbo = new BasicDBObject();
            }else{
                querydbo = query.toDBObject();
            }
            
            DBCursor cursor;
            if(fields == null){
                cursor = getCollection(amc.name()).find(querydbo);
            }else{
                cursor = getCollection(amc.name()).find(querydbo, fields.toDBObject());
            }
            
            if(order != null){
                cursor.sort(order.toDBObject());
            }
            
            if(skip > ZERO){
                cursor.skip(skip);
            }
            
            if(limit > ZERO){
                cursor.limit(limit);
            }
            
            return new DBCursorAdapter<T>(clazz, cursor);
        }
        return null;
    }
    
    public <T extends IMongoBean> T findAndModify(Class<T> clazz, IMongoCondition query, IMongoCondition update){
        return this.findAndModify(clazz, query, null, null, update, false, true, true);
    }
    
    @SuppressWarnings("unchecked")
    public <T extends IMongoBean> T findAndModify(Class<T> clazz, 
            IMongoCondition query, IMongoCondition fields, IMongoCondition order, IMongoCondition update, 
            boolean remove, boolean returnNew, boolean upsert) {
        MongoCollection amc = MongoBeanUtil.getMongoCollectionAnnotation(clazz);
        if (amc != null) {
            DBObject querydbo = null;
            if(query != null){
                querydbo = query.toDBObject();
            }
            
            DBObject fieldsdbo = null;
            if(fields != null){
                fieldsdbo = fields.toDBObject();
            }
            
            DBObject orderdbo = null;
            if(order != null){
                orderdbo = order.toDBObject();
            }
            
            DBObject updatedbo = null;
            if(update != null){
                updatedbo = update.toDBObject();
            }
            
            BasicBSONObject dbo = (BasicBSONObject) getCollection(amc.name()).findAndModify(
                    querydbo, fieldsdbo, orderdbo, remove, updatedbo, returnNew, upsert);
            
            if(dbo != null){
                return (T)MongoBeanUtil.parse(clazz, (BasicDBObject)dbo);
            }
        }
        return null;
    }
    
    public <T extends IMongoBean> long count(Class<T> clazz, IMongoCondition query){
        MongoCollection amc = MongoBeanUtil.getMongoCollectionAnnotation(clazz);
        if (amc != null) {
            if(query == null){
                return getCollection(amc.name()).count();
            }else{
                return getCollection(amc.name()).count(query.toDBObject());
            }
        }
        return ZERO;
    }
    
    public <T extends IMongoBean> List<?> distinct(Class<T> clazz, IMongoCondition query, String key){
        MongoCollection amc = MongoBeanUtil.getMongoCollectionAnnotation(clazz);
        if (amc != null) {
            if(query == null){
                return getCollection(amc.name()).distinct(key);
            }else{
                return getCollection(amc.name()).distinct(key, query.toDBObject());
            }
        }
        return null;
    }
}
