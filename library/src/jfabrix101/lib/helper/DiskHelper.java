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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

import jfabrix101.lib.util.GenericAsyncTask;
import jfabrix101.lib.util.Logger;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

public class DiskHelper {

	/**
	 * Restituisce un FILE dalla sdcard.
	 * Restituisce null se la sdcard non è disponibile.
	 */
	public static File getSDPath(String path) {
		boolean isMounted = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		if (!isMounted) return null;
		File sdcard = Environment.getExternalStorageDirectory();
		File f = new File(sdcard, path);
		return f;
	}

	/**
	 * Restituisce una cartella PRIVATA dell'applicazione sulla sdcard.
	 * Questa cartella verrà cancellata quando l'applicazione sarà disinstallata.
	 * Restituisce null se la sdcard non è disponibile.
	 */
	public static File getPrivateSdCardFolder(Context context) {
		boolean isMounted = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		if (!isMounted) return null;
		File path = context.getExternalFilesDir(null);
		return path;
	}
	
//	public static boolean checkSdPath(String sdPath) {
//		if (!sdPath.startsWi(Sth("/")) sdPath += "/" + sdPath;
//		File f = new File (android.os.Environment.getExternalStorageDirectory() + sdPath);
//		return f.exists();
//	}
//	
//	public static boolean isSDPresent() {  
//		return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);  
//	}  
//	
//	
//	public static File getSdPath() {
//		return android.os.Environment.getExternalStorageDirectory();
//	}
	
	public static boolean isLink(File item) {
		if (item.isFile() || item.isDirectory()) return false;
		try {
			if (!item.exists())	return true;
			else {
				String cnnpath = item.getCanonicalPath();
				String abspath = item.getAbsolutePath();
				return !abspath.equals(cnnpath);
			}
		} catch (IOException ex) { 
			return true;
		}
	} // isLink
	
	
	public static String getLinkRealPath(File item) {
		if (item.isFile() || item.isDirectory()) return null;
		try {
			String cnnpath = item.getCanonicalPath();
			return cnnpath;
		} catch (IOException ex) {	
			return null;
		}
	}
	
	
	
	
	
	/**
	 * Srive un file di test 
	 */
	public static void saveTextFile(String fullPathName, String text) throws IOException {
		File f = new File(fullPathName);
        FileWriter writer = new FileWriter(f);
        BufferedWriter out = new BufferedWriter(writer);
        out.write(text);
        out.close();
        writer.close();
	}
	
