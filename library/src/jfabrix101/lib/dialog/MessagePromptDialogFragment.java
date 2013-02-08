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
package jfabrix101.lib.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

/**
 * Finestra di dialogo per l'inserimento di un testo (campo di input)
 *
 * Il risultato dell'inserimento viene restituito all'interno del bundle di risposta
 * sotto la voce 'DIALOG_RESULT_VALUE'
 * 
 * Eventuali parametri aggiuntivi in input
 * PARAM_HINT : Tooltip all'interno della casella di testo
 * 
s */
public class MessagePromptDialogFragment extends DialogFragment 
implements DialogInterface.OnClickListener {
	
	EditText editText = null;
	DialogResultListener resultListener = null;
	
	public final static String DIALOG_RESULT_VALUE = "dialogResult";
	
	public final static String PARAM_hint = "_hint";
		
	public static MessagePromptDialogFragment newInstance(String title, 
			String message, Bundle extraParams, 
			DialogResultListener dialogResultListener) {
		
		MessagePromptDialogFragment adf = new MessagePromptDialogFragment();
		adf.resultListener = dialogResultListener;
		Bundle bundle = new Bundle();
		bundle.putString("message", message);
		bundle.putString("title", title);
		if (extraParams != null) bundle.putAll(extraParams);
		adf.setArguments(bundle);
		return adf;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setCancelable(true);
		int style = DialogFragment.STYLE_NORMAL, theme = 0;
		setStyle(style, theme);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
		b.setTitle(this.getArguments().getString("title"));
		b.setPositiveButton(getActivity().getString(android.R.string.ok), this);
		b.setNegativeButton(getActivity().getString(android.R.string.cancel), this);
		b.setMessage(this.getArguments().getString("message"));
		
		editText = new EditText(getActivity());
		String hits = getArguments().getString(PARAM_hint);
		if (hits != null) editText.setHint(hits);
		b.setView(editText);
		
		return b.create();
	}

	public void onClick(DialogInterface dialog, int which) {
		if (which == AlertDialog.BUTTON_NEGATIVE) return;
		Bundle b = getArguments();
		b.putString(DIALOG_RESULT_VALUE, editText.getText().toString());
		resultListener.onDialogDone(getTag(), b);
	}
}
