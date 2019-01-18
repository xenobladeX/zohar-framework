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

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * SnowflakeIdGenerator
 * @author xenoblade
 * @since 1.0.0
 */
@Slf4j
public class SnowflakeIdGenerator {

    private long workerId;
    private long dataCenterId;
    private long sequence = 0L;

    private long twepoch = 1288834974657L;

    private long workerIdBits     = 5L;
    private long datacenterIdBits = 5L;
    private long maxWorkerId      = -1L ^ (-1L << workerIdBits);
    private long maxDataCenterId  = -1L ^ (-1L << datacenterIdBits);
    private long sequenceBits     = 12L;

    private long workerIdShift      = sequenceBits;
    private long datacenterIdShift  = sequenceBits + workerIdBits;
    private long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private long sequenceMask       = -1L ^ (-1L << sequenceBits);

    private long lastTimestamp = -1L;

    private static final SnowflakeIdGenerator generator;

    static {
        Random random = new Random();
        long workerId = Long.getLong("id-worker", random.nextInt(31));
        long dataCenterId = Long.getLong("id-datacenter", random.nextInt(31));
        generator = new SnowflakeIdGenerator(workerId, dataCenterId);
    }

    public static SnowflakeIdGenerator getInstance() {
        return generator;
    }

    public SnowflakeIdGenerator(long workerId, long dataCenterId) {
        // sanity check for workerId
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDataCenterId));
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
        log.info("worker starting. timestamp left shift {}, datacenter id bits {}, worker id bits {}, sequence bits {}, workerid {}", timestampLeftShift, datacenterIdBits, workerIdBits, sequenceBits, workerId);
    }

    public synchronized long nextId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            log.error("clock is moving backwards.  Rejecting requests until {}.", lastTimestamp);
            throw new UnsupportedOperationException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - twepoch) << timestampLeftShift) | (dataCenterId << datacenterIdShift) | (workerId << workerIdShift) | sequence;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }

}
