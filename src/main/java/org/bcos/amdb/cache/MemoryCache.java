package org.bcos.amdb.cache;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.collections4.map.LRUMap;

public class MemoryCache implements Cache {
	
	private Integer cacheSize;
	//定义一个大小为cacheSize的容器
	private LRUMap<String, CacheEntry> map;
	//使该map线程安全
	private Map<String, CacheEntry> cache;
	private Integer lastCommitNum = 0;
	
	public MemoryCache(Integer cacheSize) {
		//通过spring配置文件注入，给一个默认值1000，当注入失败则为默认值
		if(cacheSize == null) {
			this.cacheSize = 1000;
		}else {
			this.cacheSize = cacheSize;
		}
		
		map = new LRUMap<String, CacheEntry>(this.cacheSize);
		cache = Collections.synchronizedMap(map);
	}
	
	@Override
	public CacheEntry get(String key) {
		return cache.get(key);
	}
	
	@Override
	public void set(String key, CacheEntry entry) {
		cache.put(key, entry);
	}
	
	@Override
	public void remove(String key) {
		cache.remove(key);
	}
	
	@Override
	public Integer getLastCommitNum() {
		return lastCommitNum;
	}

	@Override
	public void setLastCommitNum(Integer num) {
		lastCommitNum = num;
	}
	
	public Integer getCacheSize() {
		return cacheSize;
	}

	public void setCacheSize(Integer cacheSize) {
		this.cacheSize = cacheSize;
	}
}
