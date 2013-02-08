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
package jfabrix101.lib.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;

public class ImageCache {

	private static ImageCache instance = null;
	
	private Logger mLogger = Logger.getInstance(ImageCache.class);
	private Map<String, Drawable> map = new HashMap<String, Drawable>();
	private Context ctx = null;
	
	/**
	 * Return the number of images cached
	 */
	public int size() { return map.size(); }
	
	/**
	 * Return the key of cached images
	 */
	public Set<String> getKeys() { return map.keySet(); }
	
	/**
	 * Return the folder where the images are taken
	 * @return
	 */
	public File getCacheFolder() {
		File imageFolder = new File(ctx.getFilesDir() + "/images");
		return imageFolder;
	}
	
	public static ImageCache getInstance(Context ctx) {
		if (instance == null) instance = new ImageCache();
		instance.ctx = ctx;
		File folder = instance.getCacheFolder();
		if (!folder.exists()) folder.mkdirs();
		return instance;
	}
	
	private ImageCache() { }
	
	public void clear() { map.clear(); }
	
	public Drawable getDrawable(String name) {
		Drawable d = map.get(name);
		if (d != null) return d;
		else {
			mLogger.debug("Loading image for resource " + name);
			File imageFolder = getCacheFolder();
			File resFile = new File(imageFolder,  name + ".png");
			// Cerca il file con le estensioni più comuni
			if (!resFile.exists()) resFile = new File(imageFolder,  name + ".jpg");
			if (!resFile.exists()) resFile = new File(imageFolder, name + ".jpeg");
			if (!resFile.exists()) resFile = new File(imageFolder, name + ".gif");
			if (!resFile.exists()) {
				mLogger.error("Image " + name + " not found in folder %s ", imageFolder.getAbsolutePath());
				return null;
			}
			d = DrawableContainer.createFromPath(resFile.getAbsolutePath());
			map.put(name, d);
			return d;
		}
	}
}