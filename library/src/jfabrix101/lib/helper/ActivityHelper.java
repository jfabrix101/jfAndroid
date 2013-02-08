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

import jfabrix101.lib.dialog.DialogResultListener;
import jfabrix101.lib.util.CallbackHandler;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class ActivityHelper {

	/**
	 * Restituisce il tipo di screen su cui gira l'applicazione (small, medium, large, xlarge)
	 * @param context 
	 * @return Configuration.SCREENLAYOUT_SIZE_SMALL, Configuration.SCREENLAYOUT_SIZE_NORMAL,
	 * 		Configuration.SCREENLAYOUT_SIZE_LARGE, Configuration.SCREENLAYOUT_SIZE_XLARGE,
	 * 		Configuration.SCREENLAYOUT_SIZE_UNDEFINED	
	 */
	public static int getScreenLayoutType(Context context) {
		int screenLayout = ( context.getResources().getConfiguration().screenLayout & 
			    Configuration.SCREENLAYOUT_SIZE_MASK);
		return screenLayout;
		
	}
	
	
	public static void showDialogBox(Context context, String title, String msg, int icon) {
		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setTitle(title);
		ad.setPositiveButton(android.R.string.ok, null);
		if (icon <= 0) ad.setIcon(android.R.drawable.ic_dialog_alert);
		else ad.setIcon(icon);
		ad.setMessage(msg);
		ad.show();
	}
	
		
	public static void showConfirmDialogBox(Context context, String title, 
			String msg, int icon, final DialogResultListener confirmListener) {
		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setTitle(title);
		ad.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				confirmListener.onDialogDone("", null);
			}
		});
		if (icon <= 0) ad.setIcon(android.R.drawable.ic_dialog_info);
		else ad.setIcon(icon);
		ad.setMessage(msg);
		ad.show();
	}
	
	/**
	 * Avvia una nuova activity
	 * @param context
	 * @param activityClass
	 * @param params
	 */
	public static void startActivity(Context context, Class<? extends Activity> activityClass, Bundle params) {
		Intent intent = new Intent(context, activityClass);
		if (params != null) intent.putExtras(params);
		context.startActivity(intent);
	}
	
	
	public static boolean isLandscapeMode(Activity activity) {
		Display d = activity.getWindowManager().getDefaultDisplay();
		if (d.getWidth() > d.getHeight()) return true;
		else return false;
	}
	
	/**
	 * Invia una notifica.
	 * E' possibile indicare l'icona, il titolo ed il testo della notifica
	 * Se si indica anche una activity, alla selezione della notifica partira' l'activity indicata
	 * 
	 * Nota: Richiede il permesso : android.permission.VIBRATE
	 * 
	 * @param context
	 * @param icon - Id della risorsa per l'icona
	 * @param title - Titolo delle notifica
	 * @param text - Testo della notifica
	 * @param activityClass - Activity da lanciare alla selezione della notifica (null = nessuna azione)
	 */
	public static void sendNotification(Context context, int icon, String title, String text, Class<?> activityClass) {
		
		Notification notification = new Notification(icon, title + ": " + text, System.currentTimeMillis());
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		notification.defaults |= Notification.DEFAULT_SOUND;    //Suona
		
		notification.defaults |= Notification.DEFAULT_VIBRATE;  //Vibra
		notification.vibrate = new long[] {100, 100, 200, 200};
		
		//notification.defaults |= Notification.DEFAULT_LIGHTS;   //LED
		notification.ledARGB = 0xff00ff00;  // Green
		notification.ledOnMS = 300;
		notification.ledOffMS = 2000;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		
		Intent intent = new Intent(context, activityClass);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0,intent, PendingIntent.FLAG_CANCEL_CURRENT);
		notification.setLatestEventInfo(context, title, text, pIntent);
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		notificationManager.notify(1, notification);
		return;
	}
	
	/**
	 * Carica i dati di uno spinner con la lista passata
	 * @param context - Context
	 * @param spinner - Spinner da caricare
	 * @param data - List di stringhe con cuoi caricare lo spinner
	 * @param selectedPosition - Posizione di selezione (minimo zero)
	 */
	public static void setSpinnerData(Context context, Spinner spinner, List<String> data, int selectedPosition) {
		if (selectedPosition < 0) selectedPosition = 0;
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, data);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setSelection(selectedPosition);
	}

	/**
	 * Lancia la finestra per la selezione di un gestore per la condivisione di un messaggio
	 * (twitter, facebook, ....).
	 * 
	 * @param context
	 * @param dialogTitle - Titolo della finestra di dialog (Share...)
	 * @param subject - Subject (della email o titolo di twitter)
	 * @param text - Testo del messaggio
	 */
	public static void share(Context context, String dialogTitle, String subject, String text) {
		final Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, text);
		context.startActivity(Intent.createChooser(intent, dialogTitle));
	}
	
	/**
	 * Mostra un messaggio TOAST
	 */
	public static void showToast(Context context, int resourceId) {
		showToast(context, context.getString(resourceId));
	}
	
	/**
	 * Mostra un messaggio TOAST
	 */
	public static void showToast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * Restituisce le informazioni relative al package
	 * @param context
	 * @param thisClass
	 * @return
	 */
	public static PackageInfo getAppPackageInfo(Context context, Class<?> thisClass) {
		PackageInfo pinfo = null;
		try {
			ComponentName comp = new ComponentName(context, thisClass);
			pinfo = context.getPackageManager().getPackageInfo(comp.getPackageName(), 0);
		} catch (Exception e) {}
		return pinfo;
	}
	
	/**
	 * Restituisce il numero di versione dell'applicazione
	 * Se il numero di versione (versionName) termina con il carattere punto
	 * allora viene aggiunto anche il numero della build
	 */
	public static String getApplicationVersion(Context context, Class<?> thisClass) {
		PackageInfo pinfo = null;
		String version = null;
		try {
			ComponentName comp = new ComponentName(context, thisClass);
			pinfo = context.getPackageManager().getPackageInfo(comp.getPackageName(), 0);
			version = pinfo.versionName;
			if (version.endsWith(".")) version += pinfo.versionCode;
		} catch (Exception e) {}
		return version;
	}
	
	/**
     * Visualizza la dialog per la licenza se questa non è mai stata accettata.
     * 
     * @param context Contesto di riferiemento.
     * @param dialogTitle: Titolo nella finestra di dialogo
     * @param acceptButtonLabel : Label del pulsante di accettazione
     * @param rejectButtonLabel : Label per il pulsante di rifiuto
     * @param forceShow Forza la visualizzazione.
     * @return Restituisce <code>true</code> se l'utente ha già accettato l'EULA, <code>false</code> altrimenti.
     */
	private static final String PREFS_ABOUT_PREFERENCE_NAME = "defaultPreference";
    public static boolean showLicenceDialog(final Context context, 
    		String dialogTitle, String acceptButtonLabel, String rejectButtonLabel,
    		boolean forceShow, final CallbackHandler onOKHandler) {
        final SharedPreferences preferences = context.getSharedPreferences(PREFS_ABOUT_PREFERENCE_NAME, Context.MODE_PRIVATE);
        boolean isLicenseAccepted = preferences.getBoolean("licenseAccepted", false);
        if(!isLicenseAccepted || forceShow) {
	        	LinearLayout dialogView = new LinearLayout(context);
	        	dialogView.setOrientation(LinearLayout.VERTICAL);
	        	String data = DiskHelper.loadRawTextFile(context, "license");
	            WebView webView = new WebView(context);
	            webView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
	            webView.loadData(data, "text/html", "UTF-8");
	            dialogView.addView(webView);
                
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(false);
                builder.setTitle(dialogTitle);
                builder.setView(dialogView);
                builder.setPositiveButton(acceptButtonLabel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                                preferences.edit().putBoolean("licenseAccepted", true).commit();
                                if(onOKHandler != null) onOKHandler.handle(context, "licenseAccepted");
                        }
                });
                builder.setNegativeButton(rejectButtonLabel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                                preferences.edit().putBoolean("licenseAccepted", false).commit();
                                if(context instanceof Activity) ((Activity)context).finish();
                        }
                });
                builder.create().show();
                return false;
        }
        else return true;
    }
    
    /**
     * Restituisce il nome dell'appicazione
     * @param context
     * @return
     */
    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        return context.getString(applicationInfo.labelRes);
    }
    
    /**
     * Mostra una finestra di dialogo con il changelog dell'applicazione
     * @param context- context
     * @param chengeLogTitle - titolo della finestra di dialogo
     * @param forceShow : True = viene visualizzata sempre, false=viene visualizzata solo se la prima volta
     * @param onOKHandler: Task da eseguire alla pressine de tasto Ok (default = null)
     * @return
     */
    public static boolean showChangelogDialog(final Context context,
    		String chengeLogTitle,
    		boolean forceShow, final CallbackHandler onOKHandler) {
        final SharedPreferences preferences = context.getSharedPreferences(PREFS_ABOUT_PREFERENCE_NAME, Context.MODE_PRIVATE);
        final PackageInfo packageInfo = getAppPackageInfo(context, ActivityHelper.class);
        int lastChanglog = preferences.getInt("lastChagelogVersion", -1);
        boolean showChangelog = (lastChanglog == packageInfo.versionCode);
        if(!showChangelog || forceShow) {
	        	LinearLayout dialogView = new LinearLayout(context);
	        	dialogView.setOrientation(LinearLayout.VERTICAL);
	        	String data = DiskHelper.loadRawTextFile(context, "changelog");
	            WebView webView = new WebView(context);
	            webView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
	            webView.loadData(data, "text/html", "UTF-8");
	            dialogView.addView(webView);
                
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(false);
                builder.setTitle(chengeLogTitle);
                builder.setView(dialogView);
                builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        preferences.edit().putInt("lastChagelogVersion", packageInfo.versionCode).commit();
                        if(onOKHandler != null) onOKHandler.handle(context, "changelog");
                    }
                });
                builder.create().show();
                return false;
        }
        else return true;
    }
    
    
    /**
     * Verifica se l'applicazione sta girando all'interno di un emulatore
     * @return true se sta girando all'interno dell'emulatore
     */
    public static boolean isAndroidEmulator() {
    	return Build.MODEL.contains("sdk");
    }
    
    /**
     * Verifica se un servizio sta girando
     * @param context
     * @param serviceClassName - Nome completo del servizio
     * @return
     */
    public static boolean isServiceRunning(Context context, String serviceClassName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClassName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    
}
