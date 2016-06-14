package org.whb.postgres.tools.bsonimport;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.zip.GZIPInputStream;

import org.bson.BSONDecoder;
import org.bson.BSONObject;
import org.bson.BasicBSONDecoder;
import org.bson.types.ObjectId;
import org.postgresql.util.PGobject;

import com.mongodb.util.JSON;
/**
 * 把mongodb导出的bson数据导入到postgresql，适用于mongodb各个collection内的文档结构格式一致
 * postgresql表结构为固定样式: _id, data(jsonb)
 * 
 * java -jar BsonImport.jar jdbc:postgresql://172.16.0.3:5432/mydb3 postgres postgres E://user_web_invite_data.bson.gz
 * java -jar BsonImport.jar jdbc:postgresql://172.16.0.3:5432/mydb3 postgres postgres E://
 * 
 * @author whb
 *
 */
public class BsonImport {
    
    public static boolean multiThread = true;

    public static void main(String[] args) {
        String url = args[0];
        String user = args[1];
        String password = args[2];
        String path = args[3];
        
//        String path = "E://user_web_invite_data.bson.gz";
////        String path = "E://";
//        String url = "jdbc:postgresql://172.16.0.3:5432/mydb3";
//        String user = "postgres";
//        String password = "postgres";
        
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        
        File dir = new File(path);
        
        if(dir.isDirectory()){
            File[] files = dir.listFiles();
            if(files != null){
                for(File file: files){
                    if(file.getName().contains("metadata")){
                        continue;
                    }
                    if(multiThread){
                        new Thread(){
                            @Override
                            public void run() {
                                exec(file, url, user, password);
                            }
                        }.start();
                    }else{
                        exec(file, url, user, password);
                    }
                }
            }
            
        }else if(dir.isFile()){
            exec(dir, url, user, password);
        }
    }
    
    public static void exec(File file, String url, String user, String password){
        if(file.isFile()){
            String tableName = getTableName(file);
            InputStream inputStream = getFileInputStream(file);
            if(tableName != null && inputStream != null){
                BSONDecoder decoder = new BasicBSONDecoder();
                Connection connection = null;
                PreparedStatement ps_insert = null;
                PreparedStatement ps_create = null;
                try {
                    connection = getConnection(url, user, password);
                    if(connection != null){
                        //插入数据
                        ps_insert = connection.prepareStatement("INSERT INTO " + tableName + " VALUES (?, ?)");
                        int count = 0;
                        while (inputStream.available() > 0) {
                            BSONObject obj = null;
                            try {
                                obj = decoder.readObject(inputStream);
                            } catch (EOFException e) {
                                ps_insert.executeBatch();
                                System.out.println(">>######################## Table " + tableName + " is done. Count: " + count);
                                break;
                            }
                            
                            Object id = obj.get("_id");
                            String jsonString = JSON.serialize(obj);
                            
                            if(count == 0){
                                //建表
                                String idType;
                                if(id instanceof Integer){
                                    idType = "int";
                                }else if(id instanceof Long){
                                    idType = "int8";
                                }else if(id instanceof ObjectId){
                                    idType = "char(" + id.toString().length() + ")";
                                }else{
                                    idType = "varchar(" + id.toString().length() * 10 + ")";  //FIXME
                                }
                                ps_create = connection.prepareStatement("CREATE TABLE " + tableName + " ( "
                                        + "_id " + idType + " primary key, "
                                        + "data jsonb not null default '{}' "
                                        + ")");
                                ps_create.executeUpdate();
                            }
                            
                            ps_insert.setObject(1, id);
                            PGobject jsonbObject = new PGobject();
                            jsonbObject.setType("jsonb");
                            jsonbObject.setValue(jsonString);
                            ps_insert.setObject(2, jsonbObject);
                            
                            ps_insert.addBatch();
                            
                            count ++;
                            
                            if(count % 500 == 0){
                                ps_insert.executeBatch();
                            }
                            if(count % 5000 == 0){
                                System.out.println(">> " + tableName + " count: " + count);
                            }
                        }
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    close(ps_create);
                    close(ps_insert);
                    close(connection);
                    close(inputStream);
                }
            }
        }
    }
    
    public static InputStream getFileInputStream(File file){
        String fileName = file.getName();
        try {
            if(fileName.contains(".gz")){
                return new BufferedInputStream(new GZIPInputStream(new FileInputStream(file)));
            }
            return new BufferedInputStream(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static String getTableName(File file){
        String fileName = file.getName();
        
        String tableName;
        int endIndex = fileName.indexOf(".");
        if(endIndex != -1){
            tableName = fileName.substring(0, endIndex);
        }else{
            tableName = fileName;
        }
        
        if(tableName.length() > 0){
            return tableName;
        }
        return null;
    }
    
    public static Connection getConnection(String url, String user, String password){
        try {
            if("null".equalsIgnoreCase(password)){
                password = null;
            }
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static void close(AutoCloseable ac){
        if(ac != null){
            try {
                ac.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
