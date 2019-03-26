/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xenoblade.zohar.framework.sample.baal.api;

import com.xenoblade.zohar.framework.commons.web.msg.ResponseMessage;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * BaalService
 * @author xenoblade
 * @since 1.0.0
 */
public interface BaalService {

    ResponseMessage<HelloBaalResponse> helloBaal(HelloBaalRequest request);

    ResponseMessage testException(Integer errorCode);

    ResponseMessage testVallidate(@Min(value = 1, message = "id 必须大于0") Integer id, TestVallidateRequest request);

    ResponseMessage<TestExcludeBody> testExclude(List<String> excludeFields, TestExcludeBody excludeBody);


    @Data
    class HelloBaalRequest implements Serializable {

        private static final long serialVersionUID = 2122526280765157441L;


        @NotEmpty(message = "hello字段不能为空")
        private String hello;

    }

    @Data
    class HelloBaalResponse implements Serializable {

        private static final long serialVersionUID = 2122526280765157441L;


        private String response;

    }

    @Data
    class TestVallidateRequest implements Serializable {

        private static final long serialVersionUID = -3731794253024922988L;

        @NotBlank(message = "不能为空")
        private String str;

        @Min(value = 1, message = "必须大于0")
        private Integer num;


    }


    @Data
    class TestExcludeBody implements Serializable {

        private static final long serialVersionUID = 3196070488047099276L;

        @NotBlank(message = "字段1不能为空")
        private String field1;

        @NotBlank(message = "字段2不能为空")
        private String field2;

    }

}
