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

import com.esotericsoftware.kryo.Kryo;
import com.xenoblade.zohar.framework.commons.redis.serial.SerializationUtils;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSON;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.springframework.cache.support.NullValue;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.ByteArrayOutputStream;

/**
 * KryoRedisSerializer
 * @author xenoblade
 * @since 1.0.0
 */
@Slf4j
public class KryoRedisSerializer<T> implements RedisSerializer<T> {
    private static final ThreadLocal<Kryo> kryos = ThreadLocal.withInitial(Kryo::new);

    private Class<T> clazz;

    public KryoRedisSerializer(Class<T> clazz) {
        super();
        this.clazz = clazz;
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (t == null) {
            return SerializationUtils.EMPTY_ARRAY;
        }

        Kryo kryo = kryos.get();
        // 设置成false 序列化速度更快，但是遇到循环应用序列化器会报栈内存溢出
        kryo.setReferences(false);
        kryo.register(clazz);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Output output = new Output(baos)) {
            kryo.writeClassAndObject(output, t);
            output.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new SerializationException(String.format("KryoRedisSerializer Serial Failed: %s, 【JSON：%s】",
                    e.getMessage(), JSON.toJSONString(t)), e);
        } finally {
            kryos.remove();
        }
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (SerializationUtils.isEmpty(bytes)) {
            return null;
        }

        Kryo kryo = kryos.get();
        // 设置成false 序列化速度更快，但是遇到循环应用序列化器会报栈内存溢出
        kryo.setReferences(false);
        kryo.register(clazz);

        try (Input input = new Input(bytes)) {

            Object result = kryo.readClassAndObject(input);
            if (result instanceof NullValue) {
                return null;
            }
            return (T) result;
        } catch (Exception e) {
            throw new SerializationException(String.format("FastJsonRedisSerializer Serial Failed: %s, 【JSON：%s】",
                    e.getMessage(), JSON.toJSONString(bytes)), e);
        } finally {
            kryos.remove();
        }
    }
}