	/**
	 * Srive un array di byte su un file
	 */
	public static void saveBinaryFile(String fullPathName, byte data[]) throws IOException {
//		File f = new File(fullPathName);
//        FileOutputStream out = new FileOutputStream(f);
//        out.write(data);
//        out.close();
        
        OutputStream output = null;
        try {
          output = new BufferedOutputStream(new FileOutputStream(new File(fullPathName)));
          output.write(data);
        }
        finally {
          output.close();
        }
	}
	
	
	/**
	 * Carica un file di testo leggendolo dalla risorse RAW
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static String loadRawTextFile(Context context, String fileName) {
		try {
			String packageName = context.getApplicationContext().getPackageName();
			Resources resources = context.getApplicationContext().getResources();
			int resourceIdentifier = resources.getIdentifier(fileName, "raw",	packageName);
			if (resourceIdentifier != 0) {
				InputStream inputStream = resources.openRawResource(resourceIdentifier);
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
				String line;
				StringBuffer data = new StringBuffer();
				while ((line = reader.readLine()) != null) {
					data.append(line);
				}
				reader.close();
				return data.toString();
			} else
				return null;
		} catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * Carica un file di testo leggendolo tra le risorse RAW dato il suo identificativo (R.raw.xxxx)
	 * @param ctx
	 * @param resId
	 * @return
	 */
	public static String loadRawTextFile(Context ctx, int resId) {
		InputStream inputStream = ctx.getResources().openRawResource(resId);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			String line;
			StringBuffer data = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				data.append(line);
			}
			reader.close();
			return data.toString();
		} catch (IOException e) {
			return null;
		}
		
	}
	
	/**
	 * Carica un file di testo
	 */
	public static String loadTextFile(java.io.InputStream in) {
		StringBuilder contents = new StringBuilder();
	    
	    try {
	      BufferedReader input =  new BufferedReader(new InputStreamReader(in));
	      try {
	        String line = null; //not declared within while loop
	        while (( line = input.readLine()) != null){
	          contents.append(line);
	          contents.append(System.getProperty("line.separator"));
	        }
	      }
	      finally {
	        input.close();
	      }
	    }
	    catch (IOException ex){
	      ex.printStackTrace();
	    }
	    
	    return contents.toString();
	}
	
	/**
	 * Restituisce il contenuto di un file presente nelle risorse 'raw'
	 * 
	 * @param mContext
	 * @param id
	 * @return
	 */
	public static String readRawTextFile(Context mContext, int id) {
		InputStream inputStream = mContext.getResources().openRawResource(id);
		InputStreamReader in = new InputStreamReader(inputStream);
		BufferedReader buf = new BufferedReader(in);
		String line;
		StringBuilder text = new StringBuilder();
		try {
			while ((line = buf.readLine()) != null)
				text.append(line);
		} catch (IOException e) {
			return null;
		}
		return text.toString();
	}
	
	/**
	 * Carica un file di testo
	 */
	public static String loadTextFile(String filePath) {
		try {
			return loadTextFile(new FileInputStream(filePath));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	/**
	 * Legge un file di testo attraverso l'AssetsManager
	 * Il file si deve trovare in res/assets 
	 */
	public static String loadTextFromAssets(Context ctx, String fileName) {
		String txt = null;
		Logger mLogger = Logger.getInstance(DiskHelper.class);
		try {
			java.io.InputStream in = ctx.getAssets().open(fileName);
			txt = DiskHelper.loadTextFile(in);
			in.close();
			
		} catch (Exception e) {
			mLogger.error("Error reading %s from assets", fileName);
		}
		
		return txt;
	}
	

	
	
	/**
	 * Cancella una intera directory e sotto cartelle
	 * @param folder
	 */
	public static void deleteFolder(File folder) {
		Stack<File> folders = new Stack<File>();
		folders.add(folder);
		Logger mLogger = Logger.getInstance(DiskHelper.class);
		
		// Primo step : Rimuove i files
		while (!folders.isEmpty()) {
			File dir = folders.pop();
			File[] files = dir.listFiles();
			if (files != null) for (File f : files) {
				mLogger.debug("deleteFolder - processing file %s", f.getAbsolutePath());
				if (f.isDirectory()) { folders.add(f); }
				else {
					f.delete();
				}
			}
			dir.delete();
		}
		
		// Second step: Rimuove le cartelle
		folders.add(folder);
		while (!folders.isEmpty()) {
			File dir = folders.pop();
			File[] files = dir.listFiles();
			if (files != null) for (File f : files) {
				mLogger.debug("deleteFolder - processing File %s", f.getAbsolutePath());
				if (f.isDirectory()) { folders.add(f); }
				else {
					f.delete();
				}
			}
			dir.delete();
		}
		
	}
	
	
	/**
	 * Restituisce l'estensione di un file
	 * @param fileName
	 * @return
	 */
	public static String getFileExtension(String fileName) {
		if (fileName == null) 	return null;
		
		int dot = fileName.lastIndexOf(".");
		if (dot >= 0) return fileName.substring(dot);
		else return "";
		
	}
	
	public static class FileDateComparator implements Comparator<File> {
		public int compare(File file1, File file2) {
			long size1 = file1.lastModified();
			long size2 = file2.lastModified();
			long result = size1 - size2;
	        if (result < 0) return -1;
	        else if (result > 0) return 1;
	        else return 0;
		}
	}
	
	public static class FileSizeComparator implements Comparator<File> {
		public int compare(File file1, File file2) {
			long size1 = file1.length();
			long size2 = file2.length();
			long result = size1 - size2;
	        if (result < 0) return -1;
	        else if (result > 0) return 1;
	        else return 0;
		}
	}
	
	public static class FileNameComparator implements Comparator<File> {
		public int compare(File arg0, File arg1) {
			String n1 = arg0.getName();
			String n2 = arg1.getName();
			return n1.compareTo(n2);
		}
	}
	
	/**
	 * Effettua uno scanning della sdcard cercando i file con una certa
	 * estensione. Una volta trovati, mostra una dialogBox con la possibilità
	 * di selezionarne uno ed una volta selezionata invoca (reflection) il metodo
	 * <code>methodName</code> sull' oggetto <code>obj</code>
	 * 
	 * @param context
	 * @param title Titolo della finestra (progress bar e dialog di selezione)
	 * @param icon Incona delle finestre
	 * @param extensions Estensione da cercare
	 * @param obj Oggetto su cui invocare il metodo
	 * @param methodName nome del metodo da cercare (prende in input oggetto di tipo File)
	 */
	public static void selectFileProgressBar(final Context context, 
			final String title, final int icon, 
			final String extensions[], 
			final Object obj, final String methodName) {
    	
    	if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			String msg = "Sdcard not available";
			ActivityHelper.showDialogBox(context, "Warning", msg, icon);
			return;
			
		}
    	
    	final List<String> files = new ArrayList<String>();
    	
    	Handler extHandler = new Handler() {
    		@Override
    		public void dispatchMessage(Message msg) {
    			int code = msg.getData().getInt("code", -1);
    			String message = msg.getData().getString("value");
    			if (message == null) message = "";
    			if (code == 540) {
    				ActivityHelper.showDialogBox(context, "Info", message, icon);
    			}
    			if (code == 550) { 
    				try {
    					Method m = obj.getClass().getMethod(methodName, File.class);
    					m.invoke(obj, new File(""));
    				} catch (Exception e) {
    					
    				}
    				
    				return; 
    			}
    				
    		}
    	};
    	
    	// Effettua la ricerca dei file XML o OPML
    	GenericAsyncTask aTask = new GenericAsyncTask(context, extHandler) {
			
			@Override
			public void doTaskInBackground() {
				File root = Environment.getExternalStorageDirectory();
				publishProgress(root.getAbsolutePath());
				
				Stack<File> folders = new Stack<File>();
				folders.add(root);
				
				while (!folders.isEmpty()) {
					File folder = folders.pop();
					if (!folder.canRead()) continue;
					publishProgress(folder.getAbsolutePath());
					File[] ff = folder.listFiles();
					for (File f : ff) {
						if (f.isDirectory()) folders.add(f);
						else {
							String name = f.getAbsolutePath();
							name = name.toLowerCase();
							boolean found = false;
							for (String ext : extensions) {
								if (name.endsWith(ext)) found = true;
							}
							if (found) files.add(f.getAbsolutePath());
						}
					}
				}
				
				if (files.size() == 0) {
					Bundle b = new Bundle();
					b.putInt("code", 540);
					b.putString("value", "No files found");
					handleMessageCode(-1, b);
					return;
				}
				
				else {
					Bundle b = new Bundle();
					b.putInt("code", 550);
					handleMessageCode(-1, b);
				}
			}
		};
		aTask.setTitle(title);
		aTask.setIcon(icon);
		aTask.execute();
    }
	
	
	/**
	 * Restituisce (ed eventualmente crea) la cartella privata
	 * dei dati nella SD-CARD
	 * @param context
	 * @return
	 */
	public static File getApplicationDataFolder(Context context) {
		try {
			ComponentName comp = new ComponentName(context, DiskHelper.class);
			PackageInfo pinfo = context.getPackageManager().getPackageInfo(comp.getPackageName(), 0);
			File installFolder = new File(Environment.getExternalStorageDirectory(), "/data/" + pinfo.packageName);
			if (installFolder.exists()) return installFolder;
			else installFolder.mkdirs();
			return installFolder;
		} catch (Exception e) {
			
		}
		return null;
	}
}
