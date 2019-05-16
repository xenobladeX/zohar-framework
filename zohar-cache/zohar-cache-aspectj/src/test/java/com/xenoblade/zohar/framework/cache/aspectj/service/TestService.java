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
package com.xenoblade.zohar.framework.cache.aspectj.service;

import com.xenoblade.zohar.framework.cache.aspectj.annotation.CacheEvict;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.CachePut;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.Cacheable;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.FirstCache;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.SecondaryCache;
import com.xenoblade.zohar.framework.cache.aspectj.model.User;
import com.xenoblade.zohar.framework.cache.core.support.ECacheMode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * TestService
 * @author xenoblade
 * @since 1.0.0
 */
@Service
@Slf4j
public class TestService {

    @Cacheable(value = "user-info", key = "#userId", ignoreException = false,
            firstCache = @FirstCache(expireTime = 4, timeUnit = TimeUnit.SECONDS),
            secondaryCache = @SecondaryCache(expireTime = 10, preloadTime = 3,
                    forceRefresh = true, timeUnit = TimeUnit.SECONDS))
    public User getUserById(long userId) {
        log.info("测试正常配置的缓存方法，参数是基本类型");
        User user = new User();
        user.setUserId(userId);
        user.setAge(31);
        user.setLastName(new String[]{"w", "y", "h"});
        return user;
    }

    @Cacheable(value = "user-info", ignoreException = false,
            firstCache = @FirstCache(expireTime = 4, timeUnit = TimeUnit.SECONDS),
            secondaryCache = @SecondaryCache(expireTime = 10, preloadTime = 3, forceRefresh = true, timeUnit = TimeUnit.SECONDS))
    public User getUserNoKey(long userId, String[] lastName) {
        log.info("测试没有配置key的缓存方法，参数是基本类型和数组的缓存缓存方法");
        User user = new User();
        user.setUserId(userId);
        user.setAge(31);
        user.setLastName(lastName);
        return user;
    }

    @Cacheable(value = "user-info",
            firstCache = @FirstCache(expireTime = 4, timeUnit = TimeUnit.SECONDS), ignoreException = false,
            secondaryCache = @SecondaryCache(expireTime = 10, preloadTime = 3, forceRefresh = true, timeUnit = TimeUnit.SECONDS))
    public User getUserObjectPram(User user) {
        log.info("测试没有配置key的缓存方法，参数是复杂对象");
        return user;
    }

    @Cacheable(value = "user-info", ignoreException = false,
            firstCache = @FirstCache(expireTime = 4, timeUnit = TimeUnit.SECONDS),
            secondaryCache = @SecondaryCache(expireTime = 10, preloadTime = 3, forceRefresh = true, timeUnit = TimeUnit.SECONDS))
    public User getUser(User user, int age) {
        log.info("测试没有配置key的缓存方法，参数是复杂对象和基本量类型");
        user.setAge(age);
        return user;
    }


    @Cacheable(value = "user-info", ignoreException = false,
            firstCache = @FirstCache(expireTime = 4, timeUnit = TimeUnit.SECONDS),
            secondaryCache = @SecondaryCache(expireTime = 10, preloadTime = 3, forceRefresh = true, timeUnit = TimeUnit.SECONDS))
    public User getUserNoParam() {
        log.info("测试没有配置key的缓存方法，没有参数");
        User user = new User();
        user.setUserId(223);
        user.setAge(31);
        user.setLastName(new String[]{"w", "y", "h"});
        return user;
    }

    @Cacheable(value = "user-info", key = "#user.userId", ignoreException = false,
            firstCache = @FirstCache(expireTime = 4, timeUnit = TimeUnit.SECONDS),
            secondaryCache = @SecondaryCache(expireTime = 10, preloadTime = 3, forceRefresh = true, timeUnit = TimeUnit.SECONDS))
    public User getNullObjectPram(User user) {
        log.info("测试参数是NULL对象，不忽略异常");
        return user;
    }

