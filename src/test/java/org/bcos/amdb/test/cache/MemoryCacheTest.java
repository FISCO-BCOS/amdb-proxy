package org.bcos.amdb.test.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.bcos.amdb.cache.CacheEntry;
import org.bcos.amdb.cache.MemoryCache;
import org.bcos.amdb.cache.CacheValues;

public class MemoryCacheTest {
	private MemoryCache memoryCache = new MemoryCache(null);
	
	@Test
	void test() {
		CacheEntry entry = new CacheEntry();
		entry.setKey("key");
		entry.setNum(100);
		
		assertEquals(entry.getKey(), "key");
		assertEquals(entry.getNum(), new Integer(100));
		
		List<CacheValues> values = new ArrayList<CacheValues>();
		
		CacheValues value = new CacheValues();
		Map<String, Object> fields = new HashMap<String, Object>();
		
		value.setFields(fields);
		assertEquals(value.getFields(), fields);
		
		entry.setValues(values);
		assertEquals(entry.getValues(), values);
		
		memoryCache.set("test", entry);
		
		CacheEntry r = memoryCache.get("test");
		assertNotNull(r);
		
		memoryCache.remove("test");
		r = memoryCache.get("test");
		assertNull(r);
	}
	
	@Test
	void testLru() {
	    //test param size
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "applicationContext.xml" });
		MemoryCache cache = context.getBean(MemoryCache.class);
		assertEquals(cache.getCacheSize().intValue(), 3);
		
		//test LRU
		CacheEntry entry = new CacheEntry();
		cache.set("key1", entry);
		CacheEntry r11 = cache.get("key1");
		assertNotNull(r11);
		
		cache.set("key2", entry);
		cache.get("key1");
		
		cache.set("key3", entry);
		
		cache.set("key4", entry);
		cache.remove("key4");
		
		cache.set("key5", entry);
		
		CacheEntry r12 = cache.get("key1");
		CacheEntry r21 = cache.get("key2");
		CacheEntry r31 = cache.get("key3");
		CacheEntry r41 = cache.get("key4");
		CacheEntry r51 = cache.get("key5");
		
		assertNotNull(r12);
		assertNotNull(r31);
		assertNotNull(r51);
		assertNull(r21);
		assertNull(r41);
	}
}
