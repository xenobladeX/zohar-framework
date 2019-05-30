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

import cn.hutool.core.util.StrUtil;
import com.xenoblade.zohar.framework.commons.utils.support.EEncodeType;
import com.xenoblade.zohar.framework.commons.utils.support.EHashType;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * AbstractStringRedisSerializer
 * @author xenoblade
 * @since 1.0.0
 */
public abstract class AbstractStringRedisSerializer implements RedisSerializer<Object> {

    private EEncodeType encodeType = EEncodeType.NONE;

    private EHashType hashType = EHashType.NONE;

    public AbstractStringRedisSerializer() {
    }

    public AbstractStringRedisSerializer(EEncodeType encodeType, EHashType hashType) {
        this.encodeType = encodeType;
        this.hashType = hashType;
    }

    @Override
    public String deserialize(byte[] bytes) {
        // TODO deserialize
        return (bytes == null ? null : StrUtil.utf8Str(bytes));
    }

    @Override
    public byte[] serialize(Object object) {
        if (object == null) {
            return null;
        }

        byte[] bytes = objectToBytes(object);

        if (EHashType.NONE == hashType) {
            // 如果不做哈希，则使用编码方法
            bytes = com.xenoblade.zohar.framework.commons.utils.StrUtil.encode(bytes, encodeType);
        } else {
            // 直接进行哈希算法
            bytes = com.xenoblade.zohar.framework.commons.utils.StrUtil.hash(bytes, hashType);
        }

        return bytes;
    }

    protected abstract byte[] objectToBytes(Object object);

}