    @Cacheable(value = "user-info",
            firstCache = @FirstCache(expireTime = 4, timeUnit = TimeUnit.SECONDS),
            secondaryCache = @SecondaryCache(expireTime = 10, preloadTime = 3, forceRefresh = true, timeUnit = TimeUnit.SECONDS))
    public User getNullObjectPramIgnoreException(User user) {
        log.info("测试参数是NULL对象，忽略异常");
        return user;
    }

    @Cacheable(value = "user-info", key = "#userId", ignoreException = false,
            firstCache = @FirstCache(expireTime = 4, timeUnit = TimeUnit.SECONDS),
            secondaryCache = @SecondaryCache(expireTime = 10, preloadTime = 3, forceRefresh = true,
                    timeUnit = TimeUnit.SECONDS, isAllowNullValue = true, magnification = 1))
    public User getNullUser(Long userId) {
        log.info("缓存方法返回NULL");
        return null;
    }

    @Cacheable(value = "user-info", key = "#userId", ignoreException = false,
            firstCache = @FirstCache(expireTime = 4, timeUnit = TimeUnit.SECONDS),
            secondaryCache = @SecondaryCache(expireTime = 100, preloadTime = 70, forceRefresh = true,
                    timeUnit = TimeUnit.SECONDS, isAllowNullValue = true, magnification = 10))
    public User getNullUserAllowNullValueTrueMagnification(Long userId) {
        log.info("缓存方法返回NULL");
        return null;
    }

    @Cacheable(value = "user-info", key = "#userId", ignoreException = false,
            firstCache = @FirstCache(expireTime = 4, timeUnit = TimeUnit.SECONDS),
            secondaryCache = @SecondaryCache(expireTime = 10, preloadTime = 7, forceRefresh = true,
                    timeUnit = TimeUnit.SECONDS, isAllowNullValue = false))
    public User getNullUserAllowNullValueFalse(Long userId) {
        log.info("缓存方法返回NULL");
        return null;
    }

    @Cacheable(value = "user-info", ignoreException = false,
            firstCache = @FirstCache(expireTime = 4, timeUnit = TimeUnit.SECONDS),
            secondaryCache = @SecondaryCache(expireTime = 10, preloadTime = 3, forceRefresh = true, timeUnit = TimeUnit.SECONDS))
    public String getString(long userId) {
        log.info("缓存方法返回字符串");
        return "User";
    }

    @Cacheable(value = "user-info", ignoreException = false,
            firstCache = @FirstCache(expireTime = 4, timeUnit = TimeUnit.SECONDS),
            secondaryCache = @SecondaryCache(expireTime = 10, preloadTime = 3, forceRefresh = true, timeUnit = TimeUnit.SECONDS))
    public int getInt(long userId) {
        log.info("缓存方法返回基本类型");
        return 111;
    }

    @Cacheable(value = "user-info", ignoreException = false,
            firstCache = @FirstCache(expireTime = 4, timeUnit = TimeUnit.SECONDS),
            secondaryCache = @SecondaryCache(expireTime = 10, preloadTime = 3, forceRefresh = true, timeUnit = TimeUnit.SECONDS))
    public Long getLong(long userId) {
        log.info("缓存方法返回包装类型");
        return 1111L;
    }

    @Cacheable(value = "user-info", ignoreException = false,
            firstCache = @FirstCache(expireTime = 4, timeUnit = TimeUnit.SECONDS),
            secondaryCache = @SecondaryCache(expireTime = 10, preloadTime = 3, forceRefresh = true, timeUnit = TimeUnit.SECONDS))
    public double getDouble(long userId) {
        log.info("缓存方法返回包装类型");
        return 111.2;
    }

    @Cacheable(value = "user-info", ignoreException = false,
            firstCache = @FirstCache(expireTime = 4, timeUnit = TimeUnit.SECONDS),
            secondaryCache = @SecondaryCache(expireTime = 10, preloadTime = 3, forceRefresh = true, timeUnit = TimeUnit.SECONDS))
    public float getFloat(long userId) {
        log.info("缓存方法返回包装类型");
        return 11.311F;
    }

