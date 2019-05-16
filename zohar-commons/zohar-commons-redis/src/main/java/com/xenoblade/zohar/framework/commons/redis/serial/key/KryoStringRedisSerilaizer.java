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

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSON;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.ByteArrayOutputStream;

/**
 * KryoStringRedisSerilaizer
 * @author xenoblade
 * @since 1.0.0
 */
public class KryoStringRedisSerilaizer extends AbstractStringRedisSerializer{

    private static final ThreadLocal<Kryo> kryos = ThreadLocal.withInitial(Kryo::new);

    private Class clazz = Object.class;

    public KryoStringRedisSerilaizer() {
        super();
    }

    @Override protected String objectToString(Object object) {
        Kryo kryo = kryos.get();
        // 设置成false 序列化速度更快，但是遇到循环应用序列化器会报栈内存溢出
        kryo.setReferences(false);
        kryo.register(clazz);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Output output = new Output(baos)) {
            kryo.writeClassAndObject(output, object);
            output.flush();
            byte[] objectBytes = baos.toByteArray();
            return Base64.encode(objectBytes);
        } catch (Exception e) {
            throw new SerializationException(String.format("KryoRedisSerializer Serial Failed: %s, 【JSON：%s】",
                    e.getMessage(), JSON.toJSONString(object)), e);
        } finally {
            kryos.remove();
        }
    }
}
