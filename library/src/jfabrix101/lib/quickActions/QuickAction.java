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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * A QuickAction implements an item in a {@link QuickActionWidget}. 
 * A QuickAction represents a single action and may contain a text and an icon.
 * 
 * @author Benjamin Fellous
 * @author Cyril Mottier
 * @author Fabrizio Russo
 */
public class QuickAction {

	private int mId;
    private Drawable mDrawable;
    private CharSequence mTitle;

    WeakReference<View> mView;

    public QuickAction(int id, Drawable d, CharSequence title) {
    	mId = id;
        mDrawable = d;
        mTitle = title;
    }

    public QuickAction(Context ctx, int id, int drawableId, CharSequence title) {
    	mId = id;
        mDrawable = ctx.getResources().getDrawable(drawableId);
        mTitle = title;
    }

    public QuickAction(Context ctx, int id, Drawable d, int titleId) {
    	mId = id;
        mDrawable = d;
        mTitle = ctx.getResources().getString(titleId);
    }

    public QuickAction(Context ctx, int id, int drawableId, int titleId) {
    	mId = id;
        mDrawable = ctx.getResources().getDrawable(drawableId);
        mTitle = ctx.getResources().getString(titleId);
    }
    
    public CharSequence getTitle() { return mTitle; }
    public void setTitle(String title) { mTitle = title; }

    public Drawable getDrawable() { return mDrawable; }
    public void setDrawable(Drawable d) { mDrawable = d; }
    
    public int getId() { return mId; }
    public void setId(int id) { mId = id; }
    

}
