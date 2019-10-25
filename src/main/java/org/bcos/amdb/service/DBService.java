package org.bcos.amdb.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bcos.amdb.cache.MemoryCache;
import org.bcos.amdb.dao.DataMapper;
import org.bcos.amdb.dto.BatchCommitRequest;
import org.bcos.amdb.dto.CommitRequest;
import org.bcos.amdb.dto.CommitResponse;
import org.bcos.amdb.dto.Condition;
import org.bcos.amdb.dto.Header;
import org.bcos.amdb.dto.InfoRequest;
import org.bcos.amdb.dto.InfoResponse;
import org.bcos.amdb.dto.SelectRequest;
import org.bcos.amdb.dto.SelectResponse;
import org.bcos.amdb.dto.SelectResponse2;
import org.bcos.amdb.dto.TableData;
import org.bcos.amdb.enums.AmdbExceptionCodeEnums;
import org.bcos.amdb.exception.AmdbException;
import org.bcos.amdb.dto.Request;
import org.bcos.amdb.dto.Response;
import org.bcos.amdb.dto.SelectByNumRequest;

public class DBService {

	private static Logger logger = LoggerFactory.getLogger(DBService.class);
	private static final String SYSTABLE = "_sys_tables_";
	private static final String DETAIL_TABLE_POST_FIX = "d_";
	
	private static final int PAGE_SIZE = 10000;

	public void initTables() {
		logger.info("Start create table:");
		try {
			dataMapper.createSysTables();
			dataMapper.insertSysTables();
			// add by darrenyin
			dataMapper.setMaxAllowedPacket();
			dataMapper.setSqlMode();
			dataMapper.createSysConsensus();
			dataMapper.createAccessTables();
			dataMapper.createCurrentStateTables();
			dataMapper.createNumber2HashTables();
			dataMapper.createTxHash2BlockTables();
			dataMapper.createHash2BlockTables();
			dataMapper.createCnsTables();
			dataMapper.createSysConfigTables();
			dataMapper.createSysBlock2NoncesTables();

		} catch (Exception e) {
			logger.debug("Create table error: " + e.getMessage());
		}
		logger.info("Create table successful!");
	}

	@Transactional(transactionManager = "transactionManager")
	public String process(String content) throws JsonProcessingException {

		Response response = new Response();
		Object result = null;

		try {
			logger.trace("Process receives the request: {}", content);
			Header header = objectMapper.readValue(content, Header.class);

			if (header.getOp() == null) {
				throw new Exception("Failed to parse header.op:" + content);
			}

			if (header.getOp().equals("info")) {
				Request<InfoRequest> request = objectMapper.readValue(content,
						new TypeReference<Request<InfoRequest>>() {
						});
				result = info(request.getParams());

			} else if (header.getOp().equals("select")) {
				Request<SelectRequest> request = objectMapper.readValue(content,
						new TypeReference<Request<SelectRequest>>() {
						});

				SelectRequest params = request.getParams();
				try {
					result = select(params);
				} catch (DataAccessException e) {
					createTable(params.getTable());
					result = select(params);
				}

			} else if (header.getOp().equals("select2")) {
				Request<SelectRequest> request = objectMapper.readValue(content,
						new TypeReference<Request<SelectRequest>>() {
						});

				SelectRequest params = request.getParams();
				try {
					result = select2(params);
				} catch (DataAccessException e) {
					createTable(params.getTable());
					result = select2(params);
				}

			} else if(header.getOp().equals("selectbynum")){
                Request<SelectByNumRequest> request = objectMapper.readValue(content,
                        new TypeReference<Request<SelectByNumRequest>>() {
                        });
                SelectByNumRequest params = request.getParams();
                if(dataMapper.existTable(params.getTableName()) != 1){
                    throw new AmdbException(AmdbExceptionCodeEnums.NO_TABLE_MESSAGE);
                }else if(params.getNum() > dataMapper.getMaxBlock()){
                    throw new AmdbException(AmdbExceptionCodeEnums.BLOCK_NUM_ERROR_MESSAGE);
                }else{
                    result = selectByNum(params);
                }               
            } else if (header.getOp().equals("commit")) {
				Request<CommitRequest> request = objectMapper.readValue(content,
						new TypeReference<Request<CommitRequest>>() {
						});

				try {
					result = commit(request.getParams());
				} catch (DataAccessException e) {// process fail，create table，send request again
					/*
					 * List<TableData> data = request.getParams().getData(); for (TableData
					 * tableData : data) { String table_name = tableData.getTable();
					 * createTable(table_name); result = commit(request.getParams()); }
					 */
					logger.error("commitDB error:", e);
				}
			} else {
				logger.error("Unknown request:{}", header.getOp());

				throw new Exception("Unknown request:" + header.getOp());
			}

			response.setCode(0);
		} catch (Exception e) {
			logger.error("Error:", e);

			response.setCode(1);
		}

		if (result != null) {
			response.setResult(result);
		} else {
			logger.error("Result is null");
		}

		String out;
		out = objectMapper.writeValueAsString(response);

		return out;
	}

