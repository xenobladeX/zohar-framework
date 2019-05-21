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

import cn.hutool.core.util.ClassUtil;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.nio.charset.Charset;

/**
 * AbstractStringRedisSerializer
 * @author xenoblade
 * @since 1.0.0
 */
public abstract class AbstractStringRedisSerializer implements RedisSerializer<Object> {

    private static final Charset CAHRSET_UTF8 = Charset.forName("UTF8");



    public AbstractStringRedisSerializer() {
    }

    @Override
    public String deserialize(byte[] bytes) {
        return (bytes == null ? null : new String(bytes, CAHRSET_UTF8));
    }

    @Override
    public byte[] serialize(Object object) {
        if (object == null) {
            return null;
        }
        
        String string = null;
        if (object.getClass() == int.class) {
            string = String.valueOf((int)object);
        }  else if (object.getClass() == char.class) {
            string  = String.valueOf((char)object);
        } else if (object.getClass() == char[].class) {
            string = String.valueOf((char[]) object);
        } else if (object.getClass() == long.class) {
            string  = String.valueOf((long)object);
        } else if (object.getClass() == float.class) {
            string = String.valueOf((float)object);
        } else if (object.getClass() == double.class) {
            string = String.valueOf((double)object);
        } else if (object.getClass() == boolean.class) {
            string = String.valueOf((boolean)object);
        }
        else if (object instanceof Integer) {
            string = String.valueOf((Integer) object);
        } else if (object instanceof Long) {
            string = String.valueOf((Long)object);
        } else if (object instanceof Float) {
            string = String.valueOf((Float)object);
        } else if (object instanceof Double) {
            string = String.valueOf((Double)object);
        } else if (object instanceof Boolean) {
            string = String.valueOf((Boolean)object);
        } else if (object instanceof String) {
            string = (String)object;
        } else {
            string = objectToString(object);
        }

        if (string == null) {
            return null;
        }
        return string.getBytes(CAHRSET_UTF8);
    }

    protected abstract String objectToString(Object object);

}
