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
 * Dialog window to insert an input text.
 *
 * As result, the text inserted by user will be returned inside a bundle
 * under the key 'DIALOG_RESULT_VALUE'
 * 
 * Optional input paramters:
 * PARAM_HINT : Tooltip to be used inside a textfield
 * PARAM_INPUT_TYPE : Input type to be associated with the keyboard (see android.text.InputType)
 * 
s */
public class MessagePromptDialogFragment extends DialogFragment 
implements DialogInterface.OnClickListener {
	
	EditText editText = null;
	DialogResultListener resultListener = null;
	
	public final static String DIALOG_RESULT_VALUE = "dialogResult";
	
	public final static String PARAM_HINT = "_hint";
	public final static String PARAM_INPUT_TYPE = "_inputType";
		
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
		
		// Set the optional param HINT
		String hits = getArguments().getString(PARAM_HINT);
		if (hits != null) editText.setHint(hits);
		
		// Set the optional param INPUT_TYPE
		int inputType = getArguments().getInt(PARAM_INPUT_TYPE, -1);
		if (inputType > 0) editText.setInputType(inputType);
		
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
