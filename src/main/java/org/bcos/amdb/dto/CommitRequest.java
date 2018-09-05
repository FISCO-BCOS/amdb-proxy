package org.bcos.amdb.dto;

import java.util.List;

public class CommitRequest {
	private String blockHash;
	private Integer num;
	private List<TableData> data;
	
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
	public List<TableData> getData() {
		return data;
	}
	public void setData(List<TableData> data) {
		this.data = data;
	}
}