    @Cacheable(value = "user-info", ignoreException = false,
            firstCache = @FirstCache(expireTime = 4, timeUnit = TimeUnit.SECONDS),
            secondaryCache = @SecondaryCache(expireTime = 10, preloadTime = 3, forceRefresh = true, timeUnit = TimeUnit.SECONDS))
    public BigDecimal getBigDecimal(long userId) {
        log.info("缓存方法返回包装类型");
        return new BigDecimal(33.33);
    }

    @Cacheable(value = "user-info", ignoreException = false,
            firstCache = @FirstCache(expireTime = 4, timeUnit = TimeUnit.SECONDS),
            secondaryCache = @SecondaryCache(expireTime = 10, preloadTime = 3, forceRefresh = true, timeUnit = TimeUnit.SECONDS))
    public Date getDate(long userId) {
        log.info("缓存方法返回Date类型");
        return new Date();
    }


    @Cacheable(value = "user-info", ignoreException = false,
            firstCache = @FirstCache(expireTime = 4, timeUnit = TimeUnit.SECONDS),
            secondaryCache = @SecondaryCache(expireTime = 10, preloadTime = 3, forceRefresh = true, timeUnit = TimeUnit.SECONDS))
    public ECacheMode getEnum(long userId) {
        log.info("缓存方法返回枚举");
        return ECacheMode.ONLY_FIRST;
    }

    @Cacheable(value = "user-info", ignoreException = false,
            firstCache = @FirstCache(expireTime = 4, timeUnit = TimeUnit.SECONDS),
            secondaryCache = @SecondaryCache(expireTime = 10, preloadTime = 3, forceRefresh = true, timeUnit = TimeUnit.SECONDS))
    public long[] getArray(long userId) {
        log.info("缓存方法返回数组");
        return new long[]{111, 222, 333};
    }

    @Cacheable(value = "user-info", ignoreException = false,
            firstCache = @FirstCache(expireTime = 4, timeUnit = TimeUnit.SECONDS),
            secondaryCache = @SecondaryCache(expireTime = 10, preloadTime = 3, forceRefresh = true, timeUnit = TimeUnit.SECONDS))
    public User[] getObjectArray(long userId) {
        log.info("缓存方法返回数组");
        User user = new User();
        return new User[]{user, user, user};
    }

    @Cacheable(value = "user-info", ignoreException = false,
            firstCache = @FirstCache(expireTime = 4, timeUnit = TimeUnit.SECONDS),
            secondaryCache = @SecondaryCache(expireTime = 10, preloadTime = 3, forceRefresh = true, timeUnit = TimeUnit.SECONDS))
    public List<String> getList(long userId) {
        log.info("调用方法获取数组");
        List<String> list = new ArrayList<>();
        list.add("111");
        list.add("112");
        list.add("113");

        return list;
    }

    @Cacheable(value = "user-info", ignoreException = false,
            firstCache = @FirstCache(expireTime = 4, timeUnit = TimeUnit.SECONDS),
            secondaryCache = @SecondaryCache(expireTime = 10, preloadTime = 3, forceRefresh = true, timeUnit = TimeUnit.SECONDS))
    public LinkedList<String> getLinkedList(long userId) {
        log.info("调用方法获取数组");
        LinkedList<String> list = new LinkedList<>();
        list.add("111");
        list.add("112");
        list.add("113");

        return list;
    }

    @Cacheable(value = "user-info", ignoreException = false,
            firstCache = @FirstCache(expireTime = 4, timeUnit = TimeUnit.SECONDS),
            secondaryCache = @SecondaryCache(expireTime = 10, preloadTime = 3, forceRefresh = true, timeUnit = TimeUnit.SECONDS))
    public List<User> getListObject(long userId) {
        log.info("调用方法获取数组");
        List<User> list = new ArrayList<>();
        User user = new User();
        list.add(user);
        list.add(user);
        list.add(user);

        return list;
    }

    @Cacheable(value = "user-info", ignoreException = false,
            firstCache = @FirstCache(expireTime = 4, timeUnit = TimeUnit.SECONDS),
            secondaryCache = @SecondaryCache(expireTime = 10, preloadTime = 3, forceRefresh = true, timeUnit = TimeUnit.SECONDS))
    public Set<String> getSet(long userId) {
        log.info("调用方法获取数组");
        Set<String> set = new HashSet<>();
        set.add("111");
        set.add("112");
        set.add("113");
        return set;
    }

