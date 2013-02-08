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
package jfabrix101.lib.helper;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StringHelper {

	public static boolean isEmpty(String s) {
		if (s == null) return true;
		if (s.trim().length() == 0) return true;
		return false;
	}
	
	/**
	 * Restituisce lo stackTrace di una eccezione
	 * @param e
	 * @return
	 */
	public static String toString(Exception e) {  
		StringWriter s = new StringWriter();  
		e.printStackTrace(new PrintWriter(s));  
		return s.toString();   
	}  
}
