/*
 * jfabrix101 - Library to simplify the developer life
 * 
 * Copyright (C) 2013 jfabrix101 (www.fabrizio-russo.it)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version
 * 2.1, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License 2.1 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License version 2.1 along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
*/
package jfabrix101.lib.security.impl;


import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import jfabrix101.lib.security.Base64;
import jfabrix101.lib.security.Obfuscator;
import jfabrix101.lib.security.SecurityHelper;
import jfabrix101.lib.security.SecurityValidationException;

import android.content.Context;

/**
 * Obfuscator basato sui dati del dispositivo.
 * La password è generata usando la firma del dispositivo
 */
public class DeviceDependentObfuscator implements Obfuscator {
	
    private static final String UTF8 = "UTF-8";
    private static final String KEYGEN_ALGORITHM = "PBEWITHSHAAND256BITAES-CBC-BC";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final byte[] IV = { 16, 74, 71, -80, 32, 101, -47, 72, 117, -14, 0, -29, 70, 65, -12, 74 };
    private static final String header = DeviceDependentObfuscator.class.getName() + "-1|";

    private Cipher mEncryptor;
    private Cipher mDecryptor;

    public static Obfuscator getInstance(Context c) {
    	return new DeviceDependentObfuscator(SecurityHelper.getAndroidId(c).getBytes(),
    			SecurityHelper.getDeviceSignature(c));
    }
    
    
    private DeviceDependentObfuscator(byte[] salt, String password) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(KEYGEN_ALGORITHM);
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 1024, 256);
            SecretKey tmp = factory.generateSecret(keySpec);
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
            mEncryptor = Cipher.getInstance(CIPHER_ALGORITHM);
            mEncryptor.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(IV));
            mDecryptor = Cipher.getInstance(CIPHER_ALGORITHM);
            mDecryptor.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(IV));
        } catch (GeneralSecurityException e) {
            // This can't happen on a compatible Android device.
            throw new RuntimeException("Invalid environment", e);
        }
    }

    public String obfuscate(String original) {
        if (original == null)  return null;
        
        try {
            return Base64.encodeBytes(mEncryptor.doFinal((header + original).getBytes(UTF8)));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Invalid environment", e);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Invalid environment", e);
        }
    }

    public String unobfuscate(String obfuscated) throws SecurityValidationException {
        if (obfuscated == null) return null;
        try {
            String result = new String(mDecryptor.doFinal(Base64.decode(obfuscated)));
            // Check for presence of header. This serves as a final integrity check, for cases
            // where the block size is correct during decryption.
            int headerIndex = result.indexOf(header);
            if (headerIndex != 0) {
                throw new SecurityValidationException("Header not found (invalid data or key)" + ":" +
                        obfuscated);
            }
            return result.substring(header.length(), result.length());
        } catch (Exception e) {
            throw new SecurityValidationException(e.getMessage() + ":" + obfuscated);
        } 
    }
    
}