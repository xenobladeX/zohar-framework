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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.xenoblade.zohar.framework.commons.redis.serial.FastJsonSerializerWrapper;
import lombok.extern.slf4j.Slf4j;

/**
 * FastJsonStringRedisSerilizer
 * @author xenoblade
 * @since 1.0.0
 */
@Slf4j
public class FastJsonStringRedisSerilizer extends AbstractStringRedisSerializer{

    public FastJsonStringRedisSerilizer(String... packages) {
        super();
        try {
            ParserConfig.getGlobalInstance().addAccept("com.xenoblade.zohar.");
            if (packages != null && packages.length > 0) {
                for (String packageName : packages) {
                    ParserConfig.getGlobalInstance().addAccept(packageName);
                }
            }
        } catch (Throwable e) {
            log.warn("fastjson 版本太低，反序列化有被攻击的风险", e);
        }
    }


    @Override protected String objectToString(Object object) {
        return JSON.toJSONString(new FastJsonSerializerWrapper(object), SerializerFeature.WriteClassName);
    }
}
