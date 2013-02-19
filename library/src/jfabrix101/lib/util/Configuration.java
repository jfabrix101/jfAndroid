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

import java.util.Properties;

import android.content.Context;
import android.util.Log;

/**
 * Singleton used to manage the configuration
 * 
 * You must define a properties (called "applicationConfig.prop")
 * in your ASSETS folder.
 */
public class Configuration {

	private static Configuration instance = null;
	private static String CONFIG_FILE_NAME = "applicationConfig.prop";
	private static String LOG_TAG = "jfabrix101-Configuration";
	
	private Properties conf = null;
	
	
	public static Configuration getInstance(Context context) {
		if (instance != null) return instance;
		instance = new Configuration();
		instance.conf = new Properties();
		try {
			java.io.InputStream in = context.getAssets().open(CONFIG_FILE_NAME);
			instance.conf.load(in);
			in.close();
			Log.d(LOG_TAG, "Loaded application-config file. (" + CONFIG_FILE_NAME + ")");
		} catch (Exception e) {
			Log.w(LOG_TAG, "Error loading configuration file : " + e.getMessage());
		}
		return instance;
	}
	
	public boolean isDevelopMode() { return getPropertyAsBoolean("developMode"); }
	public boolean isTraceMode() { return getPropertyAsBoolean("traceMode"); }
	public String getGoogleAnalyticsCode() { return getProperty("googleAnalyticsCode"); }
	
		
	public String getProperty(final String key) {
		if (conf == null) return null;
		return conf.getProperty(key); 
	}
	
	public String getProperty(final String key, final String defaultValue) {
		if (conf == null) return null;
		String val = conf.getProperty(key);
		if (val == null) return defaultValue;
		else return val;
	}
	
	public boolean getPropertyAsBoolean(final String key) {
		return Boolean.parseBoolean(getProperty(key, "false"));
	}
	
	public int getPropertyAsInteger(final String key) {
		return Integer.parseInt(getProperty(key, "-1"));
	}
}
