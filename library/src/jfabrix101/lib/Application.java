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
package jfabrix101.lib;

import jfabrix101.lib.helper.InstallationHelper;
import jfabrix101.lib.util.Configuration;
import jfabrix101.lib.util.Logger;

/**
 * Helper class for application.
 * This class create some basic infrastructure for you application:
 * 
 * <ol>
 * <li> Load a <i>configuration file</i> (applicationConfig.prop) if 
 * exists from your assets forlder and itialialize a singleton properties
 * (See Configuration for more details)</li>
 * <li> Create (or relaod) an installation id. The installation Id is unique for
 * this application installed on a device (divverent devise will have different 
 * installationId. </li>
 * 
 * </ol>
 * <p>Usage: Extends or iclude this class in your manifest.xml
 * Example:
 * <pre><code>
 * &lt;application
        android:name=".Application"
        android:allowBackup="true"
        android:icon="@drawable/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/AppTheme" &gt;
        
   </code></pre>
   
   where "Application" is a class in your project that extands this class or this class
   if you don't needs of customizations.
   
   @see Configuration  
 */
public class Application extends android.app.Application {

	private Logger mLogger = Logger.getInstance(getClass());
	
	@Override
	public void onCreate() {
		mLogger.trace("Starting application - jfabrix-lib version : %s", Const.API_VERSION);
		
		mLogger.trace("Loading configurations ... ");
		Configuration.getInstance(this);
		
		String installationId = InstallationHelper.getInstallationId(this);
		mLogger.trace("Installation Id : %s", installationId);
		
		super.onCreate();
	}
}
