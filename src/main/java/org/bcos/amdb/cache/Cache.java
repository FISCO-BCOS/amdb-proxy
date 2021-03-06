package org.bcos.amdb.cache;

public interface Cache {
	CacheEntry get(String key);
	void set(String key, CacheEntry entry);
	void remove(String key);
	
	Integer getLastCommitNum();
	void setLastCommitNum(Integer num);
}