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

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {

	public static final long SECOND_IN_MILLIS = 1000L;
	public static final long MINUTE_IN_MILLIS = 60000L;
	public static final long HOUR_IN_MILLIS = 3600000L;
	public static final long DAY_IN_MILLIS = 86400000L;
	public static final long WEEK_IN_MILLIS = 604800000L;
	public static final long YEAR_IN_MILLIS = 31449600000L;

	public final static String FORMAT_Date = "dd/MM/yyyy";
	public final static String FORMAT_Time = "HH:mm";
	public final static String FORMAT_DateTime = "dd/MM/yyyy - HH:mm";
	public final static String FORMAT_TimeStamp = "dd/MM/yyyy - HH:mm:ss";
	public final static String FORMAT_UDC="yyyy-MM-dd'T'HH:mm:ss'Z'";
	
	public final static String FORMAT_FileName="yyyyMMdd-HHmm";
	public final static String FORMAT_SortableFileName="yyyyMMddHHmm";
	
	
	public static String getAsString(Date dt, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(dt);
	}
	
	public static String getAsString(long timestamp, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date(timestamp));
	}

	public static String getAsString(long timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat();
		return sdf.format(new Date(timestamp));
	}

	
	public static Date parseDate(String strDate) {
		Date res = null;
		
		try {
			res = new Date(strDate);
			if (res != null) return res;
		} catch (Exception e) {}
		
		String formats[] = { FORMAT_Date, FORMAT_DateTime, FORMAT_TimeStamp, FORMAT_UDC };
		
		for (int i=0; i<formats.length && res == null; i++) {
			try {
				SimpleDateFormat df = new SimpleDateFormat(formats[i]);
				res = df.parse(strDate);
			} catch (Exception e) {}
		}

		// Prova solo con la data escludendo l'ora
		if (res == null) {
			try {
				int tPos = strDate.indexOf("T");
				if (tPos != -1) {
					String s = strDate.substring(0, tPos);
					formats = new String[] {"yyyy-MM-dd", "dd-MM-yyyy", "dd/MM/yyyy" };
					for (int i=0; i<formats.length && res == null; i++) {
						SimpleDateFormat df = new SimpleDateFormat(formats[i]);
						res = df.parse(strDate);
					}
				}
			} catch (Exception e) {}
		}
		
		return res;
	}
}
