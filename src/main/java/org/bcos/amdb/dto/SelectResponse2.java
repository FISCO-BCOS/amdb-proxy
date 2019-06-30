package org.bcos.amdb.dto;

import java.util.List;
import java.util.Map;

public class SelectResponse2 {
	private List<Map<String, Object>> column_value;

	public List<Map<String, Object>> getColumn_value() {
		return column_value;
	}

	public void setColumn_value(List<Map<String, Object>> column_value) {
		this.column_value = column_value;
	}
}
