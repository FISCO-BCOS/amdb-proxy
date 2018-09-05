package org.bcos.amdb.cache;

import java.util.List;

public class CacheEntry {
	private Integer num; //区块高度
	private String key;
	private List<CacheValues> values;
	
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public List<CacheValues> getValues() {
		return values;
	}
	public void setValues(List<CacheValues> values) {
		this.values = values;
	}
}