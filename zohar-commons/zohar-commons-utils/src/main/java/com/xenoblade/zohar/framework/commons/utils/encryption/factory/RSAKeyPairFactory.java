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

import lombok.SneakyThrows;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSAKeyPairFactory
 * @author xenoblade
 * @since 1.0.0
 */
public class RSAKeyPairFactory implements KeyPairFactory{

    public static final String ALGORITHM = "RSA";
    public static final int MAXIMUM_KEY_LENGTH = 1024;

    @SneakyThrows
    @Override public SimpleKeyPair keyPairFromPassword(byte[] publicPassword, byte[] privatePassword) {
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicPassword);
        java.security.KeyFactory publicKeyFactory = KeyFactory.getInstance(ALGORITHM);
        Key publicK = publicKeyFactory.generatePublic(x509KeySpec);

        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privatePassword);
        java.security.KeyFactory privateKeyFactory = KeyFactory.getInstance(ALGORITHM);
        Key privateK = privateKeyFactory.generatePrivate(pkcs8KeySpec);

        return new SimpleKeyPair(publicK, privateK);
    }

    @Override public SimpleKeyPair randomKeyPair() {
        return randomKeyPair(MAXIMUM_KEY_LENGTH);
    }

    @SneakyThrows
    @Override public SimpleKeyPair randomKeyPair(int size) {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(size);
        java.security.KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return new SimpleKeyPair(keyPair.getPublic(), keyPair.getPrivate());

    }
}
