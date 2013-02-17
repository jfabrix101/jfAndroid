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
package jfabrix101.lib.fragmentActivity;

import java.util.List;

import jfabrix101.lib.util.Logger;
import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Implementazione helper per la gestione di un fragmment con una ListView.
 * Il tipo A è il tipo del modello gestito dall'activity.
 * Il tipo X è il tipo di oggetti gestiti dalla lista.
 * 
 * @author jfabrix101
 *
 * @param <A> - Tipo dell'object model dell'activity controller
 * @param <B> - Tipo dell'oggetto gestito dalla lista
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractFragmentList<A, X> 
extends AbstractFragmentContent<A, X> {

	private Logger mLogger = Logger.getInstance(AbstractFragmentList.class);
	
	private ListView mListView;
	
	
	protected ListView getListView() { return mListView; }
	
	// CommandID per la selezione di un item della lista
	public static int COMMAND_ItemListSelected = 1520;
	
	/*
	 * Metodo responsabile della restituzione della view dell'iesimo elemnto della lista
	 */
	public abstract View getViewForItem(X item, int position, View convertView);
	
	
	protected List<X> mDataModel = null;
	protected List<X> getDataModel() { return mDataModel; }
	protected void setDataModel(List<X> model) { mDataModel = model; }
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 	Bundle savedInstanceState) {
		mLogger.trace("onCreateView()");
		int layout = getFragmentLayout();
		View v = null;
		if (layout <= 0) {
			mLogger.warn("onCreateView() - getFragmentLayout return an invalid value. Creating a default listView");
			mListView = new ListView(mActivityController);
			mListView.setId(android.R.id.list);
			mListView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mListView.setDividerHeight(0);
			mListView.setDrawSelectorOnTop(false);
			v = mListView;
		} else {
			v  = inflater.inflate(layout, container, false);
			mListView = (ListView) v.findViewById(android.R.id.list);
		}
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {
				onListItemClick(mListView, v, position, id);
				
			}
		});
		initializeView(v);
		return v;
	}
	
	@Override
	public void onResume() {
		mLogger.trace("onResume()");
		super.onResume();
		populateList();
	}
	
	
	@SuppressWarnings("unchecked") 
	public void onListItemClick(ListView l, View v, int position, long id) {
		mLogger.trace("onListItemClick()");
		//super.onListItemClick(l, v, position, id);
		l.setItemChecked(position, true);
		
		X item = (X) getListView().getAdapter().getItem(position);
		mLogger.trace("onListItemClick() - Selected Object : %s", item.toString());
		boolean processed = mActivityController.onFragmentCommandEvent(this, COMMAND_ItemListSelected, item);
		if (!processed) {
			mLogger.warn("Ignored message 'COMMAND_ItemListSelected' (%d) from fragment %s", 
					COMMAND_ItemListSelected, getClass().getName());
		}
	}
	
		
	/**
	 * Populate the adapter for listView
	 */
	protected void populateList() {
		mLogger.trace("populateList()");
		if (mDataModel == null) return;
		ArrayAdapter<X> adapter = new ArrayAdapter<X>(mActivityController, 0, mDataModel) {
			public View getView(int position, View convertView, ViewGroup parent) {
				X item = getItem(position);
				View itemView  = getViewForItem(item, position, convertView);
				if (itemView == null) {
					mLogger.warn("Invalid View for itemList - using default View ");
					TextView tv = new TextView(getActivity());
					tv.setText(item.toString());
					tv.setTextSize(18);
					itemView = tv;
				}
				return itemView;
			};
		};
		getListView().setAdapter(adapter);
		registerForContextMenu(getListView());
	}
	
	
	@SuppressWarnings("unchecked") @Override
	public void onCreateContextMenu(ContextMenu menu, View v,	ContextMenuInfo menuInfo) {
		mLogger.trace("onCreateContextMenu()");
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo aMenuInfo = (AdapterContextMenuInfo) menuInfo;
		int position = aMenuInfo.position;
		X item = (X)getListView().getItemAtPosition(position);
		createContextMenu(menu, item, position);
	}
	
	/**
	 * Callback method per la generazione di un menù contestuale
	 * @param menu - Il menu da creare
	 * @param item - L'item su cui viene effettuata la selezione
	 * @param position - La posizione all'interno della lista
	 */
	public abstract void createContextMenu(ContextMenu menu, X item, int position);
	
	/**
	 * Callback method per la gestione di una selezione di una voce di menu contestuale
	 * @param menuItem - Menu Item del menu contestuale
	 * @param item - Oggetto della lista a cui appartiene il menu
	 * @param position - la posizione dell'oggetto all'interno della lista model
	 * @return true se l'evento è stato gestito
	 */
	public abstract boolean contextMenuItemSelected(MenuItem menuItem, X item, int position);
	
	
	@SuppressWarnings("unchecked") @Override
	public boolean onContextItemSelected(MenuItem menuItem) {
		try {
			AdapterContextMenuInfo aMenuInfo = (AdapterContextMenuInfo) menuItem.getMenuInfo();
			int position = aMenuInfo.position;
			mLogger.trace("onContextItemSelected(...) - " + aMenuInfo.toString());
			X item = (X)getListView().getItemAtPosition(position);
			return contextMenuItemSelected(menuItem, item, position);
		} catch (Exception e) {
			return false;
		}
	}
}
