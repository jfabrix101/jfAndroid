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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import jfabrix101.lib.security.Base64;
import jfabrix101.lib.security.Obfuscator;
import jfabrix101.lib.security.SecurityValidationException;

import android.util.Log;

/**
 * Obfuscator basato su password definite dall'utente.
 * 
 * @author jfabrix101
 *
 */
public class PasswordObfuscator implements Obfuscator {

	private Cipher cryptCipher;
	private Cipher decryptCipher;
	
    private SecretKey secretKey;
    private IvParameterSpec ivParameterSpec;
    
    private static String TAG = "frCypher";
    
	private static String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static String CIPHER_ALGORITHM = "AES";
    private static String MESSAGEDIGEST_ALGORITHM = "MD5";
    
    // Replace me with a 16-byte key, share between Java and C#
    private static byte[] rawSecretKey = new byte[16];


    public void setRawSecretKey(String key) {
    	for (int i=0; i<16; i++) rawSecretKey[i] = 0x00;
    	if (key == null) return;  
    	
    	String xxx = key;
    	while (xxx.length() < 16) xxx += key;
    	xxx = xxx.substring(0, 16);
    	
    	rawSecretKey = xxx.getBytes();
    }
    
    
    public PasswordObfuscator(String passphrase) {
        byte[] passwordKey = encodeDigest(passphrase);

        secretKey = new SecretKeySpec(passwordKey, CIPHER_ALGORITHM);
        ivParameterSpec = new IvParameterSpec(rawSecretKey);
        
        try {
        	cryptCipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        	decryptCipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        	
        	cryptCipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        	decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        	
        } catch (Exception e) {
            Log.e(TAG, "Exception initializing Crypto ", e);
        }
        
    }

    @Override
    public String obfuscate(String original) {
    	byte[] xxx = encrypt(original.getBytes());
        return Base64.encodeBytes(xxx);
    }
    
    @Override
    public String unobfuscate(String obfuscated) throws SecurityValidationException {
    	byte[] xxx = Base64.decode(obfuscated.getBytes());
    	return decrypt(xxx);
    }
    
    
    private String decrypt(byte[] encryptedData) {
    	byte[] clearData;
        try {
        	clearData = decryptCipher.doFinal(encryptedData);
        } catch (Exception e) {
            Log.e(TAG, "Illegal block size", e);
            return null;
        } 
        return new String(clearData);
    }
    
    
    private byte[] encrypt(byte[] clearData) {
        byte[] encryptedData;
        try {
            encryptedData = cryptCipher.doFinal(clearData);
        } catch (Exception e) {
            Log.e(TAG, "Illegal block size", e);
            return null;
        } 
        return encryptedData;
    }
    
    
    
    
    private byte[] encodeDigest(String text) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(MESSAGEDIGEST_ALGORITHM);
            return digest.digest(text.getBytes());
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "No such algorithm " + MESSAGEDIGEST_ALGORITHM, e);
        }

        return null;
    }


}
