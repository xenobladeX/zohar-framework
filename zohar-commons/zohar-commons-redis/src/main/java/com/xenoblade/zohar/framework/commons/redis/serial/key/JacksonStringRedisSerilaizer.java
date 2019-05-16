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
package com.xenoblade.zohar.framework.commons.redis.serial.key;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xenoblade.zohar.framework.commons.redis.serial.SerializationUtils;
import lombok.Setter;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * JacksonStringRedisSerilaizer
 * @author xenoblade
 * @since 1.0.0
 */
public class JacksonStringRedisSerilaizer extends AbstractStringRedisSerializer{

    @Setter
    private ObjectMapper objectMapper = SerializationUtils.jsonRedisObjectMapper();

    @Override protected String objectToString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Could not write JSON: " + e.getMessage(), e);
        }
    }
}
