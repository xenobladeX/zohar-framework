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
package com.xenoblade.zohar.framework.commons.redis.serial.value;

import com.xenoblade.zohar.framework.commons.redis.support.Type;
import org.springframework.data.redis.serializer.SerializationException;

import java.util.List;
import java.util.Set;

/**
 * FastJsonSerializerWrapper
 * @author xenoblade
 * @since 1.0.0
 */
public class FastJsonSerializerWrapper {
    private Object content;

    private String type;

    public FastJsonSerializerWrapper() {
    }

    public FastJsonSerializerWrapper(Object content) {
        this.content = content;

        if (content == null) {
            this.type = Type.NULL.name();
            return;
        }

        if (content instanceof String || content instanceof Integer
                || content instanceof Long || content instanceof Double
                || content instanceof Float || content instanceof Boolean
                || content instanceof Byte || content instanceof Character
                || content instanceof Short) {

            this.type = Type.STRING.name();
            return;
        }

        if (content instanceof List) {
            this.type = Type.LIST.name();
            return;
        }

        if (content instanceof Set) {
            this.type = Type.SET.name();
            return;
        }

        // TODO: fastjson suppport array and enum
        if (content.getClass().isArray()) {
            throw new SerializationException("FastJsonRedisSerializer 序列化不支持枚数组型");
        }

        if (content.getClass().isEnum()) {
            throw new SerializationException("FastJsonRedisSerializer 序列化不支持枚举类型");
        }

        this.type = Type.OBJECT.name();
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
