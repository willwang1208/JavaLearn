package org.whb.springmvc.repository.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.whb.springmvc.repository.JdbcExtendOperations;
import org.whb.springmvc.repository.pojo.Column;
import org.whb.springmvc.repository.pojo.Index;
/**
 * 继承JdbcTemplate并实现扩展接口方法
 * @author whb
 *
 */
@Repository
public class MysqlJdbcTemplate extends JdbcTemplate implements JdbcExtendOperations{

    @Autowired
    @Qualifier("dbcp")
    DataSource dataSource;
    
    @PostConstruct
    public void injectDataSource() {
        super.setDataSource(dataSource);
    }

    @Override
    public List<String> showTables() throws DataAccessException {
        List<String> result = execute(new StatementCallback<List<String>>() {
            @Override
            public List<String> doInStatement(Statement statement) throws SQLException, DataAccessException {
                List<String> list = new ArrayList<String>();
                ResultSet rs = null;
                try {
                    rs = statement.executeQuery("show tables");
                    while(rs.next()){
                        list.add(rs.getString(1));
                    }
                } finally {
                    JdbcUtils.closeResultSet(rs);
                }
                return list;
            }
        });
        return result;
    }
    
    @Override
    public List<Column> descTableColumns(String tableName) throws DataAccessException {
        Assert.notNull(tableName, "Table name must not be null");
        Connection conn = null;
        Statement statement = null;
        ResultSet rs = null;
        try {
            conn = getDataSource().getConnection();
            //获得列信息
            rs = conn.getMetaData().getColumns(conn.getCatalog(), conn.getSchema(), tableName, null);
            
            List<Column> result = null;
            while (rs.next()) {
                if(result == null){
                    result = new ArrayList<>();
                }
                Column column = new Column();
                column.setName(rs.getString("COLUMN_NAME"));
                column.setType(rs.getInt("DATA_TYPE"));
                column.setLength(rs.getInt("COLUMN_SIZE"));
                column.setScale(rs.getInt("DECIMAL_DIGITS"));
                column.setNullable(rs.getBoolean("NULLABLE"));
                column.setDefaultValue(rs.getString("COLUMN_DEF"));
                column.setRemark(rs.getString("REMARKS"));
                result.add(column);
            }
            rs.close();
            
            return result;
        } catch (SQLException e) {
            //Release Connection early, to avoid potential conn pool deadlock
            JdbcUtils.closeStatement(statement);
            statement = null;
            DataSourceUtils.releaseConnection(conn, getDataSource());
            conn = null;
            throw new InvalidDataAccessResourceUsageException("Describe table columns failed", e);
        } finally {
            JdbcUtils.closeStatement(statement);
            DataSourceUtils.releaseConnection(conn, getDataSource());
        }
    }
    
