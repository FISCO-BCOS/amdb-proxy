package org.bcos.amdb.service;

import java.util.List;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;

import org.bcos.amdb.cache.Cache;

public class Table {
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	public List<String> getIndices() {
		return indices;
	}
	
	public void setIndices(List<String> indices) {
		this.indices = indices;
	}
	
	public String indicesEqualString() {
		StringBuffer sb = new StringBuffer();
		
		for(String column: indices) {
			sb.append(" and `tt`.`");
			sb.append(column);
			sb.append("` = `");
			sb.append(column);
			sb.append("`");
		}
		
		return sb.toString();
	}

	public Cache getCache() {
		return cache;
	}
	public void setCache(Cache cache) {
		this.cache = cache;
	}

	private String name;
	private String key;
	private List<String> indices;
	private Cache cache;
}
