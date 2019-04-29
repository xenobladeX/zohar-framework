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

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.Assert;

import java.nio.charset.Charset;

/**
 * AbstractStringRedisSerializer
 * @author xenoblade
 * @since 1.0.0
 */
public abstract class AbstractStringRedisSerializer implements RedisSerializer<Object> {

    private final Charset charset;

    private String prefix = "";

    public AbstractStringRedisSerializer() {
        this(Charset.forName("UTF8"), "");
    }

    public AbstractStringRedisSerializer(String prefix) {
        this(Charset.forName("UTF8"), prefix);
    }

    public AbstractStringRedisSerializer(Charset charset, String prefix) {
        Assert.notNull(charset, "Charset must not be null!");
        this.charset = charset;
        this.prefix = prefix;
    }

    @Override
    public String deserialize(byte[] bytes) {
        return (bytes == null ? null : new String(bytes, charset));
    }

    @Override
    public byte[] serialize(Object object) {
        if (object == null) {
            return null;
        }
        String string = objectToString(object);
        if (string == null) {
            return null;
        }
        return (prefix + string).getBytes(charset);
    }

    protected abstract String objectToString(Object object);
}
