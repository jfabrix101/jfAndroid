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
 * 
 * First implementation : Thanks to Cyril Mottier (http://www.cyrilmottier.com)
 */
package jfabrix101.lib.quickActions;

import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import jfabrix.lib.R;

/**
 * A {@link QuickActionGrid} is an implementation of a {@link QuickActionWidget}
 * that displays {@link QuickAction}s in a grid manner. This is usually used to create
 * a shortcut to jump between different type of information on screen.
 * 
 * @author Benjamin Fellous
 * @author Cyril Mottier
 */
public class QuickActionGrid extends QuickActionWidget {

    private GridView mGridView;

    public QuickActionGrid(Context context) {
        super(context);

        setContentView(R.layout.quickactions_grid);

        final View v = getContentView();
        mGridView = (GridView) v.findViewById(R.id.gdi_grid);
    }

    @Override
    protected void populateQuickActions(final List<QuickAction> quickActions) {

        mGridView.setAdapter(new BaseAdapter() {

            public View getView(int position, View view, ViewGroup parent) {

                TextView textView = (TextView) view;

                if (view == null) {
                    final LayoutInflater inflater = LayoutInflater.from(getContext());
                    textView = (TextView) inflater.inflate(R.layout.quickactions_grid_item, mGridView, false);
                }

                QuickAction quickAction = quickActions.get(position);
                textView.setText(quickAction.getTitle());
                textView.setCompoundDrawablesWithIntrinsicBounds(null, quickAction.getDrawable(), null, null);
                textView.setTag(quickAction); // Store action for future reference
                return textView;

            }

            public long getItemId(int position) {
                return position;
            }

            public Object getItem(int position) {
                return null;
            }

            public int getCount() {
                return quickActions.size();
            }
        });

        mGridView.setOnItemClickListener(mInternalItemClickListener);
    }

    @Override
    protected void onMeasureAndLayout(Rect anchorRect, View contentView) {

        contentView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        contentView.measure(MeasureSpec.makeMeasureSpec(getScreenWidth(), MeasureSpec.EXACTLY),
                LayoutParams.WRAP_CONTENT);

        int rootHeight = contentView.getMeasuredHeight();

        int offsetY = getArrowOffsetY();
        int dyTop = anchorRect.top;
        int dyBottom = getScreenHeight() - anchorRect.bottom;

        boolean onTop = (dyTop > dyBottom);
        int popupY = (onTop) ? anchorRect.top - rootHeight + offsetY : anchorRect.bottom - offsetY;

        setWidgetSpecs(popupY, onTop);
    }

    private OnItemClickListener mInternalItemClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        	QuickAction action = (QuickAction) view.getTag();
            getOnQuickActionClickListener().onQuickActionClicked(QuickActionGrid.this, action);
            if (getDismissOnClick()) {
                dismiss();
            }
        }
    };

}
