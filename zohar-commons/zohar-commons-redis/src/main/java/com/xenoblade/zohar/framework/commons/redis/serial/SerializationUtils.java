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
package com.xenoblade.zohar.framework.commons.redis.serial;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.xenoblade.zohar.framework.commons.utils.jackson.protobuf.CustomProtobufModule;
import lombok.experimental.UtilityClass;

/**
 * SerializationUtils
 * @author xenoblade
 * @since 1.0.0
 */
@UtilityClass
public class SerializationUtils {

    public static final byte[] EMPTY_ARRAY = new byte[0];

    public static boolean isEmpty(byte[] data) {
        return (data == null || data.length == 0);
    }

    public static ObjectMapper jsonRedisObjectMapper() {
        ObjectMapper jsonRedisObjectMapper = new ObjectMapper();
        jsonRedisObjectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        jsonRedisObjectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        // 序列化异常不抛出
        jsonRedisObjectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        jsonRedisObjectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // 忽略不能转移的字符
        jsonRedisObjectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
        // 自定义序列化方式
        SimpleModule simpleModule = new SimpleModule();
        // Long 转 String 防止精度丢失
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        jsonRedisObjectMapper.registerModule(simpleModule);
        // Jackson protobuf format
        jsonRedisObjectMapper.registerModule(new CustomProtobufModule());

        return jsonRedisObjectMapper;
    }

}
