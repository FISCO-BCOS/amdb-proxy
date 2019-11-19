
package org.bcos.amdb.dto;

/**
 * SelectByNumRequest
 *
 * @Description: SelectByNumRequest
 * @author graysonzhang
 * @data 2019-10-15 18:56:26
 *
 */
public class SelectByNumRequest {
	
	private String tableName;
    private long num;
    private long preIndex;
    private int pageSize;
    
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public long getNum() {
		return num;
	}
	public void setNum(long num) {
		this.num = num;
	}
	public long getPreIndex() {
		return preIndex;
	}
	public void setPreIndex(long preIndex) {
		this.preIndex = preIndex;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}   
}
