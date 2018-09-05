package org.bcos.amdb.dto;

import java.util.List;

public class TableData {
	private String table;
	private List<Entry> entries;
	
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public List<Entry> getEntries() {
		return entries;
	}
	public void setEntries(List<Entry> entries) {
		this.entries = entries;
	}
}