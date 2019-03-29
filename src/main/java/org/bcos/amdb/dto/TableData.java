package org.bcos.amdb.dto;

import java.util.List;
import java.util.Map;

public class TableData {
	private String table;
	private List<Map<String, String>> entries;
	
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public List<Map<String, String>> getEntries() {
		return entries;
	}
	public void setEntries(List<Map<String, String>> entries) {
		this.entries = entries;
	}
}