/*
 * Copyright [2022] [xenoblade]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xenoblade.zohar.framework.core.api;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xenoblade.zohar.framework.core.api.dto.Response;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DTOTest
 *
 * @author xenoblade
 * @since 0.0.1
 */
public class DTOTest {

    private Logger logger = LoggerFactory.getLogger(DTOTest.class);

    @Test
    public void testSerialResponseByJackson() throws Exception{
        Response<String> response = Response.of("test");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setConfig(objectMapper.getSerializationConfig()
                .with(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY));
        String json = objectMapper.writeValueAsString(response);
        Assert.assertEquals("{\"data\":\"test\",\"errCode\":100000,\"errMessage\":\"OK\"}", json);
    }


    @Test
    public void testSerialResponseByFastJson() {
        Response<String> response = Response.of("test");
        String json = JSON.toJSONString(response);
        Assert.assertEquals("{\"data\":\"test\",\"errCode\":100000,\"errMessage\":\"OK\"}", json);
    }

}