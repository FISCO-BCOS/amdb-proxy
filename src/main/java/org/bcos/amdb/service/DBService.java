package org.bcos.amdb.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bcos.amdb.cache.Cache;
import org.bcos.amdb.cache.CacheEntry;
import org.bcos.amdb.cache.CacheValues;
import org.bcos.amdb.cache.MemoryCache;
import org.bcos.amdb.dao.DataMapper;
import org.bcos.amdb.dto.BatchCommitRequest;
import org.bcos.amdb.dto.CommitRequest;
import org.bcos.amdb.dto.CommitResponse;
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

  @PostConstruct
  public void initTables() {
    logger.info("start create table:");
    try {
        dataMapper.createSysTables();
        dataMapper.insertSysTables();
        dataMapper.createSysMiners();
    } catch (Exception e) {
       logger.debug("create table error: "+e.getMessage());
    }
    logger.info("create table successful!");
  }

  public String process(String content) throws JsonProcessingException {
    Response response = new Response();
    Object result = null;

    try {
      logger.trace("process收到请求: {}", content);
      Header header = objectMapper.readValue(content, Header.class);

      if (header.getOp() == null) {
        throw new Exception("无法解析header.op:" + content);
      }

      if (header.getOp().equals("info")) {
        Request<InfoRequest> request =
            objectMapper.readValue(content, new TypeReference<Request<InfoRequest>>() {});
        result = info(request.getParams());

      } else if (header.getOp().equals("select")) {
        Request<SelectRequest> request =
            objectMapper.readValue(content, new TypeReference<Request<SelectRequest>>() {});

        SelectRequest params = request.getParams();
        try {
          result = select(params);
        } catch (DataAccessException e) {
          createTable(params.getTable());
          result = select(params);
        }

      } else if (header.getOp().equals("commit")) {
        Request<CommitRequest> request =
            objectMapper.readValue(content, new TypeReference<Request<CommitRequest>>() {});

        try {
          result = commit(request.getParams());
        } catch (DataAccessException e) {// 处理失败，建表，重发更新请求
          List<TableData> data = request.getParams().getData();
          for (TableData tableData : data) {
            String table_name = tableData.getTable();
            try {
              createTable(table_name);
            } catch (DataAccessException e1) {

            }
            result = commit(request.getParams());
          }
        } catch (NullPointerException e2) {
          logger.debug("空操作！-----------------");
        }
      } else {
        logger.error("未知请求:{}", header.getOp());

        throw new Exception("未知请求:" + header.getOp());
      }

      response.setCode(0);
    } catch (Exception e) {
      logger.error("错误:", e);

      response.setCode(-1);
    }

    if (result != null) {
      response.setResult(result);
    } else {
      logger.error("result为null");
    }

    String out;
    out = objectMapper.writeValueAsString(response);

    return out;
  }

  private InfoResponse info(InfoRequest request) throws IOException {
    String table = request.getTable();
    Table info = getTable(table);
    InfoResponse response = new InfoResponse();
    if(info == null)
    {
        return response;
    }
    response.setKey(info.getKey());
    response.setIndices(info.getIndices());

    return response;
  }

  private SelectResponse select(SelectRequest request) throws DataAccessException {
    String table = request.getTable();
    Integer num = request.getNum();
    Table info = getTable(table);
    String key = request.getKey();
    SelectResponse response = new SelectResponse();
    
    if(info == null)
    {
        return response;
    }
    List<Map<String, Object>> data = null;
    Cache cache = info.getCache();
    CacheEntry entry = null;
    if (cache != null) {
      entry = cache.get(key);
      if (entry != null && num > entry.getNum()) {
        logger.debug("命中cache:{}", entry.getKey());

        data = entry.getValues().stream().map(v -> {
          return v.getFields();
        }).collect(Collectors.toList());
      }
    }

    if (data == null) {
      logger.debug("未命中cache:{}", key);
      logger.debug("indicesEqualString:{}", info.indicesEqualString());

      data = dataMapper.queryData(table, num, info.indicesEqualString(), info.getKey(), key);

      if (cache != null && entry == null && cache.getLastCommitNum() != 0
          && (num > cache.getLastCommitNum())) {
        logger.info("更新cache:{}", key);

        entry = new CacheEntry();
        entry.setKey(key);
        entry.setNum(num);
        entry.setValues(data.stream().map(v -> {
          CacheValues value = new CacheValues();
          value.setFields(v);
          return value;
        }).collect(Collectors.toList()));

        cache.set(entry.getKey(), entry);
      }
    }

    if (!data.isEmpty()) {
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
      response.setColumns(new HashSet<String>());
      response.setData(new ArrayList<List<Object>>());
    }

    return response;
  }

  private CommitResponse commit(CommitRequest request) throws DataAccessException, IOException {
    Integer count = 0;

    Map<Table, List<String>> update =
        DBReplace(request.getBlockHash(), request.getNum(), request.getData());

    // DB提交成功后，更新cache
    for (Map.Entry<Table, List<String>> entry : update.entrySet()) {
      Table table = entry.getKey();

      if (table.getCache() != null) {
        for (String key : entry.getValue()) {
          // request.getNum()+1为了查出刚刚写入的数据
          List<Map<String, Object>> newData = dataMapper.queryData(table.getName(),
              request.getNum() + 1, table.indicesEqualString(), table.getKey(), key);

          CacheEntry cacheEntry = new CacheEntry();
          cacheEntry.setKey(key);
          cacheEntry.setNum(request.getNum() + 1);
          cacheEntry.setValues(newData.stream().map(v -> {
            CacheValues values = new CacheValues();
            values.setFields(v);

            return values;
          }).collect(Collectors.toList()));

          table.getCache().set(key, cacheEntry);
          table.getCache().setLastCommitNum(request.getNum());
        }
      }
    }

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
        logger.error("未找到表:{}", tableData.getTable());
        continue;
      }

      List<BatchCommitRequest> list = new ArrayList<>();
      String _table = null;
      String _fields = null;

      List<String> keys = new ArrayList<String>();

      Cache cache = table.getCache();
      if (cache != null) {
        cache.setLastCommitNum(0); // 更新LastCommit为0，暂时禁用该cache的缓存
      }

      for (Entry entry : tableData.getEntries()) {
        String key = entry.getKey();

        if (cache != null) {
          cache.remove(key);
        }

        for (Map<String, String> fields : entry.getValues()) {
          StringBuffer sbFields = new StringBuffer();
          StringBuffer sbValues = new StringBuffer();

          for (Map.Entry<String, String> line : fields.entrySet()) {
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

          // 要插入的数据批量放入list
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

        }

        keys.add(entry.getKey());
      }

      updateKeys.put(table, keys);
      if (_table != null && _fields != null && list.size() > 0) {
        // 批量插入数据
        dataMapper.commitData(_table, _fields, list);
      }
    }

    return updateKeys;
  }

  private Table getTable(String table_name) {
    List<Map<String, String>> fields = dataMapper.getTable(table_name);
    Table table = null;
    if (fields.isEmpty()) {
      logger.error("无此table");
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

  private void createTable(String table_name) throws DataAccessException {

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
