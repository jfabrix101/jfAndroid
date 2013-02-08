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


import jfabrix101.lib.security.impl.DeviceDependentObfuscator;
import jfabrix101.lib.security.impl.PasswordObfuscator;
import android.content.Context;

/**
 * Factory per la generazione e l'inizializzazione di un obfscator
 * 
 * @author jfabrix101
 *
 */
public class ObfuscatorFactory {

	public static Obfuscator getObfuscator(Context ctx, ObfuscatorTypeImp impType, String password) {
		
		Obfuscator res = null;
		
		if (impType == ObfuscatorTypeImp.PasswordBased) {
			res = new PasswordObfuscator(password);
		}
		
		if (impType == ObfuscatorTypeImp.DeviceBased) {
			res = DeviceDependentObfuscator.getInstance(ctx);
	    }
			
		
		return res;
	}
	
	public enum ObfuscatorTypeImp {
		PasswordBased, DeviceBased;
	}
}
