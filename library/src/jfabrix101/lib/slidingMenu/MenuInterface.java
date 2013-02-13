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
package jfabrix101.lib.slidingMenu;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

public interface MenuInterface {

	public abstract void scrollBehindTo(int x, int y, 
			CustomViewBehind cvb, float scrollScale);
	
	public abstract int getMenuLeft(CustomViewBehind cvb, View content);
	
	public abstract int getAbsLeftBound(CustomViewBehind cvb, View content);

	public abstract int getAbsRightBound(CustomViewBehind cvb, View content);

	public abstract boolean marginTouchAllowed(View content, int x, int threshold);
	
	public abstract boolean menuOpenTouchAllowed(View content, int currPage, int x);
	
	public abstract boolean menuTouchInQuickReturn(View content, int currPage, int x);
	
	public abstract boolean menuClosedSlideAllowed(int x);
	
	public abstract boolean menuOpenSlideAllowed(int x);
	
	public abstract void drawShadow(Canvas canvas, Drawable shadow, int width);
	
	public abstract void drawFade(Canvas canvas, int alpha, 
			CustomViewBehind cvb, View content);
	
	public abstract void drawSelector(View content, Canvas canvas, float percentOpen);
	
}
