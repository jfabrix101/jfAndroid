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
package jfabrix101.lib.view;


import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

/*
 * DatePicker per la visualizzaione del mese e dell'anno
 */
public class DatePickerView extends LinearLayout {

	
	private Spinner monthSpinner;
	private EditText yearEditText;
		
	public DatePickerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		setOrientation(HORIZONTAL);
		
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		monthSpinner = new Spinner(context);
		monthSpinner.setLayoutParams(lp);
		monthSpinner.setAdapter(getMonthAdapter(context));
		
		yearEditText = new EditText(context);
		yearEditText.setLayoutParams(lp);
		yearEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
		setDate(new Date());
		
		addView(monthSpinner);
		addView(yearEditText);
		
		
		
	}
	
	
	public void setYear(int year) {
		yearEditText.setText("" + year);
	}
	
	public void setMonth(int month) {
		if (month > 11 || month < 0) return;
		monthSpinner.setSelection(month);
	}
	
	public int getYear() {
		try {
			Integer i = Integer.parseInt(yearEditText.getText().toString());
			return i.intValue();
		} catch (Exception e) {}
		return -1; 
	}

	public String getMonth() {
		return monthSpinner.getSelectedItem().toString();
	}
	
	
	public void setDate(Date d) {
		Calendar c = new GregorianCalendar();
		c.setTime(d);
		setMonth(c.get(Calendar.MONTH));
		setYear(c.get(Calendar.YEAR));
	}
	
	// Get a list of months 
	private ArrayAdapter<String> getMonthAdapter(Context context) {
		String months[] = new DateFormatSymbols().getMonths();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, 
				android.R.layout.simple_spinner_item, months);
		return adapter;
	}
}
