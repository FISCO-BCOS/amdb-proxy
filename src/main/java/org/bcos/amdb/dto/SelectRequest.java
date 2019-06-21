package org.bcos.amdb.dto;

import java.util.List;

public class SelectRequest {
	
	private String blockHash;
	private Integer num;
	private String table;
	private String key;
	private List<List<String>>	condition;
	
	public List<List<String>> getCondition() {
		return condition;
	}

	public void setCondition(List<List<String>> condition) {
		this.condition = condition;
	}

	public String getBlockHash() {
		return blockHash;
	}

	public void setBlockHash(String blockHash) {
		this.blockHash = blockHash;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
