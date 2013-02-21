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

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public abstract class AbstractFragmentContentPager<A, X> extends AbstractFragmentContent<A, X> {

	private ViewPager internalViewPager;
    private InternalPagerAdapter internalPageAdapter;
	
    protected List<X> mDataModel = null;
	protected List<X> getDataModel() { return mDataModel; }
	protected void setDataModel(List<X> model) { mDataModel = model; }
	
	/*
	 * Metodo responsabile della restituzione della view dell'iesimo elemnto della lista
	 */
	public abstract void populateViewForItem(X item, int position, View view);
	
	
    public class InternalPagerAdapter extends PagerAdapter {
    	@Override
    	public int getCount() {
    		if (getDataModel() == null) return 0;
    		else return 	getDataModel().size(); 	
    	}
    	
    	@Override
    	public boolean isViewFromObject(View view, Object object) {
    		 return (view == object);
    	}
    	
    	@Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
                collection.removeView((View) view);
        }
    	
    	@Override
        public Object instantiateItem(ViewGroup collection, int position) {
    		int viewLayoutId = getFragmentLayout();
    		if (viewLayoutId <= 0) {
                TextView tv = new TextView(getActivity());
                tv.setText("" + position);
                tv.setTextSize(30);
                collection.addView(tv,0);
                return tv;
    		} else {
    			X item = getDataModel().get(position);
    			LayoutInflater inflater = LayoutInflater.from(getActivity());
    			View v = inflater.inflate(viewLayoutId, null);
    			populateViewForItem(item, position, v);
    			collection.addView(v, 0);
    			return v;
    		}
    	}
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 internalViewPager = new ViewPager(getActivity());
		 internalViewPager.setBackgroundColor(Color.RED);
	     internalViewPager.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	     initializeView(internalViewPager);
	     return internalViewPager;
	}
	
	@Override
	public void initializeView(View v) {
		internalPageAdapter = new InternalPagerAdapter();
        internalViewPager = new ViewPager(getActivity());
        internalViewPager.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        internalViewPager.setAdapter(internalPageAdapter);
		
	}
		
}
