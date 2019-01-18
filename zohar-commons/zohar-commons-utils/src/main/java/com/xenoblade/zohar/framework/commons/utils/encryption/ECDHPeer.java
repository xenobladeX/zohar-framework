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
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;

import org.bouncycastle.jce.ECNamedCurveTable;

/**
 * <p><code>KeyAgreementPeer</code> implementation that uses the Elliptic Curve Diffie-Hellman key agreement protocol.</p>
 * @author xenoblade
 * @since 1.0.0
 */
public class ECDHPeer extends AbstractKeyAgreementPeer{

    private static final String ALGORITHM = "ECDH";

    /*
     * Attributes
     */

    private AlgorithmParameterSpec spec;

    /*
     * Constructor(s)
     */

    /**
     * <p>Constructs a <code>ECDHPeer</code> instance using a named elliptic curve <code>curve</code>.</p>
     * <p><b>Note:</b> This constructor will use Bouncy Castle (BC) as its provider.</p>
     * @param curve
     * @throws GeneralSecurityException
     */
    public ECDHPeer(String curve) throws GeneralSecurityException {
        this(curve, null);
    }

    /**
     * <p>Constructs a <code>ECDHPeer</code> instance using a named elliptic curve <code>curve</code>.</p>
     * <p><b>Note:</b> This constructor will use Bouncy Castle (BC) as its provider.</p>
     * @param curve
     * @param keyPair
     * @throws GeneralSecurityException
     */
    public ECDHPeer(String curve, KeyPair keyPair) throws GeneralSecurityException {
        this(ECNamedCurveTable.getParameterSpec(curve), keyPair, "BC");
    }

    /**
     * <p>Constructs a <code>ECDHPeer</code> instance using a custom elliptic curve.</p>
     * @param p
     * @param a
     * @param b
     * @param xg
     * @param yg
     * @param n
     * @param h
     * @throws GeneralSecurityException
     */
    public ECDHPeer(BigInteger p, BigInteger a, BigInteger b, BigInteger xg, BigInteger yg, BigInteger n, int h) throws GeneralSecurityException {
        this(new ECParameterSpec(new EllipticCurve(new ECFieldFp(p), a, b), new ECPoint(xg, yg), n, h), null);
    }

    /**
     * <p>Constructs a <code>ECDHPeer</code> instance using a custom elliptic curve.</p>
     * @param p
     * @param a
     * @param b
     * @param xg
     * @param yg
     * @param n
     * @param h
     * @param keyPair
     * @throws GeneralSecurityException
     */
    public ECDHPeer(BigInteger p, BigInteger a, BigInteger b, BigInteger xg, BigInteger yg, BigInteger n, int h, KeyPair keyPair) throws GeneralSecurityException {
        this(p, a, b, xg, yg, n, h, keyPair, null);
    }

    /**
     * <p>Constructs a <code>ECDHPeer</code> instance using a custom elliptic curve.</p>
     * @param p
     * @param a
     * @param b
     * @param xg
     * @param yg
     * @param n
     * @param h
     * @param keyPair
     * @param provider
     * @throws GeneralSecurityException
     */
    public ECDHPeer(BigInteger p, BigInteger a, BigInteger b, BigInteger xg, BigInteger yg, BigInteger n, int h, KeyPair keyPair, String provider) throws GeneralSecurityException {
        this(new ECParameterSpec(new EllipticCurve(new ECFieldFp(p), a, b), new ECPoint(xg, yg), n, h), keyPair, provider);
    }

    /**
     * <p>Constructs a <code>ECDHPeer</code> instance using an elliptic curve <code>AlgorithmParameterSpec</code>.</p>
     * @param spec
     * @throws GeneralSecurityException
     */
    public ECDHPeer(AlgorithmParameterSpec spec) throws GeneralSecurityException {
        this(spec, null);
    }

    /**
     * <p>Constructs a <code>ECDHPeer</code> instance using an elliptic curve <code>AlgorithmParameterSpec</code>.</p>
     * @param spec
     * @param keyPair
     * @throws GeneralSecurityException
     */
    public ECDHPeer(AlgorithmParameterSpec spec, KeyPair keyPair) throws GeneralSecurityException {
        this(spec, keyPair, null);
    }

    /**
     * <p>Constructs a <code>ECDHPeer</code> instance using an elliptic curve <code>AlgorithmParameterSpec</code> and a custom provider.</p>
     * @param spec
     * @param keyPair
     * @param provider
     * @throws GeneralSecurityException
     */
    public ECDHPeer(AlgorithmParameterSpec spec, KeyPair keyPair, String provider) throws GeneralSecurityException {
        super(provider != null ? KeyAgreement.getInstance(ALGORITHM, provider) : KeyAgreement.getInstance(ALGORITHM), keyPair);
        this.spec = spec;
        initialize();
    }

    /*
     * Class methods
     */

    /**
     * <p>Returns the inner <code>AlgorithmParameterSpec</code>.</p>
     * @return
     */
    public AlgorithmParameterSpec getAlgorithmParameterSpec() {
        return spec;
    }

    /*
     * Concrete methods
     */

    @Override
    protected KeyPair createKeyPair() throws GeneralSecurityException {
        return createKeyPair(spec, getKeyAgreement().getProvider());
    }

    /*
     * Static methods
     */

    /**
     * <p>Creates and returns an ECDH key pair for the given parameter specification.</p>
     * @param spec
     * @return
     * @throws GeneralSecurityException
     */
    public static final KeyPair createKeyPair(AlgorithmParameterSpec spec) throws GeneralSecurityException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyGen.initialize(spec, new SecureRandom());
        return keyGen.generateKeyPair();
    }

    /**
     * <p>Creates and returns an ECDH key pair for the given parameter specification and provider.</p>
     * @param spec
     * @param provider
     * @return
     * @throws GeneralSecurityException
     */
    public static final KeyPair createKeyPair(AlgorithmParameterSpec spec, String provider) throws GeneralSecurityException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM, provider);
        keyGen.initialize(spec, new SecureRandom());
        return keyGen.generateKeyPair();
    }

    /**
     * <p>Creates and returns an ECDH key pair for the given parameter specification and provider.</p>
     * @param spec
     * @param provider
     * @return
     * @throws GeneralSecurityException
     */
    public static final KeyPair createKeyPair(AlgorithmParameterSpec spec, Provider provider) throws GeneralSecurityException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM, provider);
        keyGen.initialize(spec, new SecureRandom());
        return keyGen.generateKeyPair();
    }

}
