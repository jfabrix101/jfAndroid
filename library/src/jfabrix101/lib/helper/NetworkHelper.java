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


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jfabrix101.lib.security.ssl.SimpleSSLSocketFactory;
import jfabrix101.lib.util.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;

public class NetworkHelper {

	private static Logger mLogger = Logger.getInstance(NetworkHelper.class);
	
	/**
	 * Check the internet connection status.
	 * 
	 * @param context
	 * @return true if an internet connection is available
	 */
	public static boolean checkInternetConnection(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		boolean isOnLine = false;
		try {
			isOnLine = cm.getActiveNetworkInfo().isConnected();
		} catch (Exception ex) {
			isOnLine = false;
			Logger mLogger = Logger.getInstance(NetworkHelper.class);
			mLogger.error("Exception in checkInternetConnection() : %s", ex.getMessage());
			
		}
	    return isOnLine;
	}
	
	/**
	 * Check if the internet connection is through wifi.
	 * 
	 * @param context
	 * @return true if the connection is through wifi
	 */
	public static boolean isWiFiConnection(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		try {
			//mobile
			//State mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();

			//wifi
			State wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
			
//			if (mobile == State.CONNECTED || mobile == State.CONNECTING) {
//			    //mobile
//			} else if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
//			    //wifi
//			}
			
			if (wifi == State.CONNECTED || wifi == State.CONNECTING) return true;
			else return false;
			
		} catch (Exception ex) {
			Logger mLogger = Logger.getInstance(NetworkHelper.class);
			mLogger.error("Exception in checkInternetConnection() : %s", ex.getMessage());
		}
	
	    return false;
	}
	
	
	
	
	/**
	 * Download content from a URL in binary format.
	 *  
	 * @param url - URL from which to download 
	 * @return - The array of byte of content
	 * @throws Exception - Exception if something went wrong.
	 */
	public static byte[] getBinaryData(String url) throws Exception {
		return getBinaryData(url, Long.MAX_VALUE);
	}
	
	
	/**
	 * Download content from a URL in binary format with a max size.
	 *  
	 * @param url - URL from which to download 
	 * @return - The array of byte of content
	 * @throws Exception - Exception if something went wrong or the content-lngth is too long
	 */
	public static byte[] getBinaryData(String url, long maxSizeLength) throws Exception {
		HttpClient client = new DefaultHttpClient();
		HttpGet getHttp = new HttpGet(url);
		
		HttpResponse response = client.execute(getHttp);
		int returnCode = response.getStatusLine().getStatusCode();
		long length = response.getEntity().getContentLength();
		if (length > maxSizeLength) throw new RuntimeException("contentLength too big !!");
		
		byte[] result = EntityUtils.toByteArray(response.getEntity());
		
		if(returnCode != HttpStatus.SC_OK) {
			mLogger.error("UpdateServiceHelper() - Network error reading URL %s",url);
			mLogger.error("UpdateServiceHelper() - Network error: HttpStatusCode : %s", response.getStatusLine().getStatusCode());
			return null;
		}
		return result;
	}
	
	/**
	 * First step for an execution of an http request.
	 * Create an HttpClient using http or https.
	 * @return
	 */
	private static HttpClient getHttpClient() {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", new SimpleSSLSocketFactory(), 443));

		HttpParams params = new BasicHttpParams();
		params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 30);
		params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(30));
		params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);

		ClientConnectionManager cm = new SingleClientConnManager(params, schemeRegistry);
		HttpClient httpClient = new DefaultHttpClient(cm, params);
		return httpClient;
	}
	
	/**
	 * Make an http request using the GET protocol
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static String getUrl(String url) throws Exception {
		HttpClient httpClient = getHttpClient();
		HttpGet httpRequest = new HttpGet(url);
		return execHttpRequest(httpClient, httpRequest);
	}
	
	/**
	 * Make an http request using the POST protocol
	 * @param url 
	 * @param params - Maps with parameters 
	 * @return
	 * @throws Exception
	 */
	public static String postUrl(String url, Map<String, String> params) throws Exception {
		HttpClient httpClient = getHttpClient();
		HttpPost httpRequest = new HttpPost(url);
		
		// Add post data
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(params.size());
        for (Entry<String, String> entry : params.entrySet()) {
        	String key = entry.getKey();
        	String val = entry.getValue();
        	nameValuePairs.add(new BasicNameValuePair(key, val));
        }
        httpRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		return execHttpRequest(httpClient, httpRequest);
	}
	
	
	/**
	 * Last part of execution an http request.
	 * Execute a request and return the body. 
	 * @param httpClient
	 * @param httpRequest
	 * @return
	 * @throws Exception
	 */
	private static String execHttpRequest(HttpClient httpClient, HttpRequestBase httpRequest)
	throws Exception {
		HttpResponse response = httpClient.execute(httpRequest);
		int returnCode = response.getStatusLine().getStatusCode();
		
		String htmlBody = EntityUtils.toString(response.getEntity());
		
		if(returnCode != HttpStatus.SC_OK) {
			mLogger.error( "- Network error reading URL: " + httpRequest.getURI().toString());
			mLogger.error( "- Network error: HttpStatusCode : " + response.getStatusLine().getStatusCode());
			return null;
		}
		return htmlBody;
	}
}
