package org.bcos.amdb.test.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.bcos.amdb.cache.Cache;
import org.bcos.amdb.cache.CacheEntry;
import org.bcos.amdb.cache.CacheValues;
import org.bcos.amdb.cache.MemoryCache;
import org.bcos.amdb.dao.DataMapper;
import org.bcos.amdb.dto.BatchCommitRequest;
import org.bcos.amdb.dto.CommitRequest;
import org.bcos.amdb.dto.CommitResponse;
import org.bcos.amdb.dto.Entry;
import org.bcos.amdb.dto.InfoRequest;
import org.bcos.amdb.dto.InfoResponse;
import org.bcos.amdb.dto.Request;
import org.bcos.amdb.dto.SelectRequest;
import org.bcos.amdb.dto.SelectResponse;
import org.bcos.amdb.dto.TableData;
import org.bcos.amdb.service.DBService;
import org.bcos.amdb.service.Table;

public class DBServiceTest {
  public class MockDataMapper implements DataMapper {
    private Integer blockNum = 100;

    @Override
    public List<Map<String, Object>> queryData(String table, Integer num, String indices_equal,
        String keyField, String keyValue,String ConditionSql) {
      if (keyValue.equals("no exists")) {
        return new ArrayList<Map<String, Object>>();
      }

      if (table.equals("t_test")) {
        assertEquals(table, "t_test");
        assertEquals(blockNum, num);

        Table tableInfo = new Table();
        tableInfo.setIndices(Stream.of("field1", "field2", "field3").collect(Collectors.toList()));
        assertEquals(indices_equal, tableInfo.indicesEqualString());

        assertEquals(keyField, "field1");
        assertEquals(keyValue, "key_field");

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        result.add(
            Stream.of("field1:key_field", "field2:2", "field3:2").collect(Collectors.toMap(v -> {
              return v.split(":")[0];
            }, v -> {
              return v.split(":")[1];
            }))); //satisfy query condition

        result.add(Stream.of("field1:key_field", "field2:not_equal", "field3:2")
            .collect(Collectors.toMap(v -> {
              return v.split(":")[0];
            }, v -> {
              return v.split(":")[1];
            }))); // field2 not satisfy

        result.add(
            Stream.of("field1:key_field", "field2:1", "field3:1").collect(Collectors.toMap(v -> {
              return v.split(":")[0];
            }, v -> {
              return v.split(":")[1];
            }))); // field3 not satisfy

        result.add(
            Stream.of("field1:key_field", "field2:1", "field3:2").collect(Collectors.toMap(v -> {
              return v.split(":")[0];
            }, v -> {
              return v.split(":")[1];
            }))); // field4 not satisfy

        result.add(
            Stream.of("field1:key_field", "field2:2", "field3:1").collect(Collectors.toMap(v -> {
              return v.split(":")[0];
            }, v -> {
              return v.split(":")[1];
            }))); // field5 not satisfy

        result.add(
            Stream.of("field1:key_field", "field2:3", "field3:2").collect(Collectors.toMap(v -> {
              return v.split(":")[0];
            }, v -> {
              return v.split(":")[1];
            }))); // field6 not satisfy

        return result;
      }

      if (table.equals("t_test_cache")) {
        assertEquals(table, "t_test_cache");
        assertEquals(blockNum, num);

        Table tableInfo = new Table();
        tableInfo.setIndices(Stream.of("field1", "field2", "field3").collect(Collectors.toList()));
        assertEquals(indices_equal, tableInfo.indicesEqualString());

        assertEquals(keyField, "field1");
        assertEquals(keyValue, "key_field");

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        result.add(
            Stream.of("field1:key_field", "field2:2", "field3:2").collect(Collectors.toMap(v -> {
              return v.split(":")[0];
            }, v -> {
              return v.split(":")[1];
            }))); // satisfy query condition

        result.add(Stream.of("field1:key_field", "field2:not_equal", "field3:2")
            .collect(Collectors.toMap(v -> {
              return v.split(":")[0];
            }, v -> {
              return v.split(":")[1];
            }))); // field2 not satisfy

        result.add(
            Stream.of("field1:key_field", "field2:1", "field3:1").collect(Collectors.toMap(v -> {
              return v.split(":")[0];
            }, v -> {
              return v.split(":")[1];
            }))); // field3 not satisfy

        result.add(
            Stream.of("field1:key_field", "field2:1", "field3:2").collect(Collectors.toMap(v -> {
              return v.split(":")[0];
            }, v -> {
              return v.split(":")[1];
            }))); // field4 not satisfy

        result.add(
            Stream.of("field1:key_field", "field2:2", "field3:1").collect(Collectors.toMap(v -> {
              return v.split(":")[0];
            }, v -> {
              return v.split(":")[1];
            }))); // field5 not satisfy

        result.add(
            Stream.of("field1:key_field", "field2:3", "field3:2").collect(Collectors.toMap(v -> {
              return v.split(":")[0];
            }, v -> {
              return v.split(":")[1];
            }))); // field6 not satisfy

        return result;
      }

      return null;
    }

