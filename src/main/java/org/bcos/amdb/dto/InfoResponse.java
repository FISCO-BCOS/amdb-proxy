package org.bcos.amdb.dto;

import java.util.List;

public class InfoResponse {
	private String key;
	private List<String> indices;
	
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
}
