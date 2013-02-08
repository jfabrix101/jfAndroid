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
package jfabrix101.lib.activity;

import jfabrix101.lib.util.Logger;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

/**
 * Base class for a fragment managed by <code>AbstractFragmentActivityController</code>
 * 
 * 
 * @author jfabrix101
 *
 * @param <A> Generic class for the activity. This is the type of the objectModel ohosted by activity.
 *  	See <code>getActivityObjectModel</code> and <code>setActivityObjectModel</code>
 *  
 * @param <B> Generic class for the fragment. The fragment will host an objectModel of this type
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractFragmentContent<A, X> 
extends android.support.v4.app.Fragment  {

	protected AbstractFragmentActivityController<A> mActivityController = null;
	
	// Logger reference for the fragment
	private Logger mLogger = Logger.getInstance(AbstractFragmentContent.class);

	// ObjectModel managed by fragment
	protected X fragmentObjectModel = null;
	
	/**
	 * Return the object model managed by activity
	 * @return
	 */
	protected A getActivityObjectModel() { 
		return mActivityController.getObjectModel(); 
	}
	
	/**
	 * Set the objectModel managed by activity
	 * @param newModel
	 */
	protected void setActivityObjectModel(A newModel) {
		mActivityController.setObjectModel(newModel);
	}

	/**
	 * Return the objectModle managed by fragment
	 * @return 
	 */
	public X getObjectModel() { 
		return fragmentObjectModel; 
	}
	
	/**
	 * Set the object model managed by fragment
	 * @param newObjectModel - The new objectModel
	 */
	public void setObjectModel(X newObjectModel) { 
		this.fragmentObjectModel = newObjectModel; 
	}
	
	/**
	 * Return the last visulizationMode.
	 */
	@SuppressWarnings("all")
	protected VisualizationMode getVisualizationMode() {
		return mActivityController.getVisualizationMode();
	}
	
	
	@Override @SuppressWarnings("all")
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mLogger.trace("onAttach()");
		A objectModel = getActivityObjectModel();
		if (objectModel == null) mLogger.warn("Object model is null");
	}
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mLogger.trace("onCreate()");
		super.onCreate(savedInstanceState);
		
	}
	
	
	@Override
	public void onDetach() {
		mLogger.trace("onDetach()");
		super.onDetach();
	}
	
	
	@Override
	public void onDestroy() {
		mLogger.trace("onDestoy()");
		super.onDestroy();
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mLogger.trace("onActivityCreated()");
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mLogger.trace("onCreateView()");
		View v = null;
		int layout = getFragmentLayout();
		if (layout <= 0) {
			mLogger.warn("onCreateView() - getFragmentLayout return an invalid value. Creating a default contentView");
			TextView view = new TextView(mActivityController);
			view.setId(android.R.id.content);
			view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			view.setText("objectMode :  " + getActivityObjectModel());
			view.setTextSize(22);
			v = view;
		} else v  = inflater.inflate(layout, container, false);
		initializeView(v);
		return v;
	}
	
	
	@Override
	public void onResume() {
		mLogger.trace("onResume()");
		super.onResume();
	}
	
	
	public void sendFragmentCommand(int commandType, Object payload) {
		boolean processed = mActivityController.onFragmentCommandEvent(this, commandType, payload);
		if (!processed) {
			mLogger.warn("Ignored message (%d) sent by fragment %s - payload : %s", 
					commandType, getClass().getName(), payload);
		}
	}
	
	
	// ------------ FragmentComponent
	
	/**
	 * Restituisce il layout da utilizzare all'interno del fragment.
	 * Se restituisce un valore zero (o negativo) verrà costruito
	 * un layout in base al tipo di fragment (list o content)
	 */
	public abstract int getFragmentLayout();

	/**
	 * Inizializza il fragment, passando sia il controller come argomento
	 * che l'attuale valore dell'objectModel
	 */
	public abstract void inizializeFragment(AbstractFragmentActivityController<A> parentActivity, A currentObjectModel);

	
	/**
	 * Metodo invocato per inizializzare la View ed effettuare il binding
	 * con i componenti. Questo metodo viene invocato sempre dopo il
	 * metodo <code>inizializeFragment</code>
	 */
	public abstract  void initializeView(View v);
	
}
