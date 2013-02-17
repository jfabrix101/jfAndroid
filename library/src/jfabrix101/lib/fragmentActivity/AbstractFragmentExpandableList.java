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
import java.util.Map;

import jfabrix101.lib.adapter.AbstractExpandableListAdapter;
import jfabrix101.lib.util.Logger;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;

/**
 * Fragment per la gestione di una ExpandableListView.
 * 
 * Chi estende questo fragment deve fornire l'implementazione minima per la gestione di una
 * lista espandibile.
 * 
 * Il layout associato a questa view deve contenere un ExpandableListView con 
 * id = android:id/list. Di seguito un esempio
 * 
 * <code>
  <ExpandableListView
        android:background="#F0F0F0"
        android:id="@android:id/list"
        android:paddingTop="15dip"
        android:layout_width="fill_parent"
        android:layout_height="fill_parent"
        android:layout_weight="1"
        android:divider="#00000000"
        android:dividerHeight="0px"
        android:drawSelectorOnTop="false"
        android:groupIndicator="@android:color/transparent" />
 * </code>
 * 
 * Il ciclo di vita di questo componente è il seguente :
 * 
 * <ol>
 * 		<li> Viene creato il componente nell'activity che contiene il fragment
 * 			attraverso il metodo statico newInstance (patter di creazione) e 
 * 			passando un bundle contenente i dati di inizializzazione.
 * 		<li> Viene chiamato il metodo <code>initFragment</code> e passato come 
 * 			argomento il bundle usato per la creazione. In questa implementazione
 * 			non è necessario fare nulla se non richiamare il metodo super che si fa
 * 			carico della memorizzazione dei riferimenti al gruppo ed al figlio selezionato
 * 		<li> Viene chiamato il metodo <code>initializeView</code> dove viene passato la
 * 			vista che contiene la lista espandibile. Se ci sono altri oggetti nella view, è
 * 			ora possibile referenziarli.
 * 		<li> Viene richiamato il metodo <code>onResume</code> che chiama in cascata il
 * 			metodo <code>getData()</code> per il popolamento e la visualizzazione della lista
 * 			espandibile.
 * </ol>
 * 
 * <p> I metodi che è necessario ridefinire sono:
 * 
 * <ul>
 * <li> <code>getData</code>: Restituisce il modello dati da visualizzare
 * <li> <code>getGroupItemView</code>: Restituisce la view per la visualizzazione di un gruppo (A)
 * <li> <code>getChildItemView</code>: Restituisce la view per la visualizzazione di un figlio (B)
 * <li> <code>onGroupItemClick(int, A)</code>: Callback per la selezione di un gruppo
 * <li> <code>onChildtemClick(int, A)</code>: Callback per la selezione di un figlio
 * <li> <code>onCreateGroupContextMenu</code>: Callback per la creazione di un menu' contestuale sul gruppo
 * <li> <code>onCreateChildContextMenu</code>: Callback per la creazione di un menu' contestuale su un figlio
 * <li> <code>onGroupContxtItemSelected</code>: Callback per la selezione di una voce di menu' su un gruppo 
 * <li> <code>onChildContxtItemSelected</code>: Callback per la selezione di una voce di menu' su un figlio
 * </ul>

 * @author Fabrizio Russo - fabrizio.russo@gmail.com
 *
 * @param <A>
 * @param <B>
 */
