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

import java.lang.ref.WeakReference;
import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import jfabrix.lib.R;

/**
 * A QuickActionBar displays a set of {@link QuickAction} on a single row. 
 * In case too many items are added to the QuickActionBar, the user can
 * horizontally scroll {@link QuickAction}s. 
 * Using a QuickActionBar is a great replacement for the long click UI pattern. 
 * For instance, {@link QuickActionBar} adds secondary 
 * actions to an item of a ListView.
 * 
 * @author Benjamin Fellous
 * @author Cyril Mottier
 * @author Fabrizio Russo
 */
public class QuickActionBar extends QuickActionWidget {

    private HorizontalScrollView mScrollView;
    private Animation mRackAnimation;
    private ViewGroup mRack;
    private ViewGroup mQuickActionItems;

    private List<QuickAction> mQuickActions;

    public QuickActionBar(Context context) {
        super(context);

        mRackAnimation = AnimationUtils.loadAnimation(context, R.anim.quickactions_rack);

        mRackAnimation.setInterpolator(new Interpolator() {
            public float getInterpolation(float t) {
                final float inner = (t * 1.55f) - 1.1f;
                return 1.2f - inner * inner;
            }
        });

        setContentView(R.layout.quickactions_bar);

        final View v = getContentView();
        mRack = (ViewGroup) v.findViewById(R.id.gdi_rack);
        mQuickActionItems = (ViewGroup) v.findViewById(R.id.gdi_quick_action_items);
        mScrollView = (HorizontalScrollView) v.findViewById(R.id.gdi_scroll);
    }

    @Override
    public void attachToView(View anchor) {
        super.attachToView(anchor);
        mScrollView.scrollTo(0, 0);
        mRack.startAnimation(mRackAnimation);
    }

    @Override
    protected void onMeasureAndLayout(Rect anchorRect, View contentView) {

        contentView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        contentView.measure(MeasureSpec.makeMeasureSpec(getScreenWidth(), MeasureSpec.EXACTLY), LayoutParams.WRAP_CONTENT);

        int rootHeight = contentView.getMeasuredHeight();

        int offsetY = getArrowOffsetY();
        int dyTop = anchorRect.top;
        int dyBottom = getScreenHeight() - anchorRect.bottom;

        boolean onTop = (dyTop > dyBottom);
        int popupY = (onTop) ? anchorRect.top - rootHeight + offsetY : anchorRect.bottom - offsetY;

        setWidgetSpecs(popupY, onTop);
    }

    @Override
    protected void populateQuickActions(List<QuickAction> quickActions) {

        mQuickActions = quickActions;

        final LayoutInflater inflater = LayoutInflater.from(getContext());

        for (QuickAction action : quickActions) {
            TextView view = (TextView) inflater.inflate(R.layout.quickactions_bar_item, mQuickActionItems, false);
            view.setText(action.getTitle());

            view.setCompoundDrawablesWithIntrinsicBounds(null, action.getDrawable(), null, null);
            view.setOnClickListener(mClickHandlerInternal);
            mQuickActionItems.addView(view);
            action.mView = new WeakReference<View>(view);
        }
    }

    @Override
    protected void onClearQuickActions() {
        super.onClearQuickActions();
        mQuickActionItems.removeAllViews();
    }

    private OnClickListener mClickHandlerInternal = new OnClickListener() {

        public void onClick(View view) {

            final OnQuickActionClickListener listener = getOnQuickActionClickListener();

            if (listener != null) {
                final int itemCount = mQuickActions.size();
                for (int i = 0; i < itemCount; i++) {
                    if (view == mQuickActions.get(i).mView.get()) {
                        listener.onQuickActionClicked(QuickActionBar.this, mQuickActions.get(i));
                        break;
                    }
                }
            }

            if (getDismissOnClick()) {
                dismiss();
            }
        }

    };

}
