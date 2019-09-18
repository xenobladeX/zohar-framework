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
package com.xenoblade.zohar.framework.cache.aspectj.annotation;

import com.xenoblade.zohar.framework.commons.redis.serial.ERedisSerialType;
import com.xenoblade.zohar.framework.commons.utils.support.EEncodeType;
import com.xenoblade.zohar.framework.cache.core.support.EExpireMode;
import com.xenoblade.zohar.framework.commons.utils.support.EHashType;
import lombok.experimental.UtilityClass;

import java.lang.annotation.Annotation;
import java.util.concurrent.TimeUnit;

/**
 * AnnotationConstants
 * @author xenoblade
 * @since 1.0.0
 */
@UtilityClass
public class AnnotationConstants {

    public static FirstCache defaultFirstCache() {
        FirstCache firstCache = new FirstCache(){

            /**
             * Returns the annotation type of this annotation.
             * @return the annotation type of this annotation
             */
            @Override public Class<? extends Annotation> annotationType() {
                return FirstCache.class;
            }

            /**
             * 缓存初始Size
             *
             * @return int
             */
            @Override public int initialCapacity() {
                return 10;
            }

            /**
             * 缓存最大Size
             *
             * @return int
             */
            @Override public int maximumSize() {
                return 5000;
            }

            /**
             * 缓存有效时间
             *
             * @return int
             */
            @Override public int expireTime() {
                return 9;
            }

            /**
             * 缓存时间单位
             *
             * @return TimeUnit
             */
            @Override public TimeUnit timeUnit() {
                return TimeUnit.MINUTES;
            }

            /**
             * 缓存失效模式
             *
             * @return ExpireMode
             * @see EExpireMode
             */
            @Override public EExpireMode expireMode() {
                return EExpireMode.WRITE;
            }
        };

        return firstCache;
    }


    public static SecondaryCache defaultSecondaryCache() {
        SecondaryCache secondaryCache = new SecondaryCache() {

            /**
             * Returns the annotation type of this annotation.
             * @return the annotation type of this annotation
             */
            @Override public Class<? extends Annotation> annotationType() {
                return SecondaryCache.class;
            }

            /**
             * 缓存有效时间
             *
             * @return long
             */
            @Override public long expireTime() {
                return 5;
            }

            /**
             * 缓存主动在失效前强制刷新缓存的时间
             * 建议是： preloadTime = expireTime * 0.2
             *
             * @return long
             */
            @Override public long preloadTime() {
                return 1;
            }

            /**
             * 时间单位 {@link TimeUnit}
             *
             * @return TimeUnit
             */
            @Override public TimeUnit timeUnit() {
                return TimeUnit.HOURS;
            }

            /**
             * 是否强制刷新（直接执行被缓存方法），默认是false
             *
             * @return boolean
             */
            @Override public boolean forceRefresh() {
                return false;
            }

            /**
             * 是否允许存NULL值
             *
             * @return boolean
             */
            @Override public boolean isAllowNullValue() {
                return false;
            }

            /**
             * 非空值和null值之间的时间倍率，默认是1。isAllowNullValue=true才有效
             * <p>
             * 如配置缓存的有效时间是200秒，倍率这设置成10，
             * 那么当缓存value为null时，缓存的有效时间将是20秒，非空时为200秒
             * <p/>
             *
             * @return int
             */
            @Override public int magnification() {
                return 1;
            }

            @Override
            public ERedisSerialType keySerialType() {
                return ERedisSerialType.JDK;
            }

            @Override public EEncodeType keyEncodeType() {
                return EEncodeType.NONE;
            }

            @Override public EHashType keyHashType() {
                return EHashType.MD5;
            }

            @Override
            public ERedisSerialType valueSerialType() {
                return ERedisSerialType.FASTJSON;
            }
        };

        return secondaryCache;
    }

}
