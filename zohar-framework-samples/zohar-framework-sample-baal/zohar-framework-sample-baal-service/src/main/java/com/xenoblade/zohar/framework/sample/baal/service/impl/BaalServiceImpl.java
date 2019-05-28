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
package com.xenoblade.zohar.framework.sample.baal.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.xenoblade.zohar.framework.commons.api.enums.ZoharErrorCode;
import com.xenoblade.zohar.framework.commons.api.exception.NotFoundException;
import com.xenoblade.zohar.framework.commons.api.exception.ZoharException;
import com.xenoblade.zohar.framework.commons.log.api.annotation.AccessLogger;
import com.xenoblade.zohar.framework.sample.baal.api.dto.HelloBaalRequest;
import com.xenoblade.zohar.framework.sample.baal.api.dto.HelloBaalResponse;
import com.xenoblade.zohar.framework.sample.baal.api.dto.RedisStoreObject;
import com.xenoblade.zohar.framework.sample.baal.api.dto.TestExcludeBody;
import com.xenoblade.zohar.framework.sample.baal.api.dto.TestVallidateRequest;
import com.xenoblade.zohar.framework.sample.baal.service.IBaalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * BaalServiceImpl
 * @author xenoblade
 * @since 1.0.0
 */
@Service
@Slf4j
public class BaalServiceImpl implements IBaalService{

    private static String TEST_REDIS_KEY = "test:redis:key";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public HelloBaalResponse testRest(HelloBaalRequest request) {
        HelloBaalResponse response = new HelloBaalResponse();
        response.setResponse("Hello, this is baal");
        return response;
    }

    @Override
    public void testException(Integer errorCode) throws ZoharException {
        ZoharErrorCode zoharErrorCode = ZoharErrorCode.CodeOf(errorCode);
        throw new NotFoundException(zoharErrorCode);
    }

    @Override
    public void testVallidate(Integer id, TestVallidateRequest request) {

    }

    @Override
    public TestExcludeBody testExclude(List<String> excludeFieldList,
                                       TestExcludeBody excludeBody) {
        if (excludeFieldList != null && excludeFieldList.size() > 0) {

            String[] excludeFieldArray = new String[excludeFieldList.size()];
            TestExcludeBody newExcludeBody = new TestExcludeBody();
            BeanUtil.copyProperties(excludeBody, newExcludeBody, excludeFieldList.toArray(excludeFieldArray));
            return newExcludeBody;
        } else {
            return excludeBody;
        }
    }

    @Override public RedisStoreObject testRedis(RedisStoreObject storeObject) {
        redisTemplate.opsForValue().set(TEST_REDIS_KEY + storeObject.getStr(), storeObject);

        return Optional
                .ofNullable((RedisStoreObject) redisTemplate.opsForValue().get(storeObject))
                .orElseThrow(NotFoundException::new);
    }
}
