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

import jfabrix101.lib.util.CallbackHandler;
import jfabrix101.lib.util.Logger;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;

public class ProgressBarHandler {

	private static ProgressDialog pDialog = null;
	public static final int PDIALOG_DISMISS = 1000;
	public static final int PDIALOG_CHANGEVALUE = 1001;

	private static boolean continueThread = true;
	
	private static Handler progressBarHandler = new Handler() {
		
		public void handleMessage(Message msg) {
			if (pDialog == null) return;
			int code = msg.getData().getInt("code", -1);
			String message = msg.getData().getString("message");
			int value = msg.getData().getInt("value", -1);
			switch (code) {
				case PDIALOG_DISMISS: {
					pDialog.dismiss();
					pDialog = null;
					break;
				}
				
				case PDIALOG_CHANGEVALUE: {
					pDialog.setMessage(message);
					if (value > 0) pDialog.setProgress(value);
					break;
				}
			}
			
		}
	};
			
	/*
	 * Interfaccia per l'esecuzione di un metodo su un elemento (della lista)
	 * Il metodo execute si occupa di eseguire la logica
	 * Il metodo getTitleForItem restituisce la stringa da mettere nella progress dialog
	 */
	public interface MethodExecutor {
		
		public Object execute(Object param);
		public String getTitleForItem(Object param);
	}
	
	/**
	 * Esegue un task su una lista di elementi, visualizzando una progress dialog
	 * che mostra l'avanzamento.
	 * 
	 * @param context
	 * @param dialogTitle - Titolo della finestra di dialogo
	 * @param items - Lista di elementi su cui applicare  la logica
	 * @param job - JOb da eseguire su ogni elemento della lista
	 * @param handler - Callback da richiamare al termine dell'esecuzione
	 */
	public static void executeInBackground(final Context context,
			final String dialogTitle, final int icon,
			final List<?> items, 
			final MethodExecutor job, final CallbackHandler handler) {
		
		continueThread = true;
		Thread th = new Thread(new Runnable() {
			public void run() {
				int i=0;
				for (Object item : items) {
					Logger mLogger = Logger.getInstance(ProgressBarHandler.class);
					mLogger.debug("ProgressBarHandler.executeInBackground: Executing job for item : %s", item);
					sendHandlerMessage(PDIALOG_CHANGEVALUE, job.getTitleForItem(item), ++i);
					if (continueThread) job.execute(item);
				}
				if (continueThread) { // Se non interrotto 
					sendHandlerMessage(PDIALOG_DISMISS, null, -1);
					if (handler != null) handler.handle(context, "Terminated");
				}
			}
		});
		
		pDialog = new ProgressDialog(context);
		pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pDialog.setMessage("");
		pDialog.setTitle(dialogTitle);
		if (icon > 0) pDialog.setIcon(icon);
		pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				sendHandlerMessage(PDIALOG_DISMISS, "", -1);
				if (handler != null) handler.handle(context, "Interrupted");
				continueThread = false; 
				Logger mLogger = Logger.getInstance(ProgressBarHandler.class);
				mLogger.debug("ProgressBarHandler.executeInBackground: Thread interrupted.");
			}
		});
		pDialog.setMax(items.size());
		pDialog.show();
		th.start();
	}
	
	
	/**
	 * Esegue della logica di business visualizzando una progress bar (indefinita)
	 * che mostra l'avanzamento lavori
	 * 
	 * @param context
	 * @param dialogTitle - Titolo della progress dialog
	 * @param job - job da eseguire
	 * @param handler - callback da richiamare al termine dell'esecuzione
	 */
	public static void executeInBackground(final Context context, 
			final String dialogTitle, 
			final Runnable job, final CallbackHandler handler) {
		
		
		Thread th = new Thread(new Runnable() {
			public void run() {
				job.run();  // Richiama sincronamente la logica
				sendHandlerMessage(PDIALOG_DISMISS, null, -1);
				if (handler != null) handler.handle(context, "Terminated");
			}
		});
		
		pDialog = new ProgressDialog(context);
		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pDialog.setMessage("");
		pDialog.setTitle(dialogTitle);
		pDialog.setCancelable(false);
		pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				pDialog.dismiss();
			}
		});
		pDialog.show();
		th.start();
	}
	
	private static void sendHandlerMessage(int code, String message, int value) {
		Message msg = progressBarHandler.obtainMessage();
		msg.getData().putInt("code", code);
		msg.getData().putString("message", message);
		msg.getData().putInt("value", value);
		progressBarHandler.sendMessage(msg);
	}
}
