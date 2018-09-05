package org.bcos.amdb.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.bcos.amdb.dto.BatchCommitRequest;
import org.springframework.dao.DataAccessException;

public interface DataMapper {
	//public List<Map<String, Object> > queryData(@Param("table")String table, @Param("num")Integer num, @Param("indices_equal")String indices_equal, @Param("where")String where);
	public List<Map<String, Object> > queryData(@Param("table")String table,
			@Param("num")Integer num,
			@Param("indices_equal")String indices_equal,
			@Param("key_field")String keyField,
			@Param("key_value")String keyValue) throws DataAccessException;
	
	
	public void commitData(@Param("table")String table,
			@Param("fields")String fields,
			@Param("list")List<BatchCommitRequest> list) throws DataAccessException;
	
	public void createTable(@Param("sql")String sql) throws DataAccessException;
	
	public List<Map<String, String>> getTable(@Param("table_name")String table_name);
	
	public void createSysTables();
	
	public void insertSysTables();
	
	public void createSysMiners();
	
}
