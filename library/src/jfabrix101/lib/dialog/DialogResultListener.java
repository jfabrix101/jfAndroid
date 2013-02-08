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
package jfabrix101.lib.dialog;

import android.os.Bundle;

public interface DialogResultListener {

	/**
	 * Callback method da parte di un dialogFragment
	 * @param tag - Tag di chiamata
	 * @param payload - ObjectModel del dialogFragment
	 * 
	 * Nota: Se la finestra di dialogo viene cancellata (cancel / back) il metodo non viene invocato
	 */
	public void onDialogDone(String tag, Bundle payload);

	
}
