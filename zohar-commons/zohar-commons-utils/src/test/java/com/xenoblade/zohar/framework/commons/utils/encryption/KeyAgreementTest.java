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

import org.junit.Test;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.Security;

import static org.junit.Assert.assertArrayEquals;

/**
 * <p>Unit test that tests key agreement algorithms such as Diffie-Hellman and Elliptic Curve Diffie-Hellman.</p>
 * <p><b>Note:</b> This unit test does not contain MQV and ECMQV test methods because these algorithms are considered insecure and thus obsolete.</p>
 *
 * @see <a href="https://en.wikipedia.org/wiki/Diffie%E2%80%93Hellman_key_exchange">https://en.wikipedia.org/wiki/Diffie%E2%80%93Hellman_key_exchange</>
 * @see <a href="https://en.wikipedia.org/wiki/Elliptic_curve_Diffie%E2%80%93Hellman">https://en.wikipedia.org/wiki/Elliptic_curve_Diffie%E2%80%93Hellman</a>
 * @see <a href="https://en.wikipedia.org/wiki/MQV">https://en.wikipedia.org/wiki/MQV</a>
 * @author xenoblade
 * @since 1.0.0
 */
public class KeyAgreementTest {

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    /**
     * <p>Tests Diffie-Hellman key exchange.</p>
     * <p>Use at least a <code>p</code> of 2048 bits. Better pre-determined values for <code>p</code> can be found at the link below.</p>
     * @see <a href="https://tools.ietf.org/html/rfc3526"></a>
     * @throws GeneralSecurityException
     */
    @Test public void testDH() throws GeneralSecurityException {

        // Create primes p & g
        // Tip: You don't need to regenerate p; Use a fixed value in your application
        int bits = 2048;
        BigInteger p = BigInteger.probablePrime(bits, new SecureRandom());
        BigInteger g = new BigInteger("2");

        // Create two peers
        AbstractKeyAgreementPeer peerA = new DHPeer(p, g);
        AbstractKeyAgreementPeer peerB = new DHPeer(p, g);

        // Exchange public keys and compute shared secret
        byte[] sharedSecretA = peerA.computeSharedSecret(peerB.getPublicKey());
        byte[] sharedSecretB = peerB.computeSharedSecret(peerA.getPublicKey());

        assertArrayEquals(sharedSecretA, sharedSecretB);
    }

    @Test public void testECDH() throws GeneralSecurityException {

        String algorithm = "brainpoolp512r1";

        // Create two peers
        AbstractKeyAgreementPeer peerA = new ECDHPeer(algorithm);
        AbstractKeyAgreementPeer peerB = new ECDHPeer(algorithm);

        // Exchange public keys and compute shared secret
        byte[] sharedSecretA = peerA.computeSharedSecret(peerB.getPublicKey());
        byte[] sharedSecretB = peerB.computeSharedSecret(peerA.getPublicKey());

        assertArrayEquals(sharedSecretA, sharedSecretB);
    }
    
}
