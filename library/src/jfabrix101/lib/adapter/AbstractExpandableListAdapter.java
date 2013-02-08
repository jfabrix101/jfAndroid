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
package jfabrix101.lib.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;

/**
 * Adapter per liste espandibili. 
 * Una lista espandibile è caratterizzata da due tipi di informazioni
 * Il tipo A è il tipo della categoria, mentre il tipo B è usato per gestire 
 * una lista di B associatio alla chiave A della mappa.
 * 
 * Questo adapter utilizza anche il tips per nascondere il descrittore di gruppo
 * qualora la lista associata al gruppo fosse vuota.
 * 
 *
 * @author fabrizio
 * 
 * @param <A> - Tipo gruppo
 * @param <B> - Tipo figlio (Ad ogni gruppo A è associata una lista di B)
 */
public abstract class AbstractExpandableListAdapter<A, B> implements ExpandableListAdapter {

	private final List<Entry<A, List<B>>> objects;

	private final DataSetObservable dataSetObservable = new DataSetObservable();

	protected final Context context;
	private final Integer groupClosedView;
	private final Integer groupExpandedView;
	private final Integer childView;
	private final LayoutInflater inflater;

	// Per la gestione dei gruppi vuoti
	private static final int[] EMPTY_STATE_SET = {};
	private static final int[] GROUP_EXPANDED_STATE_SET =  {android.R.attr.state_expanded};
	private static final int[][] GROUP_STATE_SETS = {   EMPTY_STATE_SET,  GROUP_EXPANDED_STATE_SET };
	
	public AbstractExpandableListAdapter(Context context, int groupClosedView,
			int groupExpandedView, int childView,
			Map<A, List<B>> dataMap) {
		this.context = context;
		this.objects = convertMap(dataMap);
		this.groupClosedView = Integer.valueOf(groupClosedView);
		this.groupExpandedView = Integer.valueOf(groupExpandedView);
		this.childView = Integer.valueOf(childView);
		this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	
	private List<Entry<A, List<B>>> convertMap(Map<A, List<B>> map) {
		List<Entry<A, List<B>>> list = new ArrayList<Map.Entry<A,List<B>>>();
		Set<Entry<A, List<B>>> entries = map.entrySet();
		for (Entry<A, List<B>> entry : entries) {
			list.add(entry);
		}
		return list;
	}
	
	public void add(Entry<A, List<B>> group) {
		this.getObjects().add(group);
		this.notifyDataSetChanged();
	}

	public void remove(A group) {
		for (Entry<A, List<B>> entry : this.getObjects()) {
			if (entry != null && entry.getKey().equals(group)) {
				this.getObjects().remove(group);
				this.notifyDataSetChanged();
				break;
			}
		}
	}

	public void remove(Entry<A, List<B>> entry) {
		remove(entry.getKey());
	}

	public void addChild(A group, B child) {
		for (Entry<A, List<B>> entry : this.getObjects()) {
			if (entry != null && entry.getKey().equals(group)) {
				if (entry.getValue() == null)
					entry.setValue(new ArrayList<B>());

				entry.getValue().add(child);
				this.notifyDataSetChanged();
				break;
			}
		}
	}

	public void removeChild(A group, B child) {
		for (Entry<A, List<B>> entry : this.getObjects()) {
			if (entry != null && entry.getKey().equals(group)) {
				if (entry.getValue() == null)
					return;

				entry.getValue().remove(child);
				this.notifyDataSetChanged();
				break;
			}
		}
	}

	/*
	 * Nasconde il descrittore di gruppo se la lista dei figli è vuota.
	 * Da richiamare all'interno del metodo che crea la view del gruppo
	 */
	public void hideGroupSelectorIfEmpty(View v, int groupPosition, boolean isExpanded, int indicatorRefId) {
		View ind = v.findViewById( indicatorRefId );
		if( ind != null ) {
			ImageView indicator = (ImageView)ind;
			if( getChildrenCount( groupPosition ) == 0 ) {
				indicator.setVisibility( View.INVISIBLE );
			} else {
				indicator.setVisibility( View.VISIBLE );
				int stateSetIndex = ( isExpanded ? 1 : 0) ;
				Drawable drawable = indicator.getDrawable();
				drawable.setState(GROUP_STATE_SETS[stateSetIndex]);
			}
		}
	}
	
	public void notifyDataSetChanged() {
		this.getDataSetObservable().notifyChanged();
	}

	public void notifyDataSetInvalidated() {
		this.getDataSetObservable().notifyInvalidated();
	}

	public void registerDataSetObserver(DataSetObserver observer) {
		this.getDataSetObservable().registerObserver(observer);
	}

	public void unregisterDataSetObserver(DataSetObserver observer) {
		this.getDataSetObservable().unregisterObserver(observer);
	}

	public int getGroupCount() {
		return getObjects().size();
	}

	public int getChildrenCount(int groupPosition) {
		return getObjects().get(groupPosition).getValue().size();
	}

	public A getGroup(int groupPosition) {
		return getObjects().get(groupPosition).getKey();
	}

	public B getChild(int groupPosition, int childPosition) {
		return getObjects().get(groupPosition).getValue().get(childPosition);
	}

	public long getGroupId(int groupPosition) {
		return ((Integer) groupPosition).longValue();
	}

	public long getChildId(int groupPosition, int childPosition) {
		return ((Integer) childPosition).longValue();
	}

	public boolean hasStableIds() {
		return true;
	}

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		if (convertView != null
				&& convertView.getId() != (isExpanded ? getGroupExpandedView()
						: getGroupClosedView())) {
			// do nothing, we're good to go, nothing has changed.
		} else {
			// something has changed, update.
			convertView = inflater.inflate(isExpanded ? getGroupExpandedView()
					: getGroupClosedView(), parent, false);
			convertView.setTag(getObjects().get(groupPosition));
		}

		return convertView;
	}

	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		if (convertView != null) {
			// do nothing
		} else {
			// create
			convertView = inflater.inflate(getChildView(), parent, false);
			convertView.setTag(getObjects().get(groupPosition).getValue()
					.get(childPosition));
		}

		return convertView;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public boolean areAllItemsEnabled() {
		return true;
	}

	public boolean isEmpty() {
		return getObjects().size() == 0;
	}

	public void onGroupExpanded(int groupPosition) {
	}

	public void onGroupCollapsed(int groupPosition) {
	}

	public long getCombinedChildId(long groupId, long childId) {
		return groupId * 10000L + childId;
	}

	public long getCombinedGroupId(long groupId) {
		return groupId * 10000L;
	}

	protected DataSetObservable getDataSetObservable() {
		return dataSetObservable;
	}

	protected List<Entry<A, List<B>>> getObjects() {
		return objects;
	}

	protected Context getContext() {
		return context;
	}

	protected Integer getGroupClosedView() {
		return groupClosedView;
	}

	protected Integer getGroupExpandedView() {
		return groupExpandedView;
	}

	protected Integer getChildView() {
		return childView;
	}
}