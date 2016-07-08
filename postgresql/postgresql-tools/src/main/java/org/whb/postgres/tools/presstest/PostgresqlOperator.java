package org.whb.postgres.tools.presstest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.postgresql.util.PGobject;

/**
 * 集合的_id必须是连续的正整数
 * @author whb
 *
 */
public class PostgresqlOperator {
    
    int max_Id;
    
    PostgresqlClient client;
    
    String tableName;
    
    String tableName_copy;
    
    public PostgresqlOperator(PostgresqlClient client, String tableName) throws SQLException {
        this.client = client;
        this.tableName = tableName;
        this.tableName_copy = tableName + "_copy";
        
        Connection connection = null;
        Statement ps_create = null;
        try {
            connection = client.getDataSource().getConnection();
            //建表
            ps_create = connection.createStatement();
            ps_create.executeUpdate("CREATE TABLE " + tableName_copy + " ( "
                    + "_id int primary key, "
                    + "data jsonb not null default '{}' "
                    + ")");
        } catch(SQLException e) {
//            e.printStackTrace();
        } finally {
            close(ps_create);
            close(connection);
        }
        
        Statement ps_count = null;
        ResultSet rs = null;
        try {
            connection = client.getDataSource().getConnection();
            //查询总数
            ps_count = connection.createStatement();
            rs = ps_count.executeQuery("select count(_id) c from " + tableName);
            
            if(rs.next()){
                this.max_Id = rs.getInt("c");
            }
        } finally {
            close(rs);
            close(ps_count);
            close(connection);
        }
    }

    /**
     * 随机_id查询
     * 如果用PreparedStatement
     * @param database
     * @throws SQLException 
     */
    public Object[] find_first() throws SQLException{
        Connection connection = null;
        PreparedStatement ps_select = null;
        ResultSet rs = null;
        try {
            connection = client.getDataSource().getConnection();
            ps_select = connection.prepareStatement("select * from " + tableName + " where _id = ?");
            int userId = new Random().nextInt(max_Id) + 1;
            ps_select.setObject(1, userId);
            rs = ps_select.executeQuery();
            if(rs.next()){
                return new Object[]{rs.getInt("_id"), rs.getObject("data")};
            }
        } finally {
            close(rs);
            close(ps_select);
            close(connection);
        }
        return null;
    }
    public Object[] find_first_not_ps() throws SQLException{
        Connection connection = null;
        Statement ps_select = null;
        ResultSet rs = null;
        try {
            connection = client.getDataSource().getConnection();
            ps_select = connection.createStatement();
            int userId = new Random().nextInt(max_Id) + 1;
            rs = ps_select.executeQuery("select * from " + tableName + " where _id = " + userId);
            if(rs.next()){
                return new Object[]{rs.getInt("_id"), rs.getObject("data")};
            }
        } finally {
            close(rs);
            close(ps_select);
            close(connection);
        }
        return null;
    }
    
    /**
     * 随机_ids查询
     * @param database
     * @throws SQLException 
     */
    public List<Object[]> find_in() throws SQLException{
        int length_ids = 40;
        Integer[] userIds = new Integer[length_ids];
        Random random = new Random();
        for(int i = 0; i < length_ids; i ++){
            userIds[i] = random.nextInt(max_Id) + 1;
        }
        
        Connection connection = null;
        PreparedStatement ps_select = null;
        ResultSet rs = null;
        try {
            connection = client.getDataSource().getConnection();
            String sub = "";
            for(int i = 1; i <= length_ids; i ++){
                sub += "?,";
            }
            ps_select = connection.prepareStatement("select * from " + tableName + " where _id in (" + sub.substring(0, sub.length() - 1) + ")");
            for(int i = 1; i <= length_ids; i ++){
                ps_select.setObject(i, random.nextInt(max_Id) + 1);
            }
            rs = ps_select.executeQuery();
            
            List<Object[]> result = new ArrayList<Object[]>();
            while(rs.next()){
                result.add(new Object[]{rs.getInt("_id"), rs.getObject("data")});
            }
            return result;
        } finally {
            close(rs);
            close(ps_select);
            close(connection);
        }
    }
    