	public InfoResponse info(InfoRequest request) throws IOException {
		String table = request.getTable();
		Table info = getTable(table);
		InfoResponse response = new InfoResponse();
		if (info == null) {
			return response;
		}
		response.setKey(info.getKey());
		response.setIndices(info.getIndices());

		return response;
	}
	
	public List<Map<String, Object>> selectByNum(SelectByNumRequest request){
        String tableName;
        if(request.getTableName().equals(SYSTABLE)){
            tableName = request.getTableName();
        }else{
            tableName = getDetailTableName(request.getTableName());
        }
        
        long preIndex = 0;
        int pageCount = PAGE_SIZE;
        List<Map<String, Object>> resultMap = new ArrayList<>();
        while(pageCount == PAGE_SIZE){
            List<Map<String, Object>> tempResult = dataMapper.selectTableDataByNum(tableName, request.getNum(), preIndex, PAGE_SIZE);
            if(tempResult != null && tempResult.size() != 0){
                preIndex = Long.valueOf(String.valueOf(tempResult.get(tempResult.size()-1).get("_id_")));
                resultMap.addAll(tempResult);
                pageCount = tempResult.size();
            }else{
                pageCount = 0;
            }    
        }
        
        for (Map<String, Object> map : resultMap) {
            map.remove("pk_id");
        }
        return resultMap;
    }
	
	private String getDetailTableName(String tableName){
        String detailTableName;
        if(!tableName.endsWith("_")){
            detailTableName = tableName + "_" + DETAIL_TABLE_POST_FIX;
        }else{
            detailTableName = tableName + DETAIL_TABLE_POST_FIX;
        }
        return detailTableName;
    }

	public String getSqlForSelect(SelectRequest request) throws Exception {
		String key = request.getKey();
		List<List<String>> condition = request.getCondition();
		logger.debug("key:{} condition:{}", key, condition);
		Map<String, Condition> conditionmap = new HashMap<String, Condition>();
		if (condition != null) {
			for (List<String> cond : condition) {
				if (cond.size() < 3) {
					throw new Exception("Invalid cond:" + cond.stream().reduce((a, b) -> a + ", " + b));
				}

				Condition condItem = new Condition();
				condItem.setOp(Condition.valueOf(Integer.parseInt(cond.get(1))));
				condItem.setValue(cond.get(2));

				conditionmap.put(cond.get(0), condItem);
			}
		}

		StringBuilder sb = new StringBuilder();
		Iterator<java.util.Map.Entry<String, Condition>> entries = conditionmap.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<String, Condition> entry = entries.next();
			logger.debug("key:{}", entry.getKey());
			Condition value = entry.getValue();
			
			
			String strKeyEscape = getStrSql(entry.getKey());

			if (value.getOp() == org.bcos.amdb.dto.Condition.ConditionOp.eq) {
				sb.append(" and `").append(strKeyEscape).append("` = ");
				sb.append("'").append(getStrSql(value.getValue()));
				sb.append("'");
			}

			else if (value.getOp() == org.bcos.amdb.dto.Condition.ConditionOp.ne) {
				sb.append(" and `").append(strKeyEscape).append("` != ");
				sb.append("'").append(getStrSql(value.getValue()));
				sb.append("'");
			}

			else if (value.getOp() == org.bcos.amdb.dto.Condition.ConditionOp.gt) {
				sb.append(" and `").append(strKeyEscape).append("` > ");
				sb.append("'").append(getStrSql(value.getValue()));
				sb.append("'");
			}

			else if (value.getOp() == org.bcos.amdb.dto.Condition.ConditionOp.ge) {
				sb.append(" and `").append(strKeyEscape).append("` >= ");
				sb.append("'").append(getStrSql(value.getValue()));
				sb.append("'");
			}

			else if (value.getOp() == org.bcos.amdb.dto.Condition.ConditionOp.lt) {
				sb.append(" and `").append(strKeyEscape).append("` < ");
				sb.append("'").append(getStrSql(value.getValue()));
				sb.append("'");
			}

			else if (value.getOp() == org.bcos.amdb.dto.Condition.ConditionOp.le) {
				sb.append(" and `").append(strKeyEscape).append("` <=");
				sb.append("'").append(getStrSql(value.getValue()));
				sb.append("'");
			} else {
				logger.error("error condition op:{}", value.getOp());
				continue;
			}
		}

