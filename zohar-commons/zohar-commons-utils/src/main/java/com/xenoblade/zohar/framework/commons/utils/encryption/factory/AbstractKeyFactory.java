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
package com.xenoblade.zohar.framework.commons.utils.encryption.factory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

/**
 * <p>Abstract <code>KeyFactory</code> implementation for creating secure keys.</p>
 * @author xenoblade
 * @since 1.0.0
 */
public abstract class AbstractKeyFactory implements KeyFactory {

    public static final byte[] DEFAULT_SALT = new byte[] { 73, 32, -12, -103, 88, 14, -44, 9, -119, -42, 5, -63, 102, -11, -104, 66, -17, 112, 55, 44, 18, -46, 30, -6, -55, 28, -54, 12, 39, 110, 63, 125 };
    public static final int DEFAULT_ITERATION_COUNT = 65536;

    /*
     * Attributes
     */

    private String algorithm;
    private byte[] salt;
    private int iterationCount;
    private int maximumKeyLength;

    /*
     * Constructor(s)
     */

    /**
     *
     * @param algorithm
     * @param maximumKeyLength
     */
    public AbstractKeyFactory(String algorithm, int maximumKeyLength) {
        this(algorithm, maximumKeyLength, DEFAULT_SALT, DEFAULT_ITERATION_COUNT);
    }

    /**
     *
     * @param algorithm
     * @param maximumKeyLength
     * @param salt
     * @param iterationCount
     */
    public AbstractKeyFactory(String algorithm, int maximumKeyLength, byte[] salt, int iterationCount) {
        this.algorithm = algorithm;
        this.maximumKeyLength = maximumKeyLength;
        this.salt = salt;
        this.iterationCount = iterationCount;
    }

    /*
     * Class methods
     */

    /**
     * <p>Sets the salt to be used by this factory.</p>
     * @param salt
     */
    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    /**
     * <p>Sets the amount of hashing iterations to be used by this factory.</p>
     * @param iterationCount
     */
    public void setIterationCount(int iterationCount) {
        this.iterationCount = iterationCount;
    }

    /*
     * Interface implementations
     */

    @Override
    public final Key keyFromPassword(String password) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            int keyLength = Math.min(Cipher.getMaxAllowedKeyLength(algorithm), maximumKeyLength);
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterationCount, keyLength);
            SecretKey tmp = factory.generateSecret(spec);
            return new SecretKeySpec(tmp.getEncoded(), algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public final Key randomKey() {
        try {
            int keyLength = Math.min(Cipher.getMaxAllowedKeyLength(algorithm), maximumKeyLength);
            return randomKey(keyLength);
        } catch(NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public final Key randomKey(int size) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
            keyGenerator.init(size);
            return keyGenerator.generateKey();
        } catch(NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
