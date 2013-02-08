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
package jfabrix101.lib.util;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public class Logger {

	private String logTag = "";
	private static Map<String, Logger> factoryMap = new HashMap<String, Logger>();
	private static boolean traceMode = true;
	private static boolean debugMode = true;
	
	private Logger() {};
	
	public static Logger getInstance(Class<?> cls) {
		String tag = cls.getName();
		Logger log = factoryMap.get(tag);
		if (log == null) {
			log = new Logger();
			log.logTag = tag;
			factoryMap.put(tag, log);
		}
		return log;
	}
	
	public void debug(String msg, Object ... params) {
		if (!debugMode) return;
		String s = msg;
		if (params != null && params.length > 0) s = String.format(msg, params);
		Log.d(logTag, s);
	}
	
	public void error(String msg, Object ... params) {
		String s = msg;
		if (params != null && params.length > 0) s = String.format(msg, params);
		Log.e(logTag, s);
	}
	
	public void warn(String msg, Object ... params) {
		String s = msg;
		if (params != null && params.length > 0) s = String.format(msg, params);
		Log.w(logTag, s);
	}
	
	public void info(String msg, Object ... params) {
		String s = msg;
		if (params != null && params.length > 0) s = String.format(msg, params);
		Log.i(logTag, s);
	}
	
	public static boolean isTraceMode() { return traceMode; }
	public static void setTraceMode(boolean traceMode) { Logger.traceMode = traceMode; }
	
	public void trace(String msg, Object ... params) {
		if (!traceMode) return;
		String s = msg;
		if (params != null && params.length > 0) s = String.format(msg, params);
		Log.i(logTag, "[TRACE] " + s);
	}
	
	public static boolean isDebugMode() { return debugMode;	}
	public static void setDebugMode(boolean debugMode) { Logger.debugMode = debugMode;	}
}

