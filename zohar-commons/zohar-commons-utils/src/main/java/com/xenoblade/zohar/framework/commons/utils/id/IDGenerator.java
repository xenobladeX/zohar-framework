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
package com.xenoblade.zohar.framework.commons.utils.id;

import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * IDGenerator
 * @author xenoblade
 * @since 1.0.0
 */
@FunctionalInterface
public interface IDGenerator<T> {

    T generate();

    /**
     * 使用UUID生成id
     */
    IDGenerator<String> UUID = () -> java.util.UUID.randomUUID().toString();

    /**
     * 随机字符
     */
    IDGenerator<String> RANDOM = () -> RandomStringUtils.random(16, true, true);

    /**
     * md5(uuid()+random())
     */
    IDGenerator<String> MD5 = () -> {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(UUID.generate().concat(RANDOM.generate()).getBytes());
            return new BigInteger(1, md.digest()).toString(32);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    };

    /**
     * 雪花算法
     */
    IDGenerator<Long>   SNOW_FLAKE        = SnowflakeIdGenerator.getInstance()::nextId;

    /**
     * 雪花算法转String
     */
    IDGenerator<String> SNOW_FLAKE_STRING = () -> String.valueOf(SNOW_FLAKE.generate());

    /**
     * 雪花算法的16进制
     */
    IDGenerator<String> SNOW_FLAKE_HEX = () -> Long.toHexString(SNOW_FLAKE.generate());

}
