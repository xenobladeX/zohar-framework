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

import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.junit.Assert;
import org.junit.Test;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

/**
 * Unit test for exporting/importing curve25519 public keys.

 * @author xenoblade
 * @since 1.0.0
 */
public class ECDHExportTest {


    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    @Test
    public void testExportImport() throws GeneralSecurityException {

        // Create a curve25519 parameter spec
        X9ECParameters params = CustomNamedCurves.getByName("curve25519");
        ECParameterSpec ecParameterSpec = new ECParameterSpec(params.getCurve(), params.getG(), params.getN(), params.getH(), params.getSeed());

        // Create public key
        AbstractKeyAgreementPeer peer = new ECDHPeer(ecParameterSpec, null, "BC");
        ECPublicKey ecPublicKey = (ECPublicKey) peer.getPublicKey();

        // Export public key
        byte[] encoded = ecPublicKey.getQ().getEncoded(true);

        System.out.println(Arrays.toString(encoded));
        System.out.println("Encoded length: " + encoded.length);

        // Import public key
        ECPublicKey importedECPublicKey = loadPublicKey(encoded);

        Assert.assertArrayEquals(ecPublicKey.getEncoded(), importedECPublicKey.getEncoded());
    }

    /**
     * Loads and returns the elliptic-curve public key from the data byte array.
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidKeySpecException
     */
    public static ECPublicKey loadPublicKey(byte[] data) throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidKeySpecException
    {
        X9ECParameters params = CustomNamedCurves.getByName("curve25519");
        ECParameterSpec ecParameterSpec = new ECParameterSpec(params.getCurve(), params.getG(), params.getN(), params.getH(), params.getSeed());

        ECPublicKeySpec publicKey = new ECPublicKeySpec(ecParameterSpec.getCurve().decodePoint(data), ecParameterSpec);
        KeyFactory kf = KeyFactory.getInstance("ECDH", "BC");
        return (ECPublicKey) kf.generatePublic(publicKey);
    }

}
