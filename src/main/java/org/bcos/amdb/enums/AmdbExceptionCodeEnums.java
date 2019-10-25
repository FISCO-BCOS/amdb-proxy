/**
 * Copyright 2014-2019  the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.bcos.amdb.enums;



/**
 * ExceptionCodeEnums
 *
 * @Description: ExceptionCodeEnums
 * @author graysonzhang
 * @data 2019-07-02 16:38:13
 *
 */

public enum AmdbExceptionCodeEnums {
    
    
    NO_TABLE_MESSAGE(1000, "this table does not exist"),
    BLOCK_NUM_ERROR_MESSAGE(1001, "block num greater than the max num in db");

    
    AmdbExceptionCodeEnums(int code, String message){
        this.code = code;
        this.message = message;
    }

    private int code;
    
    private String message;

    /**
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
       
}