		String conditionsql = sb.toString();
		logger.debug("condition sql:{}", conditionsql);
		return conditionsql;
	}

	public SelectResponse select(SelectRequest request) throws Exception {
		String table = request.getTable();
		Integer num = request.getNum();
		String key = request.getKey();
		Table info = getTable(table);

		if (info == null) {
			SelectResponse response = new SelectResponse();
			return response;
		}
		String conditionsql = getSqlForSelect(request);

		SelectResponse response = new SelectResponse();
		List<Map<String, Object>> data = null;
		logger.debug("key:{} table:{} number:{} index{}  key_value:{} condition:{}", key, table, num,
				info.indicesEqualString(), info.getKey(), conditionsql);

		StringBuilder _table = new StringBuilder();
		_table.append("`");
		_table.append(table);
		_table.append("`");

		data = dataMapper.queryData(_table.toString(), num, info.indicesEqualString(), info.getKey(), key,
				conditionsql);

		if (!data.isEmpty()) {
			logger.debug("condition sql:{} has data", conditionsql);
			Map<String, Object> f = data.get(0);
			response.setColumns(f.keySet());

			List<List<Object>> allValues = new ArrayList<List<Object>>();
			for (Map<String, Object> line : data) {
				List<Object> values = new ArrayList<Object>();
				for (String field : line.keySet()) {
					values.add(line.get(field));
				}
				allValues.add(values);
			}
			response.setData(allValues);
		} else {
			logger.debug("condition sql:{} has no data", conditionsql);
			response.setColumns(new HashSet<String>());
			response.setData(new ArrayList<List<Object>>());
		}
		return response;
	}

	public SelectResponse2 select2(SelectRequest request) throws Exception {
		String table = request.getTable();
		Integer num = request.getNum();
		String key = request.getKey();
		Table info = getTable(table);

		if (info == null) {
			SelectResponse2 response = new SelectResponse2();
			return response;
		}
		String conditionsql = getSqlForSelect(request);

		SelectResponse2 response = new SelectResponse2();
		List<Map<String, Object>> data = null;
		logger.debug("key:{} table:{} number:{} index{}  key_value:{} condition:{}", key, table, num,
				info.indicesEqualString(), info.getKey(), conditionsql);

		StringBuilder _table = new StringBuilder();
		_table.append("`");
		_table.append(table);
		_table.append("`");

		data = dataMapper.queryData(_table.toString(), num, info.indicesEqualString(), info.getKey(), key,
				conditionsql);

		if (!data.isEmpty()) {
			logger.debug("condition sql:{} has data", conditionsql);
			List<Map<String, Object>> allValues = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> line : data) {
				Map<String, Object> map = new HashMap<String, Object>();
				for (String field : line.keySet()) {
					map.put(field, line.get(field));
				}
				allValues.add(map);
			}
			response.setColumnValue(allValues);

		} else {
			logger.debug("condition sql:{} has no data", conditionsql);
			List<Map<String, Object>> allValues = new ArrayList<Map<String, Object>>();
			response.setColumnValue(allValues);
		}
		return response;
	}

	@Transactional
	public CommitResponse commit(CommitRequest request) throws Exception {
		Integer count = 0;

		processNewTable(request.getBlockHash(), request.getNum(), request.getData());

		Map<Table, List<String>> update = DBReplace(request.getBlockHash(), request.getNum(), request.getData());

		count = update.size();
		CommitResponse response = new CommitResponse();
		response.setCount(count);
		return response;
	}

	@Transactional
	public void processNewTable(String hash, Integer num, List<TableData> data) throws Exception {
		for (TableData tableData : data) {
			if (tableData.getTable().equals("_sys_tables_")) {
				// create table if _sys_tables got new table
				for (Map<String, String> line : tableData.getEntries()) {
					// new table
					String tableName = line.get("table_name");
					String keyField = line.get("key_field");
					String valueField = line.get("value_field");

					createTable(tableName, keyField, valueField);
				}

				break;
			}
		}
	}

	private Map<String, List<BatchCommitRequest>> getCommitFieldNameAndValue(String hash, Integer num, TableData tableData) {
		Map<String, List<BatchCommitRequest>> datalist = new HashMap<String, List<BatchCommitRequest>>();
		int index = 0;
		Map<Set<String>, List<Integer>> setlist = new HashMap<Set<String>, List<Integer>>();
		for (Map<String, String> entry : tableData.getEntries()) {
			Set<String> fieldSet = new HashSet<String>();
			for (Map.Entry<String, String> line : entry.entrySet()) {
				logger.debug("getkey:{} getvalue:{}",line.getKey(), line.getValue());
				if (line.getKey().equals("_num_") || line.getKey().equals("_hash_")) {
					continue;
				}
				fieldSet.add(line.getKey());
			}
			
			if(setlist.get(fieldSet) == null)
			{
				List<Integer> indexlist = new ArrayList<Integer>();
				indexlist.add(index);
				setlist.put(fieldSet, indexlist);
			}
			else
			{
				setlist.get(fieldSet).add(index);
			}
			++index;
		}
		
		Iterator<Map.Entry<Set<String>, List<Integer>>> entries = setlist.entrySet().iterator();
		while (entries.hasNext()) {
			List<BatchCommitRequest> list = new ArrayList<>();
			String _fields = null;
			Map.Entry<Set<String>, List<Integer>> entry = entries.next();
			List<Integer> indexList = entry.getValue();
			for (Integer loopindex : indexList) {
				StringBuffer sbFields = new StringBuffer();
				StringBuffer sbValues = new StringBuffer();
				for (Map.Entry<String, String> line : tableData.getEntries().get(loopindex).entrySet()) {
					
					logger.debug("getkey:{} getvalue:{}",line.getKey(), line.getValue());
					if (line.getKey().equals("_num_") || line.getKey().equals("_hash_")) {
						continue;
					}
					sbFields.append("`");
					sbFields.append(replaceString(line.getKey()));
					sbFields.append("`,");

					sbValues.append("'");
					sbValues.append(replaceString(line.getValue()));
					sbValues.append("',");
				}

				if (_fields == null) {
					_fields = sbFields.toString();
				}
				
				logger.debug("_fields:{}",_fields);

				BatchCommitRequest batchCommitRequest = new BatchCommitRequest();
				batchCommitRequest.setHash(hash);
				batchCommitRequest.setNum(num);
				batchCommitRequest.setValues(sbValues.toString());
				list.add(batchCommitRequest);
				logger.debug("values:{}",sbValues.toString());
				
			}
			datalist.put(_fields, list);
		}
		return datalist;
	}

	@Transactional
	public Map<Table, List<String>> DBReplace(String hash, Integer num, List<TableData> data) throws Exception {
		try {

			dataMapper.beginTransaction();
			Map<Table, List<String>> updateKeys = new HashMap<Table, List<String>>();
			for (TableData tableData : data) {

				String _table = tableData.getTable();
				StringBuilder table = new StringBuilder();
				table.append("`");
				table.append(_table);
				table.append("`");

				Map<String, List<BatchCommitRequest>> dataList = getCommitFieldNameAndValue(hash, num, tableData);
				Iterator<Map.Entry<String, List<BatchCommitRequest>>> entries = dataList.entrySet().iterator();
				while (entries.hasNext())
				{
					Map.Entry<String, List<BatchCommitRequest>> entry = entries.next();
					String _fields = entry.getKey();
					List<BatchCommitRequest> list = entry.getValue();
					if (_table != null && _fields != null && list.size() > 0) {
						dataMapper.commitData(table.toString(), _fields, list);
					}
				}
			}
			dataMapper.commit();
			return updateKeys;
		} catch (Exception e) {
			logger.error("Error while commit data ", e);
			// throw e;
			dataMapper.rollback();

			throw new RuntimeException(e.getMessage());
		}
	}

	private Table getTable(String table_name) {
		List<Map<String, String>> fields = dataMapper.getTable(table_name);
		Table table = null;
		if (fields.isEmpty()) {
			// logger.error("Cannot find the table: {}", table_name);
		} else {
			table = new Table();
			table.setName(table_name);
			String key = fields.get(0).get("key_field");
			table.setKey(key);
			List<String> indices = Arrays.asList(key);
			table.setCache(new MemoryCache(3));
			table.setIndices(indices);
		}
		return table;
	}

	public void createTable(String tableName, String keyField, String valueField) throws Exception {
		logger.debug("create tablename:{}", tableName);
		String key = keyField;
		String value_field = valueField;
		String[] values = value_field.split(",");
		String sql = getSql(getStrSql(tableName), getStrSql(key), values);
		dataMapper.createTable(sql);

	}

	public void createTable(String table_name) throws Exception {

		logger.debug("create tablename:{}", table_name);
		List<Map<String, String>> fields = dataMapper.getTable(table_name);
		logger.debug("fields=" + fields.toString());
		String key = fields.get(0).get("key_field");
		String value_field = fields.get(0).get("value_field");
		String[] values = value_field.split(",");
		String sql = getSql(getStrSql(table_name), getStrSql(key), values);
		dataMapper.createTable(sql);

	}

	private String getSql(String table_name, String key, String[] values) {
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE TABLE IF NOT EXISTS ").append("`").append(table_name).append("`").append("(\n")
				.append(" `_id_` int unsigned auto_increment,\n").append(" `_hash_` varchar(128) not null,\n")
				.append("  `_num_` int not null,\n").append("`_status_` int not null,\n").append("`").append(key)
				.append("`").append(" varchar(255) default '',\n");
		if (!"".equals(values[0].trim())) {
			for (String value : values) {
				sql.append(" `").append(getStrSql(value)).append("` mediumtext,\n");
			}
		}
		sql.append(" PRIMARY KEY( `_id_` ),\n").append(" KEY(`").append(key).append("`(191)),\n").append(" KEY(`_num_`)\n")
				.append(")ENGINE=InnoDB default charset=utf8mb4;");
		return sql.toString();
	}

	private String replaceString(String str) {
		String replaceStr = str;
		replaceStr = replaceStr.replace('\'', '_');
		replaceStr = replaceStr.replace('\"', '_');
		return replaceStr;

	}

	private String getStrSql(String str) {
		String strSql = str;
		strSql = strSql.replace("\\", "\\\\");
		strSql = strSql.replace("`", "\\`");
		return strSql;
	}

	public DataMapper getDataMapper() {
		return dataMapper;
	}

	public void setDataMapper(DataMapper dataMapper) {
		this.dataMapper = dataMapper;
	}

	public Map<String, Table> getTables() {
		return tables;
	}

	public void setTables(Map<String, Table> tables) {
		this.tables = tables;
	}

	@Autowired
	private DataMapper dataMapper;

	private Map<String, Table> tables = new HashMap<String, Table>();

	private ObjectMapper objectMapper = new ObjectMapper();
}