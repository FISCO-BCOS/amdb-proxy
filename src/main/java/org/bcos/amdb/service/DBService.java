package org.bcos.amdb.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.text.StringEscapeUtils;
import org.bcos.amdb.cache.Cache;
import org.bcos.amdb.cache.MemoryCache;
import org.bcos.amdb.dao.DataMapper;
import org.bcos.amdb.dto.BatchCommitRequest;
import org.bcos.amdb.dto.CommitRequest;
import org.bcos.amdb.dto.CommitResponse;
import org.bcos.amdb.dto.Condition;
import org.bcos.amdb.dto.Entry;
import org.bcos.amdb.dto.Header;
import org.bcos.amdb.dto.InfoRequest;
import org.bcos.amdb.dto.InfoResponse;
import org.bcos.amdb.dto.SelectRequest;
import org.bcos.amdb.dto.SelectResponse;
import org.bcos.amdb.dto.TableData;
import org.bcos.amdb.dto.Request;
import org.bcos.amdb.dto.Response;


public class DBService {
	
    private static Logger logger = LoggerFactory.getLogger(DBService.class);
    private static final String SYSTABLE = "_sys_tables_";

    public void initTables() {
        logger.info("Start create table:");
        try {
            dataMapper.createSysTables();
            dataMapper.insertSysTables();
            dataMapper.createSysMiners();
            //add by darrenyin
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
                        new TypeReference<Request<InfoRequest>>() {});
                result = info(request.getParams());

            } else if (header.getOp().equals("select")) {
                Request<SelectRequest> request = objectMapper.readValue(content,
                        new TypeReference<Request<SelectRequest>>() {});

                SelectRequest params = request.getParams();
                try {
                    result = select(params);
                } catch (DataAccessException e) {
                    createTable(params.getTable());
                    result = select(params);
                }

            } else if (header.getOp().equals("commit")) {
                Request<CommitRequest> request = objectMapper.readValue(content,
                        new TypeReference<Request<CommitRequest>>() {});

                try {
                    result = commit(request.getParams());
                } catch (DataAccessException e) {// process fail，create table，send request again
                    List<TableData> data = request.getParams().getData();
                    for (TableData tableData : data) {
                        String table_name = tableData.getTable();
                        createTable(table_name);
                        result = commit(request.getParams());
                    }
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

    private InfoResponse info(InfoRequest request) throws IOException {
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
    
    public SelectResponse select(SelectRequest request) throws Exception {
    	String table = request.getTable();
        Integer num = request.getNum();
        Table info = getTable(table);
        String key = request.getKey();
        List<List<String>> condition = request.getCondition();
        logger.debug("key:{} condition:{}",key,condition);
        //JSONArray obj = JSONArray.parseArray(condition);
        //logger.debug("key:{} condition obj:{}",key,obj);
        Map<String,Condition >	conditionmap = new HashMap<String, Condition >();
        for(List<String> cond: condition) {
          if(cond.size() < 3) {
            throw new Exception("Invalid cond:" + cond.stream().reduce((a,b) -> a +", " +b));
          }
          
          Condition condItem = new Condition();
          condItem.setOp(Condition.valueOf(Integer.parseInt(cond.get(1))));
          condItem.setValue(cond.get(2));
          
          conditionmap.put(cond.get(0), condItem);
        }
        
        /*
        for(int index=0;index<obj.size();index++)
        {
        	JSONObject ss = obj.getJSONObject(index);
        	String keyFileld = ss.getString("field_key");
        	int op = ss.getIntValue("op");
        	String keyValue = 	ss.getString("field_value");
        	Condition conditionItem  = new Condition();
        	conditionItem.setOp(Condition.valueOf(op));
        	conditionItem.setValue(keyValue);
        	conditionmap.put(keyFileld,conditionItem);
        }
        */
        StringBuilder sb = new StringBuilder();
        Iterator<java.util.Map.Entry<String, Condition>> entries 
        	= conditionmap.entrySet().iterator();
        while (entries.hasNext())
        {
        	 Map.Entry<String, Condition> entry = entries.next();
        	 logger.debug("key:{}",entry.getKey());
        	 Condition value = entry.getValue();
        	 
        	 String strKeyEscape = StringEscapeUtils.escapeJava(entry.getKey());
        	 
        	 if(value.getOp()  == org.bcos.amdb.dto.Condition.ConditionOp.eq)
        	 {
        		 sb.append(" and ").append(strKeyEscape).append(" = ");
        		 sb.append("'").append(StringEscapeUtils.escapeJava(value.getValue()));
        		 sb.append("'");
        	 }
        	 
        	 else if(value.getOp()  == org.bcos.amdb.dto.Condition.ConditionOp.ne)
        	 {
        		 sb.append(" and ").append(strKeyEscape).append(" != ");
        		 sb.append("'").append(StringEscapeUtils.escapeJava(value.getValue()));
        		 sb.append("'");
        	 }
        	 
        	 else if(value.getOp()  == org.bcos.amdb.dto.Condition.ConditionOp.gt)
        	 {
        		 sb.append(" and ").append(strKeyEscape).append(">");
        		 sb.append("'").append(StringEscapeUtils.escapeJava(value.getValue()));
        		 sb.append("'");
        	 }
        	 
        	 else if(value.getOp()  == org.bcos.amdb.dto.Condition.ConditionOp.ge)
        	 {
        		 sb.append(" and ").append(strKeyEscape).append(">=");
        		 sb.append("'").append(StringEscapeUtils.escapeJava(value.getValue()));
        		 sb.append("'");
        	 }
        	 
        	 else if(value.getOp()  == org.bcos.amdb.dto.Condition.ConditionOp.lt)
        	 {
        		 sb.append(" and ").append(strKeyEscape).append("<");
        		 sb.append("'").append(StringEscapeUtils.escapeJava(value.getValue()));
        		 sb.append("'");
        	 }
        	 
        	 else if(value.getOp()  == org.bcos.amdb.dto.Condition.ConditionOp.le)
        	 {
        		 sb.append(" and ").append(strKeyEscape).append("<=");
        		 sb.append("'").append(StringEscapeUtils.escapeJava(value.getValue()));
        		 sb.append("'");
        	 }
        	 else
        	 {
        		 logger.error("error condition op:{}",value.getOp());
        		 continue;
        	 }
        }
        
        String	conditionsql = sb.toString();
        logger.debug("condition sql:{}",conditionsql);
        
        SelectResponse response = new SelectResponse();
        List<Map<String, Object>> data = null;
        logger.debug("key:{} table:{} number:{} index{}  key_value:{} condition:{}", 
        		key,table,num,info.indicesEqualString(),info.getKey(),
        		conditionsql);
        
        data = dataMapper.queryData(table,
        		num,info.indicesEqualString(),
        		info.getKey(),key,conditionsql);
        
        if (!data.isEmpty()) 
        {
        	logger.debug("condition sql:{} has data", conditionsql);
            Map<String, Object> f = data.get(0);
            response.setColumns(f.keySet());
            List<List<Object>> allValues = new ArrayList<List<Object>>();
            for (Map<String, Object> line : data) 
            {
                List<Object> values = new ArrayList<Object>();
                for (String field : line.keySet()) 
                {
                    values.add(line.get(field));
                }
                allValues.add(values);
          }
           response.setData(allValues);
        } 
        else 
        {
        	logger.debug("condition sql:{} has no data", conditionsql);
            response.setColumns(new HashSet<String>());
            response.setData(new ArrayList<List<Object>>());
        }
        return response;
    }
    
    private CommitResponse commit(CommitRequest request) throws DataAccessException, IOException {
        Integer count = 0;
        Map<Table, List<String>> update =
                DBReplace(request.getBlockHash(), request.getNum(), request.getData());
        count = update.size();
        CommitResponse response = new CommitResponse();
        response.setCount(count);
        return response;
    }

    @Transactional
    private Map<Table, List<String>> DBReplace(String hash, Integer num, List<TableData> data)
            throws DataAccessException {
        Map<Table, List<String>> updateKeys = new HashMap<Table, List<String>>();

        for (TableData tableData : data) {
            Table table = getTable(tableData.getTable());

            if (table == null) {
                logger.error("Cannot find the table:{}", tableData.getTable());
                continue;
            }

            List<BatchCommitRequest> list = new ArrayList<>();
            String _table = null;
            String _fields = null;

            List<String> keys = new ArrayList<String>();

            Cache cache = table.getCache();
            if (cache != null) {
                cache.setLastCommitNum(0); // update LastCommit to 0， temporarily disable cache
            }

            for (Map<String, String> entry : tableData.getEntries()) {
              /*
                String key = entry.getKey();

                if (cache != null) {
                    cache.remove(key);
                }
                */

                //for (Map<String, String> fields : entry.getValues()) {
                    StringBuffer sbFields = new StringBuffer();
                    StringBuffer sbValues = new StringBuffer();

                    for (Map.Entry<String, String> line : entry.entrySet()) {
                        if (line.getKey().equals("_num_") || line.getKey().equals("_hash_")) {
                            continue;
                        }
                        
                        if(line.getKey().equals("_id_") && line.getValue().equals("0")) {
                          continue;
                        }

                        sbFields.append("`");
                        sbFields.append(replaceString(line.getKey()));
                        sbFields.append("`,");

                        sbValues.append("'");
                        sbValues.append(replaceString(line.getValue()));
                        sbValues.append("',");
                    }

                    // batch data to be inserted into the list
                    BatchCommitRequest batchCommitRequest = new BatchCommitRequest();
                    batchCommitRequest.setHash(hash);
                    batchCommitRequest.setNum(num);
                    batchCommitRequest.setValues(sbValues.toString());
                    list.add(batchCommitRequest);

                    if (_table == null) {
                        _table = table.getName();
                    }

                    if (_fields == null) {
                        _fields = sbFields.toString();
                    }

                //}

                //keys.add(entry.getKey());
            }

            //updateKeys.put(table, keys);
            if (_table != null && _fields != null && list.size() > 0) {

                dataMapper.commitData(_table, _fields, list);
            }
        }

        return updateKeys;
    }

    private Table getTable(String table_name) {
        List<Map<String, String>> fields = dataMapper.getTable(table_name);
        Table table = null;
        if (fields.isEmpty()) {
            logger.error("Cannot find the table");
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

    public void createTable(String table_name) throws Exception {
    	
    	logger.debug("create tablename:{}",table_name);
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
        sql.append("CREATE TABLE IF NOT EXISTS ").append("`").append(table_name).append("`")
                .append("(\n").append(" `_id_` int unsigned auto_increment,\n")
                .append(" `_hash_` varchar(128) not null,\n").append("  `_num_` int not null,\n")
                .append("`_status_` int not null,\n").append("`").append(key).append("`")
                .append(" varchar(128) default '',\n");
        if (!"".equals(values[0].trim())) {
            for (String value : values) {
                sql.append(" `").append(getStrSql(value)).append("` varchar(2048) default '',\n");
            }
        }
        sql.append(" PRIMARY KEY( `_id_` ),\n").append(" KEY(`").append(key).append("`),\n")
                .append(" KEY(`_num_`)\n").append(")ENGINE=InnoDB default charset=utf8mb4;");
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
