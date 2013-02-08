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
package jfabrix101.lib.security;


import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

public class SecurityHelper {

	/**
	 * Decodifica una string cifrata
	 * @param data - byte del dato cifrato
	 * @param secretKey - chiave di cifratura
	 * @return - Stringa decodificata
	 */
	public static String decode(final byte data[], byte[] secretKey) {

		byte data2[] = new byte[data.length];
		int pos = 0;
		for (int i = 0; i < data.length; i++) {
			data2[i] = (byte) (data[i] - secretKey[pos++]);
			if (pos >= secretKey.length)
				pos = 0;
		}
		return new String(data2);
	}
	
	
	/**
	 * Metodo di codifica - Commentanto per non farlo risultare nel codice compilato
	 * @param c
	 * @return
	 */
	/*
	public byte[] encode(String k, byte[] secretKey) {

		byte data[] = k.getBytes();
		int pos = 0;
		for (int i = 0; i < data.length; i++) {
			data[i] += secretKey[pos++];
			if (pos >= secretKey.length)
				pos = 0;
		}
		return data;

	}
	*/
	
	
	/**
	 * Restituisce l' androidID del dispositivo
	 */
	public static String getAndroidId(Context c) {
		if (c == null) return "";
		return Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ANDROID_ID);
	}
	
	
	/**
	 * Restituisce un codice univoco per un dato dispositivo.
	 * Utilizza i deti dell' androidID + i dati propri del dispositivo (Build.*)
	 * La stringa restituita è in formato MD5
	 * @param c
	 * @return
	 */
	public static String getUniqueDeviceId(Context c) {
        return md5sum(getDeviceSignature(c));
	}
	
	
	/**
	 * Restituisce la "firma" di un dispositivo. In particolare
	 * restituisce : AndroidID - Brand - Device - Version - Board - ID
	 */
	public static String getDeviceSignature(Context c) {
		StringBuffer b = new StringBuffer();
        String deviceId = getAndroidId(c);
        b.append(deviceId);
        b.append("-" + Build.BRAND);
        b.append("-" + Build.DEVICE);
        b.append("-" + Build.VERSION.RELEASE);
        b.append("-" + Build.BOARD);
        b.append("-" + Build.ID);
        return b.toString();
	}

	/**
	 * Restituisce il codice MD5 di una string
	 * @param s
	 * @return
	 */
	public static String md5sum(String s) {

		try {
			MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			md.update(s.getBytes());

			byte resultSum[] = md.digest();

			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < resultSum.length; i++) {
				String h = Integer.toHexString(0xFF & resultSum[i]);
				while (h.length() < 2) h = "0" + h;
				hexString.append(h);
			}

			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	
	/*
	 * Restituisce il token di firma (numerico) di una stringa
	 */
	public static String md5digest(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			return new BigInteger(1, md.digest(input.getBytes())).toString(16)
					.toUpperCase();
		} catch (Exception e) {
			return null;
		}
	}
	
	
}
