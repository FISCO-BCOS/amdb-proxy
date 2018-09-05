package org.bcos.amdb.dto;

import java.util.List;
import java.util.Set;

public class SelectResponse {
	private Set<String> columns;
	private List<List<Object>> data;

	public Set<String> getColumns() {
		return columns;
	}

	public void setColumns(Set<String> columns) {
		this.columns = columns;
	}

	public List<List<Object>> getData() {
		return data;
	}

	public void setData(List<List<Object>> data) {
		this.data = data;
	}
}