public abstract class AbstractFragmentExpandableList<A, X, Y> 
extends AbstractFragmentContent<A, X>  {

	private Logger mLogger = Logger.getInstance(AbstractExpandableListAdapter.class);
	
	protected ExpandableListView expandableList = null;
	protected AbstractExpandableListAdapter<X, Y> adapter = null;
	
	private int selectedGroupPosition = 0;
	private int selectedChildPosition = -1;

	public int COMMAND_GroupItemSelected = 13467212;
	public int COMMAND_ChildItemSelected = 13456222;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 	Bundle savedInstanceState) {
		int layout = getFragmentLayout();
		View v = null;
		if (layout == 0) {
			mLogger.warn("getFragmentLayout return an invalid value. Using default layout");
			ExpandableListView view = new ExpandableListView(getActivity());
			view.setId(android.R.id.list);
			view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			view.setDividerHeight(0);
			view.setDrawSelectorOnTop(false);
			v = view;
		}
		else v  = inflater.inflate(getFragmentLayout(), container, false);
		expandableList = (ExpandableListView) v.findViewById(android.R.id.list);
		if (expandableList == null) throw new RuntimeException("ExapandableListView not found. " +
				"Add an expandableListView with id = android.R.id.list");
		initializeView(v);
		return v;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("groupPosition", selectedGroupPosition);
		outState.putInt("childPosition", selectedChildPosition);
	}
	
	
	public abstract Map<X, List<Y>> getData();

	public abstract View getGroupItemView(X item, int groupPosition, boolean isExpanded, View convertView);
	public abstract View getChildItemView(Y item, int groupPosition, int childPosition, boolean isLastChild, View convertView);
	
	public abstract void onGroupItemClick(int groupPosition, X item);
	public abstract boolean onChildItemClick(int groupPosition, int childPosition, Y item);
	
	/*
	 * Return the object (A) for gruoup at position
	 */
	protected X getGroup(int groupPosition) { return (X)adapter.getGroup(groupPosition); }
	
	/*
	 * Return the object (B) as a position in a list og items associated with group at position
	 */
	protected Y getChild(int groupPosition, int childPosition) { return (Y)adapter.getChild(groupPosition, childPosition); }

	
	@SuppressWarnings("unchecked")
	protected void populateList() {
		
		adapter = new AbstractExpandableListAdapter<X, Y>(getActivity(), 0, 0, 0, getData()) {
			@Override
			public View getGroupView(int groupPosition,	boolean isExpanded, View convertView, ViewGroup parent) {
				X item = getGroup(groupPosition);
				View v = getGroupItemView(item, groupPosition, isExpanded, convertView);
				if (v == null) mLogger.warn("Invalid view for groupView. The view is null !!");
				return v;
			}
			
			@Override
			public View getChildView(int groupPosition,	int childPosition, boolean isLastChild,	View convertView, ViewGroup parent) {
				Y item = getChild(groupPosition, childPosition);
				View v = getChildItemView(item, groupPosition, childPosition, isLastChild, convertView);
				if (v == null) mLogger.warn("Invalid view for childView. The view is null !!");
				return v;
			}
		};
		
		expandableList.setAdapter(adapter);
		expandableList.setSelectedChild(selectedGroupPosition, selectedChildPosition, true);
		registerForContextMenu(expandableList);
		
		final ExpandableListAdapter finalAdapter = adapter;
		expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			
			public boolean onChildClick(ExpandableListView parent, View v,	int groupPosition, int childPosition, long id) {
				Y child = (Y)finalAdapter.getChild(groupPosition, childPosition);
				selectedChildPosition = childPosition;
				selectedGroupPosition = groupPosition;
				boolean process = onChildItemClick(groupPosition, childPosition, child);
				if (process) {
					// FIXME: Aggiustare la propagazione degli eventi
					//mActivityController.onFragmentCommandEvent(this, COMMAND_ChildItemSelected, child);
				}
				return false;
			}
		});
		expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			
			public boolean onGroupClick(ExpandableListView parent, View v,	int groupPosition, long id) {
				X group = (X)finalAdapter.getGroup(groupPosition);
				selectedGroupPosition = groupPosition;
				selectedChildPosition =  -1;
				onGroupItemClick(groupPosition, group);
				return false;
			}
		});
		if (getSelectedChildPosition() > 0) {
			expandableList.setSelectedChild(getSelectedGroupPosition(), getSelectedChildPosition(), true);
		} else expandableList.setSelectedGroup(getSelectedGroupPosition());
	}
	
	public abstract void onCreateGroupContextMenu(ContextMenu menu, View v, X item);
	public abstract void onCreateChildContextMenu(ContextMenu menu, View v, Y item);
	
	@Override @SuppressWarnings("all")
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		
		ExpandableListView.ExpandableListContextMenuInfo info =  (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
		
		int type =	ExpandableListView.getPackedPositionType(info.packedPosition);
		if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
			int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);
			int childPos = ExpandableListView.getPackedPositionChild(info.packedPosition);
			Y item = (Y)adapter.getChild(groupPos, childPos);
			selectedChildPosition = groupPos;
			selectedGroupPosition = childPos;
			onCreateChildContextMenu(menu, v, item);
		}
		if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
			int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);
			X item = (X)adapter.getGroup(groupPos);
			selectedChildPosition = groupPos;
			onCreateGroupContextMenu(menu, v, item);
		}
		
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	public abstract boolean onGroupContextItemSelected(MenuItem mItem, X item);
	public abstract boolean onChildContextItemSelected(MenuItem mItem, Y item);
	
	@Override @SuppressWarnings("all")
	public boolean onContextItemSelected(MenuItem mItem) {
		
		ExpandableListContextMenuInfo info =(ExpandableListContextMenuInfo) mItem.getMenuInfo();
		int groupPos = 0, childPos = 0;
		int type = ExpandableListView.getPackedPositionType(info.packedPosition);
		if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
			groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);
			childPos = ExpandableListView.getPackedPositionChild(info.packedPosition);
			Y item = (Y)adapter.getChild(groupPos, childPos);
			return onChildContextItemSelected(mItem, item);
		}
		if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
			groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);
			X item = (X)adapter.getGroup(groupPos);
			return onGroupContextItemSelected(mItem, item);
		}
		return super.onContextItemSelected(mItem);
	}
	
	public int getSelectedChildPosition() {	return selectedChildPosition; }

	public int getSelectedGroupPosition() { return selectedGroupPosition; }

	public void setSelectedChildPosition(int selectedChildPosition) { this.selectedChildPosition = selectedChildPosition; }

	public void setSelectedGroupPosition(int selectedGroupPosition) { this.selectedGroupPosition = selectedGroupPosition; }
	
}
