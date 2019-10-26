
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
    
    private long num;
    
    private String tableName;

    /**
     * @return the num
     */
    public long getNum() {
        return num;
    }

    /**
     * @param num the num to set
     */
    public void setNum(Integer num) {
        this.num = num;
    }

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName the tableName to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }   

}
