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
package jfabrix101.lib.helper;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class ImageHelper {

	public static Drawable resize(Drawable image, int scale) {
	    Bitmap d = ((BitmapDrawable)image).getBitmap();
	    int width = d.getWidth() * scale / 100;
	    int height = d.getHeight() * scale / 100;
	    Bitmap bitmapOrig = Bitmap.createScaledBitmap(d, width, height, false);
	    return new BitmapDrawable(bitmapOrig);
	}
	
	public static Drawable resize(Drawable image, int width, int height) {
	    Bitmap d = ((BitmapDrawable)image).getBitmap();
	    Bitmap bitmapOrig = Bitmap.createScaledBitmap(d, width, height, false);
	    return new BitmapDrawable(bitmapOrig);
	}
	
	
}
