/*
 * Copyright 2014-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcos.amdb.exception;

import org.bcos.amdb.enums.AmdbExceptionCodeEnums;

/**
 * PkeyMgrException
 *
 * @Description: PkeyMgrException
 * @author graysonzhang
 * @data 2019-07-10 15:23:35
 *
 */
public class AmdbException extends Exception {
    
    /** @Fields serialVersionUID : TODO */
    private static final long serialVersionUID = 893822168485972751L;
    private AmdbExceptionCodeEnums etlEnum;

    public AmdbException(AmdbExceptionCodeEnums etlEnum) {
        super(etlEnum.getMessage());
        this.etlEnum = etlEnum;
    }

    public AmdbException(String msg) {
        super(msg);
        this.etlEnum.setMessage(msg);
    }

    public AmdbExceptionCodeEnums getCodeMessageEnums() {
        return etlEnum;
    }
    

}