    /**
     * 随机游标查询
     * limit[100, 10099]
     * @param database
     * @throws SQLException 
     */
    public void find() throws SQLException{
        int start = new Random().nextInt(max_Id) + 1;
        int limit = new Random().nextInt(10000) + 100;
        
        Connection connection = null;
        Statement ps_select = null;
        ResultSet rs = null;
        try {
            connection = client.getDataSource().getConnection();
            ps_select = connection.createStatement();
//            rs = ps_select.executeQuery("select * from " + tableName + " limit " + limit + " offset " + start);
            rs = ps_select.executeQuery("select * from " + tableName + " where _id >= " + start + " limit " + limit);
            while(rs.next()){
//                rs.getInt("_id");
            }
        } finally {
            close(rs);
            close(ps_select);
            close(connection);
        }
    }
    
    /**
     * _id查询并插入
     * @param database
     * @throws SQLException 
     */
    public void find_insert_one() throws SQLException{
        Object[] objs = find_first();
        if(objs != null){
            Connection connection = null;
            PreparedStatement ps_insert = null;
            try {
                connection = client.getDataSource().getConnection();
                ps_insert = connection.prepareStatement("insert into " + tableName_copy + " values (?, ?)");
                ps_insert.setObject(1, objs[0]);
                ps_insert.setObject(2, objs[1]);
                ps_insert.executeUpdate();
            } finally {
                close(ps_insert);
                close(connection);
            }
        }
    }
    
    /**
     * _ids查询并插入
     * @param database
     * @throws SQLException 
     */
    public void find_insert_many() throws SQLException{
        List<Object[]> list = find_in();
        if(list != null && list.size() != 0){
            Connection connection = null;
            PreparedStatement ps_insert = null;
            try {
                connection = client.getDataSource().getConnection();
                ps_insert = connection.prepareStatement("insert into " + tableName_copy + " values (?, ?)");
                for(Object[] objs: list){
                    ps_insert.setObject(1, objs[0]);
                    ps_insert.setObject(2, objs[1]);
                    ps_insert.executeUpdate();
                }
            } finally {
                close(ps_insert);
                close(connection);
            }
        }
    }
    
    /**
     * _id查询并更新
     * @param database
     * @throws SQLException 
     */
    public void find_update_one() throws SQLException{
        Object[] objs = find_first();
        if(objs != null){
            Connection connection = null;
            PreparedStatement ps_update = null;
            try {
                connection = client.getDataSource().getConnection();
                ps_update = connection.prepareStatement("update " + tableName + " set data = ? where _id = ?");
                ps_update.setObject(2, objs[0]);
                ps_update.setObject(1, objs[1]);
                ps_update.executeUpdate();
            } finally {
                close(ps_update);
                close(connection);
            }
        }
    }
    public void find_update_one_not_ps() throws SQLException{
        Object[] objs = find_first_not_ps();
        if(objs != null){
            Connection connection = null;
            Statement ps_update = null;
            try {
                connection = client.getDataSource().getConnection();
                ps_update = connection.createStatement();
                ps_update.executeUpdate("update " + tableName + " set data = '" + ((PGobject)objs[1]).getValue() + "' where _id = " + objs[0]);
            } finally {
                close(ps_update);
                close(connection);
            }
        }
    }
    
    /**
     * _id查询并删除
     * @param database
     * @throws SQLException 
     */
    public void find_delete_one() throws SQLException{
        Object[] objs = find_first();
        if(objs != null){
            Connection connection = null;
            PreparedStatement ps_delete = null;
            try {
                connection = client.getDataSource().getConnection();
                ps_delete = connection.prepareStatement("delete from " + tableName_copy + " where _id = ?");
                ps_delete.setObject(1, objs[0]);
                ps_delete.executeUpdate();
            } finally {
                close(ps_delete);
                close(connection);
            }
        }
    }
    
    public void close(AutoCloseable ac){
        if(ac != null){
            try {
                ac.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