    @Override
    public List<String> descTablePrimaryKeys(String tableName) throws DataAccessException {
        Assert.notNull(tableName, "Table name must not be null");
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = getDataSource().getConnection();
            //获得主键信息
            rs = conn.getMetaData().getPrimaryKeys(conn.getCatalog(), conn.getSchema(), tableName);
            
            List<String> result = new ArrayList<>();
            while(rs.next()){
                String primaryKey = rs.getString("COLUMN_NAME");
                if(primaryKey != null){
                    result.add(primaryKey);
                }
            }
            return result;
        } catch (SQLException e) {
            //Release Connection early, to avoid potential conn pool deadlock
            JdbcUtils.closeResultSet(rs);
            rs = null;
            DataSourceUtils.releaseConnection(conn, getDataSource());
            conn = null;
            throw new InvalidDataAccessResourceUsageException("Describe table primary keys failed", e);
        } finally {
            JdbcUtils.closeResultSet(rs);
            DataSourceUtils.releaseConnection(conn, getDataSource());
        }
    }
    
    @Override
    public List<Index> descTableIndexInfos(String tableName) throws DataAccessException {
        Assert.notNull(tableName, "Table name must not be null");
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = getDataSource().getConnection();
            //获得索引信息
            rs = conn.getMetaData().getIndexInfo(conn.getCatalog(), conn.getSchema(), tableName, false, false);
            
            List<Index> result = null;
            while(rs.next()){
                if(result == null){
                    result = new ArrayList<>();
                }
                Index index = new Index();
                index.setIndexName(rs.getString("INDEX_NAME"));
                index.setColumnName(rs.getString("COLUMN_NAME"));
                index.setPosition(rs.getInt("ORDINAL_POSITION"));
                index.setNonUnique(rs.getBoolean("NON_UNIQUE"));
                index.setAsc(rs.getString("ASC_OR_DESC").equals("A") ? true : false);
                index.setType(rs.getInt("TYPE"));
                result.add(index);
            }
            return result;
        } catch (SQLException e) {
            //Release Connection early, to avoid potential conn pool deadlock
            JdbcUtils.closeResultSet(rs);
            rs = null;
            DataSourceUtils.releaseConnection(conn, getDataSource());
            conn = null;
            throw new InvalidDataAccessResourceUsageException("Describe table indexes failed", e);
        } finally {
            JdbcUtils.closeResultSet(rs);
            DataSourceUtils.releaseConnection(conn, getDataSource());
        }
    }
    
    @Override
    public List<Map<String, Object>> selectPagination(String tableName, String conditions, int pageSize, int pageNo) throws DataAccessException{
        Assert.notNull(tableName, "Table name must not be null");
        pageSize = pageSize <= 0 ? 50 : pageSize;
        pageNo = pageNo <= 0 ? 1 : pageNo;
        int skip = pageSize * (pageNo - 1);
        String limit = "limit " + skip + "," + pageSize;
        
        MessageFormat mf = new MessageFormat("select * from {0} where {1} {2}");
        String sql = mf.format(new Object[]{tableName, conditions, limit});
        
        return queryForList(sql);
    }
    
    @Override
    public int insertOne(String tableName, Map<String, Object> props) {
        Assert.notNull(tableName, "Table name must not be null");
        Assert.notEmpty(props, "Props must not be empty");
        
        StringBuffer cols = new StringBuffer();
        StringBuffer values = new StringBuffer();
        Iterator<String> iterator = props.keySet().iterator();
        while(iterator.hasNext()){
            String itemKey = iterator.next();
            if(props.get(itemKey) != null){
                cols.append("`");
                cols.append(itemKey);
                cols.append("`,");
                
                values.append("'");
                values.append(String.valueOf(props.get(itemKey)));
                values.append("',");
            }
        }
        
        String insert = "insert into {0} ({1}) values ({2})";
        
        MessageFormat mf = new MessageFormat(insert);
        String sql = mf.format(new Object[] {
                tableName, 
                cols.substring(0, cols.length() - 1), 
                values.substring(0, values.length() - 1)});
        
        return update(sql);
    }
    
    @Override
    public int updateByConditions(String tableName, String conditions, Map<String, Object> props) throws DataAccessException {
        Assert.notNull(tableName, "Table name must not be null");
        Assert.notEmpty(props, "Props must not be empty");
        
        StringBuffer buf = new StringBuffer();
        Iterator<String> iterator = props.keySet().iterator();
        while(iterator.hasNext()){
            String itemKey = iterator.next();
            if(props.get(itemKey) != null){
                buf.append(" `");
                buf.append(itemKey);
                buf.append("`='");
                buf.append(String.valueOf(props.get(itemKey)));
                buf.append("',");
            }else{
                buf.append(" `");
                buf.append(itemKey);
                buf.append("`=null,");
            }
        }
        
        String fields = buf.toString();
        String sql = "update " + tableName + " set " + fields.substring(0, fields.length() -1);
        if(conditions != null){
            sql = sql + " where " + conditions;
        }
        
        return update(sql);
    }
}
