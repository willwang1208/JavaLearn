package org.whb.mongodb.chunk;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.MaxKey;
import org.bson.types.MinKey;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

/**
 * /opt/jdk1.8.0_66/bin/java -jar MongodbSplitJumboChunk.jar 172.31.48.14:20000 > result_file
 * /opt/jdk1.8.0_66/bin/java -jar MongodbSplitJumboChunk.jar 172.31.48.14:20000 only > tmp_cmds_file
 * 
 * @author whb
 *
 */
public class SplitJumbo {
    
    public static void main(String[] args) {
        String host = args[0];   //172.31.48.14:20000
        String getCmdOnly = args[1];
        
        String uri = "mongodb://" + host + "/?maxPoolSize=5";
        
        final MongoClient client = new MongoClient(new MongoClientURI(uri));
        
        MongoDatabase configdb = client.getDatabase("config");
        
//      Document result = configdb.runCommand(new Document("buildInfo", 1));
//      Document result = configdb.runCommand(new Document("$eval", "function(){return 1}"));
//      Document result = configdb.runCommand(new Document("$eval", "function(){return sh.getBalancerLockDetails()}"));
//      Document result = configdb.runCommand(new Document("$eval", "db.runCommand(\"ismaster\")"));
//      Document result = configdb.runCommand(new Document("$eval", "sh._checkMongos()"));
//      System.out.println(result);
        
        //db.chunks.find({ns:"jelly_360.user_type_data",jumbo:true})
        MongoCollection<Document> chunks = configdb.getCollection("chunks");
        
        List<Document> jumbos = new ArrayList<>();
        FindIterable<Document> iterable = chunks.find(Filters.eq("jumbo", true));
        MongoCursor<Document> cursor = iterable.iterator();
        while (cursor.hasNext()) {
            jumbos.add(cursor.next());
        }
        
        //sh.splitAt("jelly_360.user_type_data",{_id:NumberLong("-9123171244068174933")})
//      String format = "sh._adminCommand({split: \"%s\", middle: {_id: NumberLong(\"%d\")}}, true)";
        String format = "sh.splitAt(\"%s\",{_id:NumberLong(\"%d\")})";
        
        List<String> cmds = new ArrayList<>();
        for(Document jumbo: jumbos){
            String ns = jumbo.getString("ns");
            long min = getRangeValue(jumbo.get("min", Document.class).get("_id"));
            long max = getRangeValue(jumbo.get("max", Document.class).get("_id"));
            long mid = (max - min) / 2 + min;
            
            cmds.add(String.format(format, ns, mid));
        }
        
        //仅输出split命令，用于通过其他方式执行这些命令，例如mongodb_split_jumbo_chunks.sh
        if(getCmdOnly != null){
            for(String cmd: cmds){
                System.out.println(cmd);
            }
        }else{
            for(String cmd: cmds){
                System.out.println(cmd);
                
                //FIXME
                //the uri is mongos but it throw "Error: not connected to a mongos". why?
                Document result = configdb.runCommand(new Document("$eval", cmd));
                
                System.out.println(result);
            }
        }
        
        Runtime.getRuntime().addShutdownHook(
            new Thread(){
                public void run(){
                    client.close();
                }
            }
        );
    }
    
    public static Long getRangeValue(Object value) {
        if(value instanceof Long){
            return (Long)value;
        }else if(value instanceof MinKey){
            return -9223372036854775807L;
        }else if(value instanceof MaxKey){
            return 9223372036854775807L;
        }else{
            throw new RuntimeException("Are you kidding me? Value is " + value);
        }
    }

}
