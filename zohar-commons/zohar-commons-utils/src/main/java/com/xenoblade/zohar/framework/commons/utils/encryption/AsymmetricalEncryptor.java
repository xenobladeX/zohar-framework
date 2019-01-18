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
package com.xenoblade.zohar.framework.commons.utils.encryption;

import java.security.GeneralSecurityException;

import com.xenoblade.zohar.framework.commons.utils.encryption.factory.SimpleKeyPair;

/**
 * Asymmetrical Encryptor
 * @author xenoblade
 * @since 1.0.0
 */
public class AsymmetricalEncryptor {

    private SimpleKeyPair keyPair;

    private String algorithm;

    private Encryptor publicEncryptor;

    private Encryptor privateEecryptor;

    public AsymmetricalEncryptor(SimpleKeyPair keyPair, String algorithm) {
        this.keyPair = keyPair;
        publicEncryptor = new Encryptor(keyPair.getPublicKey(), algorithm);
        privateEecryptor = new Encryptor(keyPair.getPribateKey(), algorithm);
    }

    public byte[] decryptPublic(byte[] message) throws GeneralSecurityException {
        return publicEncryptor.decrypt(message);
    }

    public byte[] decryptPublic(byte[] message, int maxBlockLength) throws GeneralSecurityException {
        return publicEncryptor.decrypt(message, maxBlockLength);
    }

    public byte[] decryptPrivate(byte[] message) throws GeneralSecurityException {
        return privateEecryptor.decrypt(message);
    }

    public byte[] decryptPrivate(byte[] message, int maxBlockLength) throws GeneralSecurityException {
        return privateEecryptor.decrypt(message, maxBlockLength);
    }

    public byte[] encryptPublic(byte[] message) throws GeneralSecurityException {
        return publicEncryptor.encrypt(message);
    }

    public byte[] encryptPublic(byte[] message, int maxBlockLength) throws GeneralSecurityException {
        return publicEncryptor.encrypt(message, maxBlockLength);
    }

    public byte[] encryptPrivate(byte[] message) throws GeneralSecurityException {
        return privateEecryptor.encrypt(message);
    }

    public byte[] encryptPrivate(byte[] message, int maxBlockLength) throws GeneralSecurityException {
        return privateEecryptor.encrypt(message, maxBlockLength);
    }


}
