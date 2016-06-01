package org.whb.mongodb.presstest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
/**
 * 集合的_id必须是连续的正整数
 * @author whb
 *
 */
public class MongodbOperator {
    
    int max_Id;
    
    UpdateOptions update_options;

    MongoCollection<Document> collection;
    
    MongoCollection<Document> collection_copy;
    
    public MongodbOperator(MongoClient client, String dbName, String collectionName) {
        MongoDatabase database = client.getDatabase(dbName);
        
        this.collection = database.getCollection(collectionName);
        this.collection_copy = database.getCollection(collectionName + "_copy");
        this.update_options = new UpdateOptions().upsert(true);
        this.max_Id = (int)collection.count();
    }

    /**
     * 随机_id查询
     * @param database
     */
    public Document find_first(){
        int userId = new Random().nextInt(max_Id) + 1;
        return collection.find(Filters.eq("_id", userId)).first();
    }
    
    /**
     * 随机_ids查询
     * @param database
     */
    public List<Document> find_in(){
        int length_ids = 40;
        Integer[] userIds = new Integer[length_ids];
        Random random = new Random();
        for(int i = 0; i < length_ids; i ++){
            userIds[i] = random.nextInt(max_Id) + 1;
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
     * 随机游标查询
     * limit[100, 10099]
     * @param database
     */
    public void find(){
        int start = new Random().nextInt(max_Id) + 1;
        int limit = new Random().nextInt(10000) + 100;
        
        FindIterable<Document> iterable = collection.find(Filters.gte("_id", start)).limit(limit);
        MongoCursor<Document> cursor = iterable.iterator();
        while (cursor.hasNext()) {
            cursor.next();
        }
    }
    
    /**
     * _id查询并插入
     * @param database
     */
    public void find_insert_one(){
        Document document = find_first();
        if(document != null){
//            collection.insertOne(document);
            collection_copy.replaceOne(Filters.eq("_id", document.getInteger("_id")), document, update_options);
        }
    }
    
    /**
     * _ids查询并插入
     * @param database
     */
    public void find_insert_many(){
        List<Document> documents = find_in();
        if(documents != null && documents.size() != 0){
            collection_copy.insertMany(documents);
        }
    }
    
    /**
     * _id查询并更新
     * @param database
     */
    public void find_update_one(){
        Document document = find_first();
        if(document != null){
//            collection.updateOne(Filters.eq("_id", document.getInteger("_id")), );
            collection.replaceOne(Filters.eq("_id", document.getInteger("_id")), document, update_options);
        }
    }
    
    /**
     * _id查询并删除
     * @param database
     */
    public void find_delete_one(){
        Document document = find_first();
        if(document != null){
            collection_copy.deleteOne(Filters.eq("_id", document.getInteger("_id")));
        }
    }
}
