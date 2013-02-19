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

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Implementazione di base di un asyncTask.
 * Le sottoclassi devono implementate il metodo astratto per la logica del task.
 * 
 * <p>Se il task ha bisogno di parametri, si deve utilizzare il metodo
 * <code>addTaskParameter</code> per impostare una mappa di parametri.
 * L'implementazione del task può accedere ai parametri attraverso il metodo <code>getTaskParameter</code>
 *
 * <p>E' compito della classe implementatrice decidere ogni quando inviare messaggi nella progressDialog
 * attraverso il comando <code>publishProgress</code> e quando inviare un messaggio al chiamante attraverso 
 * il metodo <code>handleMessage</code>. Deve inoltre definire anche le costanti dei messaggi
 * 
 * <P>L'Handler ricevera' (nei parametri Data) due valori 
 * - msgCode : Intero con identificativo del messaggio
 * - msgValue : Stringa con la definizione del messaggio
 * 
 * Un Esempio di implementazione dell'Handler del messaggio e' questo
 * <code>
 * 
 *      public void handleMessage(android.os.Message msg) {
			int msgCode = msg.getData().getInt("msgCode");
			String msgValue = msg.getData().getString("msgValue");
		};
	
 * </code>
 * 
 * che la classe chiamante deve fornire
 * 
 * E' possibile impostare un titolo alla finestra attraverso il parametro
 * <code>setTitle</code> ma è importante impostare il parametro
 * prima della execute.
 * Attraverso il metodo <code>setIcon</code> e' possibile indicare l'icona da utilizzare
 * al posto di quella di default
 * 
 * @author 	14/09/2010 - Fabrizio Russo - www.fabrizio-russo.it
 * 			25/05/2012 - Aggiornamento struttura
 *
 */
public abstract class GenericAsyncTask extends AsyncTask<Void, String, Boolean> {

	private Context context;
	private ProgressDialog progressDialog = null;
	
	private int iconRef = android.R.drawable.ic_dialog_info;
	private String title = null;
	private Handler myHandler = null;
	
	private Map<String, Object> taskParameters = null;
	
	
	public static String RESULT_CODE = "msgCode";
	
	public GenericAsyncTask(Context context, Handler handler) {
		this.context = context;
		this.myHandler = handler;
	}

	protected Context getContext() { return context; }
	
	@Override
	protected void onPreExecute() {
		
		progressDialog = new ProgressDialog(context);
		
		if (title == null) title = "Info";
		progressDialog.setTitle(title);
		progressDialog.setIndeterminate(true);
		progressDialog.setIcon(iconRef);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.show(); 
		
	}
	
	public void setIcon(int iconRef) { this.iconRef = iconRef; }
	public void setTitle(String title) { this.title = title; }
	
	@Override
	protected Boolean doInBackground(Void... params) {
		doTaskInBackground();
		return true;
	}
	
	/**
	 * Implementare la logica del task
	 */
	public abstract void doTaskInBackground();
	
	
	@Override
	protected void onPostExecute(Boolean result) {
		progressDialog.cancel(); 
		super.onPostExecute(result);
	}
	

	protected void handleMessageCode(int code, String value) {
		if (myHandler == null) return;
		
		Message msg = myHandler.obtainMessage();
		Bundle b = new Bundle();
		b.putInt("msgCode", code);
		b.putString("msgValue", value);
		msg.setData(b);
		myHandler.sendMessage(msg);
	}


	protected void handleMessageCode(int code, Bundle b) {
		if (myHandler == null) return;
		
		Message msg = myHandler.obtainMessage();
		b.putInt("msgCode", code);
		msg.setData(b);
		myHandler.sendMessage(msg);
	}
	
	
	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
		progressDialog.setMessage(values[0]);
	}
	
	public void setTaskParameters(Map<String, Object> taskParameters) {
		this.taskParameters = taskParameters;
	}
	
	
	public void addTaskParameter(String key, Object value) {
		if (taskParameters == null) taskParameters = new HashMap<String, Object>();
		taskParameters.put(key, value);
	}
	
	
	protected Object getTaskParameter(String key) {
		if (taskParameters == null) return null;
		else return taskParameters.get(key);
	}
	
}
