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
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.View;

/**
 * Dialog window to create a dialog with a custom view.
 *
 * After pressed the OK button, the result will be returned 
 * inside a bundle under the key 'DIALOG_RESULT_VALUE'
 */
public class CustomViewDialogFragment extends DialogFragment 
implements DialogInterface.OnClickListener {
	
	View customView = null;
	DialogResultListener resultListener = null;
	
	public static CustomViewDialogFragment newInstance(
			String title, String message, View view, 
			DialogResultListener dialogResultListener) {
		
		CustomViewDialogFragment adf = new CustomViewDialogFragment();
		adf.resultListener = dialogResultListener;
		adf.customView = view;
		Bundle bundle = new Bundle();
		bundle.putString("message", message);
		bundle.putString("title", title);
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
		b.setView(customView);
		return b.create();
	}

	public void onClick(DialogInterface dialog, int which) {
		if (which == AlertDialog.BUTTON_NEGATIVE) return;
		Bundle b = getArguments();
		resultListener.onDialogDone(getTag(), b);
	}
}
