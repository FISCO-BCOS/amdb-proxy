package org.bcos.amdb.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Header {
	private String op;

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}
}