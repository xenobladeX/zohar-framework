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
package com.xenoblade.zohar.framework.cache.core.util;

import lombok.experimental.UtilityClass;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import static com.xenoblade.zohar.framework.cache.core.support.ECacheConstants.REDIS_KEY_SPLIT;

/**
 * RedisLockUtils
 * @author xenoblade
 * @since 1.0.0
 */
@UtilityClass
public class RedisLockUtils {

    private static final String DEFAULT_LOCK_KEY_SUFFIX = REDIS_KEY_SPLIT + "lock";


    public static RLock getRLock(RedissonClient redissonClient, String key, String suffix) {
        return redissonClient.getLock(key + suffix);
    }


    public static RLock getRLock(RedissonClient redissonClient, String key) {
        return redissonClient.getLock(key + DEFAULT_LOCK_KEY_SUFFIX);
    }

}
