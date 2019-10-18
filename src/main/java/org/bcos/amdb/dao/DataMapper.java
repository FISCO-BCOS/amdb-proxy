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
			@Param("key_value")String keyValue,
			@Param("query_condition")String QueryCondition) throws DataAccessException;
	
	
	public void commitData(@Param("table")String table,
			@Param("fields")String fields,
			@Param("list")List<BatchCommitRequest> list) throws DataAccessException;
	
	public void createTable(@Param("sql")String sql) throws DataAccessException;
	
	public List<Map<String, String>> getTable(@Param("table_name")String table_name);
	
	public void createSysTables();
	
	public void insertSysTables();
	
	public void createSysConsensus();
	
	public void createAccessTables();
	
	public void createCurrentStateTables();
	
	public void createNumber2HashTables();
	
	public void createTxHash2BlockTables();
	
	public void createHash2BlockTables();
	
	public void createCnsTables();
	
	public void createSysConfigTables();
	
	public void createSysBlock2NoncesTables();
	
	public void setMaxAllowedPacket();
	
	public	void setSqlMode();
	
	public void beginTransaction();
	
	public	void commit();
	
	public	void rollback();
	
	public int existTable(String tableName);
    
    public int getMaxBlock();
    
    public List<Map<String, Object>> selectTableDataByNum(@Param("tableName")String table_name, @Param("num")Integer num);
    
}
