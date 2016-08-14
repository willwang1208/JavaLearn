package org.whb.springmvc.repository;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.whb.springmvc.repository.pojo.Column;
import org.whb.springmvc.repository.pojo.Index;
/**
 * 扩展 JdbcOperations 接口
 * @author whb
 *
 */
public interface JdbcExtendOperations extends JdbcOperations {
    
    public List<String> showTables() throws DataAccessException;
    
    public List<Column> descTableColumns(String tableName) throws DataAccessException;
    
    public List<String> descTablePrimaryKeys(String tableName) throws DataAccessException;
    
    public List<Index> descTableIndexInfos(String tableName) throws DataAccessException;
    
    public List<Map<String, Object>> selectPagination(String tableName, String conditions, int pageSize, int pageNo) throws DataAccessException;
    
    public int insertOne(String tableName, Map<String, Object> props) throws DataAccessException;
    
    public int updateByConditions(String tableName, String conditions, Map<String, Object> props) throws DataAccessException;
}
