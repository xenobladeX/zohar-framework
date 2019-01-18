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
import javax.crypto.spec.DHParameterSpec;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Provider;
import java.security.SecureRandom;

/**
 * <p><code>KeyAgreementPeer</code> implementation that uses the Diffie-Hellman key agreement protocol.</p>
 * @author xenoblade
 * @since 1.0.0
 */
public class DHPeer extends AbstractKeyAgreementPeer{

    private static final String ALGORITHM = "DH";

    /*
     * Attributes
     */

    private BigInteger p;
    private BigInteger g;

    /*
     * Constructor(s)
     */

    /**
     * <p>Constructs a <code>DHPeer</code> instance using primes <code>p</code> and <code>g</code>.</p>
     * <p><b>Note:</b> Use {@link BigInteger#probablePrime(int, java.util.Random)} to create good <code>p</code> and <code>g</code> candidates.</p>
     * @param p
     * @param g
     * @throws GeneralSecurityException
     */
    public DHPeer(BigInteger p, BigInteger g) throws GeneralSecurityException {
        this(p, g, null);
    }

    /**
     * <p>Constructs a <code>DHPeer</code> instance using primes <code>p</code> and <code>g</code>.</p>
     * <p><b>Note:</b> Use {@link BigInteger#probablePrime(int, java.util.Random)} to create good <code>p</code> and <code>g</code> candidates.</p>
     * @param p
     * @param g
     * @param keyPair
     * @throws GeneralSecurityException
     */
    public DHPeer(BigInteger p, BigInteger g, KeyPair keyPair) throws GeneralSecurityException {
        this(p, g, keyPair, null);
    }

    /**
     * <p>Constructs a <code>DHPeer</code> instance using primes <code>p</code> and <code>g</code>.</p>
     * <p><b>Note:</b> Use {@link BigInteger#probablePrime(int, java.util.Random)} to create good <code>p</code> and <code>g</code> candidates.</p>
     * @param p
     * @param g
     * @param keyPair
     * @throws GeneralSecurityException
     */
    public DHPeer(BigInteger p, BigInteger g, KeyPair keyPair, String provider) throws GeneralSecurityException {
        super(provider != null ? KeyAgreement.getInstance(ALGORITHM, provider) : KeyAgreement.getInstance(ALGORITHM), keyPair);
        this.p = p;
        this.g = g;
        initialize();
    }

    /*
     * Class methods
     */

    /**
     * <p>Returns prime <code>p</code>.</p>
     * @return
     */
    public BigInteger getP() {
        return p;
    }

    /**
     * <p>Returns prime <code>g</code>.</p>
     * @return
     */
    public BigInteger getG() {
        return g;
    }

    /*
     * Concrete methods
     */

    @Override
    protected KeyPair createKeyPair() throws GeneralSecurityException {
        return createKeyPair(p, g, getKeyAgreement().getProvider());
    }

    /*
     * Static methods
     */

    /**
     * <p>Creates and returns a DH key pair for the given <code>p</code> and <code>g</code>.</p>
     * @param p
     * @param g
     * @return
     * @throws GeneralSecurityException
     */
    public static final KeyPair createKeyPair(BigInteger p, BigInteger g) throws GeneralSecurityException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyGen.initialize(new DHParameterSpec(p, g), new SecureRandom());
        return keyGen.generateKeyPair();
    }

    /**
     * <p>Creates and returns a DH key pair for the given <code>p</code>, <code>g</code> and provider.</p>
     * @param p
     * @param g
     * @param provider
     * @return
     * @throws GeneralSecurityException
     */
    public static final KeyPair createKeyPair(BigInteger p, BigInteger g, Provider provider) throws GeneralSecurityException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM, provider);
        keyGen.initialize(new DHParameterSpec(p, g), new SecureRandom());
        return keyGen.generateKeyPair();
    }

}