    @Override
    public void commitData(String table, String fields, List<BatchCommitRequest> list) {
      // assertEquals("00000", hash);
      // assertEquals(new Integer(100), num);
      assertEquals("t_test_cache", table);
    }

    public void setBlockNum(int i) {
      blockNum = i;

    }

    
    @Override
    public void createTable(String sql) {

    }

    @Override
    public List<Map<String, String>> getTable(String table_name) {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public void createSysTables() {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void insertSysTables() {
      // TODO Auto-generated method stub
      
    }
    
    
    public void createSysConsensus()
    {
    	// TODO Auto-generated method stub
    }
    public void	createAccessTables()
    {
    	// TODO Auto-generated method stub
    }
    
    public void createCurrentStateTables()
    {
    	// TODO Auto-generated method stub
    }
    public void createNumber2HashTables()
    {
    	// TODO Auto-generated method stub
    }
    public void createTxHash2BlockTables()
    {
    	// TODO Auto-generated method stub
    }
    public void createHash2BlockTables()
    {
    	// TODO Auto-generated method stub
    }
    public void createCnsTables()
    {
    	// TODO Auto-generated method stub
    }
    public void createSysConfigTables()
    {
    	// TODO Auto-generated method stub
    }
    public void createSysBlock2NoncesTables()
    {
    	// TODO Auto-generated method stub
    }
    
    
  }

  MockDataMapper mockDataMapper = new MockDataMapper();
  DBService dbService = new DBService();
  private ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void init() {
    dbService.setDataMapper(mockDataMapper);

    Table tableWithoutCache = new Table();
    tableWithoutCache.setKey("field1");
    tableWithoutCache.setName("t_test");

    List<String> indices = Stream.of("field1", "field2", "field3").collect(Collectors.toList());
    tableWithoutCache.setIndices(indices);

    assertEquals(tableWithoutCache.getKey(), "field1");
    assertEquals(tableWithoutCache.getName(), "t_test");
    assertEquals(tableWithoutCache.getIndices(), indices);

    Map<String, Table> tables = new HashMap<String, Table>();
    tables.put("t_test", tableWithoutCache);

    Table tableWithCache = new Table();
    tableWithCache.setKey("field1");
    tableWithCache.setName("t_test_cache");
    tableWithCache.setIndices(indices);

    Cache cache = new MemoryCache(null);
    tableWithCache.setCache(cache);
    tables.put("t_test_cache", tableWithCache);

    dbService.setTables(tables);
  }

  @Test
  void testInfo() throws IOException {
    Request<InfoRequest> request = new Request<InfoRequest>();
    request.setOp("info");

    InfoRequest params = new InfoRequest();
    params.setBlockHash("00000");
    params.setNum(100);
    params.setTable("t_demo");

    request.setParams(params);
    String content = objectMapper.writeValueAsString(request);
    String result = dbService.process(content);

    MockResponse<InfoResponse> response =
        objectMapper.readValue(result, new TypeReference<MockResponse<InfoResponse>>() {});

    assertEquals(response.getCode(), new Integer(0));
    InfoResponse info = (InfoResponse) response.getResult();

    assertLinesMatch(info.getIndices(),
        Stream.of("field1", "field2", "field3").collect(Collectors.toList()));
  }

  @Test
  void testSelect() throws IOException {
    Request<SelectRequest> request = new Request<SelectRequest>();
    request.setOp("select");

    SelectRequest params = new SelectRequest();
    params.setBlockHash("00000");
    params.setNum(100);
    params.setTable("t_test");
    params.setKey("key_field");

    request.setParams(params);
    String content = objectMapper.writeValueAsString(request);
    String result = dbService.process(content);

    MockResponse<SelectResponse> response =
        objectMapper.readValue(result, new TypeReference<MockResponse<SelectResponse>>() {});
    SelectResponse select = response.getResult();

    assertEquals(select.getColumns(),
        Stream.of("field1", "field2", "field3").collect(Collectors.toSet()));
    assertEquals(6, select.getData().size());

    params.setKey("no exists");
    content = objectMapper.writeValueAsString(request);
    result = dbService.process(content);
    response = objectMapper.readValue(result, new TypeReference<MockResponse<SelectResponse>>() {});
    select = response.getResult();
    assertEquals(0, select.getColumns().size());
    assertEquals(0, select.getData().size());
  }

//  @Test
//  void testSelectWithCache() throws IOException {
//    Request<SelectRequest> request = new Request<SelectRequest>();
//    request.setOp("select");
//
//    SelectRequest params = new SelectRequest();
//    params.setBlockHash("00000");
//    params.setNum(100);
//    params.setTable("t_test_cache");
//    params.setKey("key_field");
//
//    request.setParams(params);
//    String content = objectMapper.writeValueAsString(request);
//    String result = dbService.process(content);
//
//    MockResponse<SelectResponse> response =
//        objectMapper.readValue(result, new TypeReference<MockResponse<SelectResponse>>() {});
//    SelectResponse select = response.getResult();
//
//    assertEquals(select.getColumns(),
//        Stream.of("field1", "field2", "field3").collect(Collectors.toSet()));
//    assertEquals(6, select.getData().size());
//
//    // 检查cache
//    Map<String, Table> tables = dbService.getTables();
//    Table table = tables.get("t_test_cache");
//    Cache cache = table.getCache();
//
//    CacheEntry entry = cache.get("key_field");
//    // 此时LastCommitNum为0，数据不进cache
//    assertNull(entry);
//
//    cache.setLastCommitNum(100);
//    result = dbService.process(content);
//    entry = cache.get("key_field");
//    // LastCommitNum = 请求num，数据不进cache
//    assertNull(entry);
//
//    cache.setLastCommitNum(99);
//    result = dbService.process(content);
//    entry = cache.get("key_field");
//    // LastCommitNum < 请求num，数据进cache
//    assertNotNull(entry);
//    assertEquals(entry.getNum(), new Integer(100));
//    assertEquals(entry.getKey(), "key_field");
//
//    List<CacheValues> values = entry.getValues();
//    assertEquals(values.size(), 6);
//    values.remove(values.size() - 1); // 删掉最后一个数据，从缓存获取数据，只有5条
//
//    // num == entry.getNum() 预期不命中cache，有6条数据
//    result = dbService.process(content);
//    response = objectMapper.readValue(result, new TypeReference<MockResponse<SelectResponse>>() {});
//    select = response.getResult();
//
//    assertEquals(select.getColumns(),
//        Stream.of("field1", "field2", "field3").collect(Collectors.toSet()));
//    assertEquals(6, select.getData().size());
//
//    // num > entry.getNum()，预期命中，5条数据
//    entry.setNum(99);
//    result = dbService.process(content);
//    response = objectMapper.readValue(result, new TypeReference<MockResponse<SelectResponse>>() {});
//    select = response.getResult();
//
//    assertEquals(select.getColumns(),
//        Stream.of("field1", "field2", "field3").collect(Collectors.toSet()));
//    assertEquals(5, select.getData().size());
//  }

//  @Test
//  void testCommitWithCache() throws IOException {
//    Request<CommitRequest> request = new Request<CommitRequest>();
//    request.setOp("commit");
//
//    CommitRequest params = new CommitRequest();
//    params.setBlockHash("00000");
//    params.setNum(100);
//
//    List<TableData> datas = new ArrayList<TableData>();
//
//    // 第一个表写入
//    TableData data = new TableData();
//    data.setTable("t_test_cache");
//
//    Entry entry = new Entry();
//    entry.setKey("key_field");
//
//    List<Map<String, String>> values = new ArrayList<Map<String, String>>();
//    values.add(Stream.of("field1:key_field1", "field2:1", "field3:1", "num:100", "hash:0x100")
//        .collect(Collectors.toMap(v -> {
//          return v.split(":")[0];
//        }, v -> {
//          return v.split(":")[1];
//        })));
//    values.add(Stream.of("field1:key_field1", "field2:2", "field3:2", "num:100", "hash:0x100")
//        .collect(Collectors.toMap(v -> {
//          return v.split(":")[0];
//        }, v -> {
//          return v.split(":")[1];
//        })));
//
//    entry.setValues(values);
//    List<Entry> entries = new ArrayList<Entry>();
//    entries.add(entry);
//    data.setEntries(entries);
//    datas.add(data);
//
//    // 第二个表写入
//    entries = new ArrayList<Entry>();
//    entry = new Entry();
//    entry.setKey("key_field");
//    values = new ArrayList<Map<String, String>>();
//    values.add(Stream.of("field1:key_field2", "field2:3", "field3:3", "num:100", "hash:0x100")
//        .collect(Collectors.toMap(v -> {
//          return v.split(":")[0];
//        }, v -> {
//          return v.split(":")[1];
//        })));
//    values.add(Stream.of("field1:key_field2", "field2:4", "field3:4", "num:100", "hash:0x100")
//        .collect(Collectors.toMap(v -> {
//          return v.split(":")[0];
//        }, v -> {
//          return v.split(":")[1];
//        })));
//
//    entry.setValues(values);
//    entries.add(entry);
//
//    data = new TableData();
//
//    data.setEntries(entries);
//    data.setTable("t_test");
//    datas.add(data);
//
//    // 第三个表写入
//    entries = new ArrayList<Entry>();
//    entry = new Entry();
//    entry.setKey("key_field");
//    values = new ArrayList<Map<String, String>>();
//    values.add(Stream.of("field1:key_field2", "field2:3", "field3:3", "num:100", "hash:0x100")
//        .collect(Collectors.toMap(v -> {
//          return v.split(":")[0];
//        }, v -> {
//          return v.split(":")[1];
//        })));
//    values.add(Stream.of("field1:key_field2", "field2:4", "field3:4", "num:100", "hash:0x100")
//        .collect(Collectors.toMap(v -> {
//          return v.split(":")[0];
//        }, v -> {
//          return v.split(":")[1];
//        })));
//
//    entry.setValues(values);
//    entries.add(entry);
//
//    data = new TableData();
//
//    data.setEntries(entries);
//    data.setTable("t_unknown");
//    datas.add(data);
//
//    params.setData(datas);
//
//    request.setParams(params);
//    String content = objectMapper.writeValueAsString(request);
//    ((MockDataMapper) dbService.getDataMapper()).setBlockNum(101);
//    String result = dbService.process(content);
//
//    MockResponse<CommitResponse> response =
//        objectMapper.readValue(result, new TypeReference<MockResponse<CommitResponse>>() {});
//    assertEquals(new Integer(0), response.getCode());
//
//    CommitResponse commit = response.getResult();
//    assertEquals(new Integer(1), commit.getCount());
//
//    // 检查cache
//    Map<String, Table> tables = dbService.getTables();
//    Table table = tables.get("t_test_cache");
//    Cache cache = table.getCache();
//
//    CacheEntry cacheEntry = cache.get("key_field");
//    assertEquals(new Integer(101), cacheEntry.getNum());
//    assertEquals(6, cacheEntry.getValues().size());
//  }

//  @Test
//  void testUnknownCommand() throws IOException {
//    Request<SelectRequest> request = new Request<SelectRequest>();
//    request.setOp("select1");
//
//    SelectRequest params = new SelectRequest();
//    params.setBlockHash("00000");
//    params.setNum(100);
//    params.setTable("t_test");
//    params.setKey("key_field");
//
//    request.setParams(params);
//    String content = objectMapper.writeValueAsString(request);
//    String result = dbService.process(content);
//
//    MockResponse<SelectResponse> response =
//        objectMapper.readValue(result, new TypeReference<MockResponse<SelectResponse>>() {});
//
//    assertEquals(new Integer(-1), response.getCode());
//
//    request.setOp(null);
//    content = objectMapper.writeValueAsString(request);
//    result = dbService.process(content);
//    response = objectMapper.readValue(result, new TypeReference<MockResponse<SelectResponse>>() {});
//
//    assertEquals(new Integer(-1), response.getCode());
//
//    content = "";
//    result = dbService.process(content);
//    response = objectMapper.readValue(result, new TypeReference<MockResponse<SelectResponse>>() {});
//
//    assertEquals(new Integer(-1), response.getCode());
//  }
}
