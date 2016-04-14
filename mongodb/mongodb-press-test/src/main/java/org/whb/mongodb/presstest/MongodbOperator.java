package org.whb.mongodb.presstest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;

public class MongodbOperator {
    
    /** user_data集合中存在_id是连续正整数的20113711条数据文档  */
    public static String sourceCollectionName = "user_data";
    
    public static String copyCollectionName = "user_data_copy";
    
    public static int max_userId = 20113711;
    
    UpdateOptions upsert_true = new UpdateOptions().upsert(true);

    /**
     * 随机_id查询
     * @param database
     */
    public Document find_first(MongoDatabase database){
        MongoCollection<Document> collection = database.getCollection(sourceCollectionName);
        
        int userId = new Random().nextInt(max_userId) + 10000;
        
        return collection.find(Filters.eq("_id", userId)).first();
    }
    
    /**
     * 随机_ids查询
     * @param database
     */
    public List<Document> find_in(MongoDatabase database){
        MongoCollection<Document> collection = database.getCollection(sourceCollectionName);
        
        int len = 20;
        Integer[] userIds = new Integer[len];
        Random random = new Random();
        for(int i = 0; i < len; i ++){
            userIds[i] = random.nextInt(max_userId) + 10000;
        }
        
        List<Document> result = new ArrayList<>();
        FindIterable<Document> iterable = collection.find(Filters.in("_id", userIds));
        MongoCursor<Document> cursor = iterable.iterator();
        while (cursor.hasNext()) {
            result.add(cursor.next());
        }
        
        return result;
    }
    
    /**
     * 随机查询
     * skip[1, 10000]
     * limit[100, 10099]
     * @param database
     */
    public void find(MongoDatabase database){
        MongoCollection<Document> collection = database.getCollection(sourceCollectionName);

        int skip = new Random().nextInt(10000) + 1;
        int limit = new Random().nextInt(10000) + 100;
        
        FindIterable<Document> iterable = collection.find().skip(skip).limit(limit);
        MongoCursor<Document> cursor = iterable.iterator();
        while (cursor.hasNext()) {
            cursor.next();
        }
    }
    
    /**
     * _id查询并插入
     * @param database
     */
    public void find_insert_one(MongoDatabase database){
        Document document = find_first(database);
        
        if(document != null){
            MongoCollection<Document> collection = database.getCollection(copyCollectionName);
//            collection.insertOne(document);
            collection.replaceOne(Filters.eq("_id", document.getInteger("_id")), document, upsert_true);
        }
    }
    
    /**
     * _ids查询并插入
     * @param database
     */
    public void find_insert_many(MongoDatabase database){
        List<Document> documents = find_in(database);
        
        if(documents != null && documents.size() != 0){
            MongoCollection<Document> collection = database.getCollection(copyCollectionName);
            collection.insertMany(documents);
        }
    }
    
    /**
     * _id查询并更新
     * @param database
     */
    public void find_update_one(MongoDatabase database){
        Document document = find_first(database);
        
        if(document != null){
            MongoCollection<Document> collection = database.getCollection(sourceCollectionName);
//            collection.updateOne(Filters.eq("_id", document.getInteger("_id")), );
            collection.replaceOne(Filters.eq("_id", document.getInteger("_id")), document, upsert_true);
        }
    }
    
    /**
     * _id查询并删除
     * @param database
     */
    public void find_delete_one(MongoDatabase database){
        Document document = find_first(database);
        
        if(document != null){
            MongoCollection<Document> collection = database.getCollection(copyCollectionName);
            collection.deleteOne(Filters.eq("_id", document.getInteger("_id")));
        }
    }
}
