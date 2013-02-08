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

import java.util.List;

/**
 * 
 * Helper class for debug
 */
public class DebugHelper {

	/**
	 * Restituisce una lista di valori in formto stringa
	 * @param data Lista da visualizzare
	 * @return Stringa con i valori
	 */
	public static String dumpList(List<?> data) {
		StringBuilder sb = new StringBuilder("{");
		for (Object l : data) {
			if (l == null) sb.append("null, ");
			else sb.append(l.toString() + ", ");
		}
		sb.append("}");
		return sb.toString();
	}
	
	public static String dumpArray(Object[] data) {
		StringBuilder sb = new StringBuilder("{");
		for (Object l : data) {
			if (l == null) sb.append("null, ");
			else sb.append(l.toString() + ", ");
		}
		sb.append("}");
		return sb.toString();
	}
	
	public static String dumpLongArray(long[] data) {
		StringBuilder sb = new StringBuilder("{");
		for (long l : data) {
			sb.append(l + ", ");
		}
		sb.append("}");
		return sb.toString();
	}
}
