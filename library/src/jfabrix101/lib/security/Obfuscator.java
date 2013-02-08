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

public interface Obfuscator {

	/**
	 * Ofuscate a string
	 * 
	 * @param original
	 * @return
	 */
	public String obfuscate(String original); 
		
	/**
	 * Unobfuscate a string.
	 * 
	 * @param obfuscated
	 * @return
	 * @throws SecurityValidationException
	 */
	public String unobfuscate(String obfuscated) throws SecurityValidationException;
	 
}
