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

import javax.crypto.KeyAgreement;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;

/**
 * <p>Abstract key agreement peer class.</p>
 * @author xenoblade
 * @since 1.0.0
 */
public abstract class AbstractKeyAgreementPeer {

    /*
     * Attributes
     */

    private KeyAgreement keyAgreement;
    private KeyPair keyPair;

    /*
     * Constructor(s)
     */

    /**
     * <p>Constructs a key agreement peer.</p>
     * @param keyAgreement
     * @throws GeneralSecurityException
     */
    public AbstractKeyAgreementPeer(KeyAgreement keyAgreement) throws GeneralSecurityException {
        this(keyAgreement, null);
    }

    /**
     * <p>Constructs a key agreement peer.</p>
     * @param keyAgreement
     * @param keyPair
     * @throws GeneralSecurityException
     */
    public AbstractKeyAgreementPeer(KeyAgreement keyAgreement, KeyPair keyPair) throws GeneralSecurityException {
        this.keyAgreement = keyAgreement;
        this.keyPair = keyPair;
    }

    /*
     * Abstract methods
     */

    /**
     * <p>Creates this peer's key pair if none has been provided upon construction.</p>
     * @return
     * @throws GeneralSecurityException
     */
    protected abstract KeyPair createKeyPair() throws GeneralSecurityException;

    /*
     * Class methods
     */

    /**
     * <p>Creates a keypair and initializes the key agreement.</p>
     * @throws GeneralSecurityException
     *
     */
    protected void initialize() throws GeneralSecurityException {
        if(keyPair == null) {
            keyPair = createKeyPair();
        }
        keyAgreement.init(keyPair.getPrivate());
    }

    /**
     * <p>Computes the shared secret using the other peer's public key.</p>
     * @param key
     * @return
     * @throws InvalidKeyException
     */
    public byte[] computeSharedSecret(Key key) throws InvalidKeyException {
        keyAgreement.doPhase(key, true);
        return keyAgreement.generateSecret();
    }

    /**
     * <p>Returns this peer's public key.</p>
     * @return
     */
    public Key getPublicKey() {
        return keyPair.getPublic();
    }

    /**
     * <p>Returns this peer's <code>KeyAgreement</code> instance.</p>
     * @return
     */
    public KeyAgreement getKeyAgreement() {
        return keyAgreement;
    }

}