    @Cacheable(value = "user-info", ignoreException = false,
            firstCache = @FirstCache(expireTime = 4, timeUnit = TimeUnit.SECONDS),
            secondaryCache = @SecondaryCache(expireTime = 10, preloadTime = 3, forceRefresh = true, timeUnit = TimeUnit.SECONDS))
    public Set<User> getSetObject(long userId) {
        log.info("调用方法获取数组");
        Set<User> set = new HashSet<>();
        User user = new User();
        set.add(user);
        return set;
    }

    @Cacheable(value = "user-info", ignoreException = false,
            firstCache = @FirstCache(expireTime = 4, timeUnit = TimeUnit.SECONDS),
            secondaryCache = @SecondaryCache(expireTime = 10, preloadTime = 3, forceRefresh = true, timeUnit = TimeUnit.SECONDS))
    public List<User> getException(long userId) {
        log.info("缓存测试方法");

        throw new RuntimeException("缓存测试方法");
    }


    @CachePut(value = "user-info", key = "#userId", ignoreException = false,
            firstCache = @FirstCache(expireTime = 4, timeUnit = TimeUnit.SECONDS),
            secondaryCache = @SecondaryCache(expireTime = 10, preloadTime = 3, forceRefresh = true, timeUnit = TimeUnit.SECONDS))
    public User putUser(long userId) {
        return new User();
    }

    @CachePut(value = "user-info",
            firstCache = @FirstCache(expireTime = 4, timeUnit = TimeUnit.SECONDS), ignoreException = false,
            secondaryCache = @SecondaryCache(expireTime = 10, preloadTime = 3, forceRefresh = true, timeUnit = TimeUnit.SECONDS))
    public User putUserNoParam() {
        User user = new User();
        return user;
    }

    @CachePut(value = "user-info", key = "#userId", ignoreException = false,
            firstCache = @FirstCache(expireTime = 4, timeUnit = TimeUnit.SECONDS),
            secondaryCache = @SecondaryCache(expireTime = 10, preloadTime = 3, forceRefresh = true,
                    timeUnit = TimeUnit.SECONDS, isAllowNullValue = true))
    public User putNullUser(long userId) {

        return null;
    }

    @CachePut(value = "user-info", key = "#userId", ignoreException = false,
            firstCache = @FirstCache(expireTime = 4, timeUnit = TimeUnit.SECONDS),
            secondaryCache = @SecondaryCache(expireTime = 40, preloadTime = 30, forceRefresh = true,
                    timeUnit = TimeUnit.SECONDS, isAllowNullValue = true, magnification = 10))
    public User putNullUserAllowNullValueTrueMagnification(long userId) {

        return null;
    }

    @CachePut(value = "user-info", key = "#userId", ignoreException = false,
            firstCache = @FirstCache(expireTime = 4, timeUnit = TimeUnit.SECONDS),
            secondaryCache = @SecondaryCache(expireTime = 10, preloadTime = 7, forceRefresh = true, timeUnit = TimeUnit.SECONDS))
    public User putNullUserAllowNullValueFalse(long userId) {

        return null;
    }



    @CachePut(value = "user-info", key = "#userId", ignoreException = false,
            firstCache = @FirstCache(expireTime = 4, timeUnit = TimeUnit.SECONDS),
            secondaryCache = @SecondaryCache(expireTime = 10, preloadTime = 3, forceRefresh = true, timeUnit = TimeUnit.SECONDS))
    public User putUserById(long userId) {
        User user = new User();
        user.setUserId(userId);
        user.setAge(311);
        user.setLastName(new String[]{"w", "y", "h"});

        return user;
    }

    @CacheEvict(value = "user-info", key = "#userId", ignoreException = false)
    public void evictUser(long userId) {

    }

    @CacheEvict(value = "user-info", allEntries = true, ignoreException = false)
    public void evictAllUser() {
    }
}
