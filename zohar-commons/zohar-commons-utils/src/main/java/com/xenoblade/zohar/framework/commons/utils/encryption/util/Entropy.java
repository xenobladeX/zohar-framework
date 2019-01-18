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
package com.xenoblade.zohar.framework.commons.utils.encryption.util;

/**
 * Entropy
 * @author xenoblade
 * @since 1.0.0
 */
public class Entropy {

    private Entropy() {
    }

    /**
     * <p>Calculates and returns the Shannon entropy of the given byte array.</p>
     * <p>The returned value is a value between <code>0</code> and <code>1</code> where
     * a value of <code>0</code> signifies no entropy and a value of <code>1</code> maximum entropy.</p>
     * <p>This method may be used to assess the entropy of randomly generated key material but is in no way
     * by itself a reliable indicator of true randomness.</p>
     * <p>It indicates distribution entropy as opposed to sequence entropy.</p>
     * @param bytes
     * @return
     */
    public static final double shannon(byte[] bytes) {
        int n = bytes.length;
        long[] values = new long[256];
        for(int i = 0; i < n; i++) {
            values[bytes[i] - Byte.MIN_VALUE]++;
        }
        double entropy = 0;
        double p;
        double log256 = Math.log(256);
        for(long count : values) {
            if(count != 0) {
                p = (double) count / n;
                entropy -= p * (Math.log(p) / log256);
            }
        }
        return entropy;
    }

    /**
     *
     * @param bytes
     * @return
     */
    public static final double shannonSequence(byte[] bytes) {
        byte[] diffs = new byte[bytes.length - 1];
        for(int i = 0, n = diffs.length; i < n; i++) {
            diffs[i] = (byte) (bytes[i + 1] - bytes[i]);
        }
        return shannon(diffs);
    }

}
