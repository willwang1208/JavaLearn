package org.whb.app.mongo;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.whb.app.vo.GlobalCounter;
import org.whb.common.mongo.AbstractOrmMongoDAO;
import org.whb.common.mongo.wrap.QueryCondition;
import org.whb.common.mongo.wrap.UpdateFieldCondition;

import com.mongodb.DB;

/**
 * ORM主mongo类
 * @author
 *
 */
@Repository
public class MainOrmMongoDAO extends AbstractOrmMongoDAO {
    
    @Autowired
    MongoDBManager mongoDBManager;

    @Override
    public DB getDB() {
        return mongoDBManager.getDb();
    }
    
    @PostConstruct
    public void postConstruct(){
        
    }
    
    public int getNextSequence(String key){
        QueryCondition query = new QueryCondition();
        query.eq("_id", key);
        UpdateFieldCondition update = new UpdateFieldCondition();
        update.inc("a", 1);
        GlobalCounter globalCounter = findAndModify(GlobalCounter.class, query, update);
        return globalCounter.getSequence();
    }
    
}
