package org.bcos.amdb.dto;

import java.util.List;
import java.util.Map;

public class Entry {
	private String key;
	private List<Map<String, String> > values;
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public List<Map<String, String>> getValues() {
		return values;
	}
	public void setValues(List<Map<String, String>> values) {
		this.values = values;
	}
}