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

import java.util.List;

import jfabrix101.lib.helper.ActivityHelper;
import jfabrix101.lib.util.Logger;
import android.R;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Base class for activities with fragments. 
 * This class cover the entire life cycle of activity and its fragments.
 * 
 * Follow the instructions to know how use this classes. 
 * 
 * @author jfabrix101 - jfabrix101@gmail.com
 *
 * 
 */
public abstract class AbstractFragmentActivityController<A> extends Activity 
implements ActionBar.OnNavigationListener {

	// ObjectModel for this activity
	private A objectModel = null;
	
	private Logger mLogger = Logger.getInstance(AbstractFragmentActivityController.class);
	
	// Flags used to know if we are in protrait mode and we are visualizing only the right (detail) fragment
	private VisualizationMode mVisualizationMode;
	
	
	// References to fragments. Left Fragment = list fragment, right fragment = detail fragment
	private AbstractFragmentContent<A, ?> refLeftFragment = null;
	private AbstractFragmentContent<A, ?> refRightFragment = null;
	
	// Getter for references instance
	protected AbstractFragmentContent<A, ?> getLeftFragment() { return refLeftFragment; }
	protected AbstractFragmentContent<A, ?> getRightFragment() { return refRightFragment; }

	// Factory methods to create fragments instances
	protected abstract Class<? extends AbstractFragmentContent<A, ?>> getLeftFragmentClass();
	protected abstract Class<? extends AbstractFragmentContent<A, ?>> getRightFragmentClass();

	
	@SuppressWarnings("all")
	protected abstract boolean onFragmentCommandEvent(AbstractFragmentContent srcEvent, int commandType, Object payload);
	
	
	
	protected AbstractFragmentContent<A, ?> createLeftFragmentInstance() {
		return createLeftFragmentInstance(null);
	}
	
	
	// Crea l'istanza del fragment di sinistra
	protected AbstractFragmentContent<A, ?> createLeftFragmentInstance(A model) {
		try {
			refLeftFragment = getLeftFragmentClass().newInstance();
			refLeftFragment.mActivityController = this;
			if (model == null) model = getObjectModel();
			refLeftFragment.inizializeFragment(this, model);
			return refLeftFragment;
		} catch (Exception e) {
			mLogger.error("Unable to creare LeftFragment instance", e.getMessage() );
			return null;
		}
	}
	
	
	protected AbstractFragmentContent<A, ?> createRightFragmentInstance() {
		return createRightFragmentInstance(null);
	}
	
	
	// Crea l'istanza del fragment di destra
	protected AbstractFragmentContent<A, ?> createRightFragmentInstance(A model) {
		try {
			refRightFragment = getRightFragmentClass().newInstance();
			refRightFragment.mActivityController = this;
			if (model == null) model = getObjectModel();
			refRightFragment.inizializeFragment(this, model);
			return refRightFragment;
		} catch (Exception e) {
			mLogger.error("Unable to creare RightFragment instance : %s", e.getMessage() );
			return null;
		}
	}
	
	
	// Riferimenti interni per i frameLayout dei fragment sx e dx
	private final int __LEFT_FRAGMENT_ID = jfabrix.lib.R.id.jfabrix101_leftFragment;
	private final int __RIGHT_FRAGMENT_ID = jfabrix.lib.R.id.jfabrix101_contentFragment;
	
	
	/**
	 * Restituisce l'ID del <code>FrameLayout</code> che rappresenta
	 * il fragment di sinista. 
	 * Nota: Se il layout non viene ridefinito (non viene ridefinito 
	 * il metodo <code>makeLandscapeLayout</code> )non è necessario 
	 * ridefinire questo metodo.
	 * @return - L'id (R.id.xxx) del <code>FrameLayout</code> utilizzato
	 * per il fragment di sinistra
	 */
	protected int getLeftFragmentId() { return __LEFT_FRAGMENT_ID; }
	
	
	/**
	 * Restituisce l'ID del <code>FrameLayout</code> che rappresenta
	 * il fragment di destra. 
	 * Nota: Se il layout non viene ridefinito (non viene ridefinito 
	 * il metodo <code>makeLandscapeLayout</code>) non è necessario 
	 * ridefinire questo metodo.
	 * @return - L'id (R.id.xxx) del <code>FrameLayout</code> utilizzato
	 * per il fragment di destra
	 */
	protected int getRightFragmentId() { return __RIGHT_FRAGMENT_ID; }
	
	
	
	// restituisce la percentuale (weight) delle dimensioni dei fragment di sinistra e destra
	// E' possibile effettuare l'override per cambiare le dimensioni di default
	protected float getLeftFragmentWeigth() { return 3.5f; }
	protected float getRightFragmentWeigth() { return 6.5f; }
	
	
	//  --- BEGIN ActionBar

	// Key to store the actual position of selected item
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "action_bar_selected_navigation_item";
	
	// List of labels for actionBar (can be null)
	private List<Object> actionBarItemList = null;
	
	// Actual position of selected item on actionBar
	private int mActionBarSelectedPosition = 0;
			
	//  --- END ActionBar
	
	
	/**
	 * Check if the portrait mode is supported.
	 * The default value verify the screen size. If the screen size is XLARGE or
	 * the portrait mode is supported, otherwise not. 
	 * 
	 * If this method return false, in portrait mode will be shown
	 * only the left fragment and clicking on an item, the activity will show 
	 * the right fragment at full screen. (Smartphone behaviour).
	 */
	protected boolean isPortraitFragmentModeSupported() {
		int screenLayout = ActivityHelper.getScreenLayoutType(this); 
		mLogger.trace("ScreenLayoutConfiguration : ", screenLayout);
		if (screenLayout >= Configuration.SCREENLAYOUT_SIZE_XLARGE) return true; 
		else return false; 
	}
	
	/**
	 * Return the layoutId for activity.
	 * @return If return an invalid value (<=0) will be create a default
	 * view for activity
	 */
	public abstract int getLayoutResourceId();
	
	
	/**
	 * Crea un layout in landscape utilizzando due fragment di peso proporzionale
	 * Per cambiare le dimensioni dei pesi effettuare l'override dei 
	 * metodi <code>getLeftFragmentSize</code> e <code>getRightFragmentSize</code> 
	 */
	@SuppressWarnings("all")
	protected View makeLandscapeLayout(LayoutInflater inflater) {
		if (getLayoutResourceId() > 0) {
			View v = LayoutInflater.from(this).inflate(getLayoutResourceId(), null);
			mVisualizationMode = VisualizationMode.LANDSCAPE;
			return v;
		} else {
			LinearLayout layout = new LinearLayout(this);
			layout.setPadding(10, 10, 10, 10);	
			layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			layout.setOrientation(LinearLayout.HORIZONTAL);
			
			FrameLayout leftFrame = new FrameLayout(this);
			leftFrame.setId(getLeftFragmentId());
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LayoutParams.FILL_PARENT, getLeftFragmentWeigth());
			leftFrame.setLayoutParams(lp);
			
			FrameLayout rightFrame = new FrameLayout(this);
			rightFrame.setId(getRightFragmentId());
			lp = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, getRightFragmentWeigth());
			rightFrame.setLayoutParams(lp);
			
			layout.addView(leftFrame); layout.addView(rightFrame);
			mVisualizationMode = VisualizationMode.LANDSCAPE;
			return layout;
		}
		
	}
	
	
	/**
	 * Crea un layout verticale utilizzando il solo fragment di sinistra (la lista)
	 */
	@SuppressWarnings("all")
	protected View makeLeftPortraitLayout(LayoutInflater inflater) {
		if (getLayoutResourceId() > 0) {
			View v = LayoutInflater.from(this).inflate(getLayoutResourceId(), null);
			View rightFragment = v.findViewById(getRightFragmentId());
			if (rightFragment != null) {
				rightFragment.setVisibility(View.GONE);
			}
			mVisualizationMode = VisualizationMode.PORTRAIT_ONLY_LEFT;
			return v;
		} else {
			LinearLayout layout = new LinearLayout(this);
		
			layout.setPadding(10, 10, 10, 10);
			layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			layout.setOrientation(LinearLayout.VERTICAL);
			
			FrameLayout leftFrame = new FrameLayout(this);
			leftFrame.setId(getLeftFragmentId());
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			leftFrame.setLayoutParams(lp);
			
			layout.addView(leftFrame); 
			mVisualizationMode = VisualizationMode.PORTRAIT_ONLY_LEFT;
			return layout;
		}
	}
	
	
	/**
	 * Crea un layout verticale utilizzando il solo fragment di destra (il dettaglio)
	 */
	@SuppressWarnings("all")
	protected View makeRightPortraitLayout(LayoutInflater inflater) {
		if (getLayoutResourceId() > 0) {
			View v = LayoutInflater.from(this).inflate(getLayoutResourceId(), null);
			View leftFragment = v.findViewById(getLeftFragmentId());
			if (leftFragment != null) {
				leftFragment.setVisibility(View.GONE);
			}
			mVisualizationMode = VisualizationMode.PORTRAIT_ONLY_RIGHT;
			getActionBar().setDisplayHomeAsUpEnabled(true); 
			return v;
		} else {
			LinearLayout layout = new LinearLayout(this);
		
			layout.setPadding(10, 10, 10, 10);	
			layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			layout.setOrientation(LinearLayout.VERTICAL);
			
			FrameLayout rightFrame = new FrameLayout(this);
			rightFrame.setId(getRightFragmentId());
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			rightFrame.setLayoutParams(lp);
			
			layout.addView(rightFrame);
			mVisualizationMode = VisualizationMode.PORTRAIT_ONLY_RIGHT;
			getActionBar().setDisplayHomeAsUpEnabled(true); 
			return layout;
		}
	}
	
	
	/**
	 * Metodo invocato durante la fase di creazione dell'activity, appena dopo
	 * aver impostato il layout.
	 * 
	 * In questo metodo bisogna creare l'objectModel per l'activity. Verificare se il
	 * <code>Bundle</code> passato come argomento è valido per costruire un objectModel
	 * (tipicamente a causa di una rotazione) oppure costruirne uno di default. 
	 * 	 * 
	 * @param savedInstanceState : If the activity is being re-initialized 
	 * 		after previously being shut down then this Bundle contains 
	 * 		the data it most recently supplied in onSaveInstanceState(Bundle). 
	 * 		Note: Otherwise it is null.
	 * 
	 * @return ObjectModel to setup.
	 */
	public abstract A createDefaultObjectModel(Bundle savedInstanceState);
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (outState == null) return;

		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, mActionBarSelectedPosition);
		mLogger.trace("onSaveInstanceState() - Saving params . %s", outState);
		
	}
	
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mLogger.trace("onRestoreInstanceState() - restoring Bundle: %s", savedInstanceState);
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			mActionBarSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM);
			if (actionBarItemList != null) getActionBar().setSelectedNavigationItem(mActionBarSelectedPosition);
		}
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mLogger.trace("onCreate() : Bundle =" + savedInstanceState);
				
		// Assegna i fragment all'activity
		super.onCreate(null);
		setObjectModel(createDefaultObjectModel(savedInstanceState));
		if (objectModel == null) mLogger.trace("ObjectModel set to null");
		
		boolean isLandscape = ActivityHelper.isLandscapeMode(this);				
		
		LayoutInflater inflater = LayoutInflater.from(this);
		mLogger.trace(" +-- Setting contentView. LandscapeMode = " + isLandscape);
		if (isLandscape || isPortraitFragmentModeSupported()) setContentView(makeLandscapeLayout(inflater)); 
		else setContentView(makeLeftPortraitLayout(inflater));
		
		VisualizationMode mVisualizationMode =  getVisualizationMode();
		switch (mVisualizationMode) {
			case LANDSCAPE:
				mLogger.trace(" +-- Creating fragments for left and right");
				replaceFragments(createLeftFragmentInstance(), createRightFragmentInstance());
				break;
	
			case PORTRAIT_ONLY_LEFT:
				mLogger.trace(" +-- Creating fragments only for left");
				replaceFragments(createLeftFragmentInstance(), null);
				break;
				
			case PORTRAIT_ONLY_RIGHT:
				mLogger.trace(" +-- Creating fragments only for right");
				replaceFragments(null, createRightFragmentInstance());
				break;
		}
	}
	
	
	public void setVisualizationMode(VisualizationMode vMode) {
		mVisualizationMode = vMode;
		LayoutInflater inflater = LayoutInflater.from(this);
		if (vMode == VisualizationMode.LANDSCAPE) {
			setContentView(makeLandscapeLayout(inflater));
			replaceFragments(createLeftFragmentInstance(), createRightFragmentInstance());
			return;
		}
		
		if (vMode == VisualizationMode.PORTRAIT_ONLY_LEFT) {
			setContentView(makeLeftPortraitLayout(inflater));
			replaceFragments(createLeftFragmentInstance(), null);
			return;
		}
		
		if (vMode == VisualizationMode.PORTRAIT_ONLY_RIGHT) {
			setContentView(makeRightPortraitLayout(inflater));
			replaceFragments(null, createRightFragmentInstance());
			return;
		}
	}
	
	
	@Override
	public void onStart() {
		super.onStart();
		mLogger.trace("onStart() - MasterDetailActivity");
	}
	
	
	public void createAndReplaceFragments() { createAndReplaceFragments(null); }
	
	
	public void createAndReplaceFragments(A model) {
		if (refLeftFragment != null) createLeftFragmentInstance(model);
		if (refRightFragment != null) createRightFragmentInstance(model);
		replaceFragments(refLeftFragment, refRightFragment);
	}
	
	/**
	 * Aggiorna contemporaneamente entrambi i fragment
	 */
	protected void replaceFragments(AbstractFragmentContent<A, ?> leftFragment, 
			AbstractFragmentContent<A, ?> rightFragments) {
		refLeftFragment = leftFragment;
		refRightFragment = rightFragments;
		
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		
		if (refLeftFragment != null)  {
			fragmentTransaction.replace(getLeftFragmentId(), (Fragment)refLeftFragment);
		}
		if (refRightFragment != null) {
			fragmentTransaction.replace(getRightFragmentId(), (Fragment)refRightFragment);
		}
		fragmentTransaction.commit();
	}

	
	protected void createAndReplaceRightFragment(A model) {
		refLeftFragment = null;
		createRightFragmentInstance(model);
		replaceFragments(refLeftFragment, refRightFragment);
	}
	
	
	protected void createAndReplaceLeftFragment(A model) {
		refRightFragment = null;
		createLeftFragmentInstance(model);
		replaceFragments(refLeftFragment, refRightFragment);
	}
	
	
	protected void resumeFragments() {
		mLogger.trace("resumeFragments()");
		if (refLeftFragment != null) {
			mLogger.trace("resumeFragments() - calling onResume on leftFragment");
			((Fragment)refLeftFragment).onResume();
		}
		if (refRightFragment != null) {
			mLogger.trace("resumeFragments() - calling onResume on rightFragment");
			((Fragment)refRightFragment).onResume();
		}
		mLogger.trace("resumeFragments() - Invalidating option menu!");
		invalidateOptionsMenu();
	}
	
		
	public A getObjectModel() { return objectModel; }
	public void setObjectModel(A item) { objectModel = item; } 
	
	/**
	 * Return the current visualizationMode
	 */
	public VisualizationMode getVisualizationMode() { return mVisualizationMode; }
	
	
	@Override
	protected void onPause() {
		mLogger.trace("onPause()");
		super.onPause();
	}
	
	

	/*
	 * Intercetta la pressione del tasto "Home" dell'actionBar per tornare
	 * alla visualizzazione della lista ma solo  se si è in  modalità 
	 * portrait e si sta visualizzando il dettaglio. 
	 */
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            	// Nel caso di visualizzazione in portrait e si sia nella visualizzazione 
            	// dettaglio, si torna alla visualizzazione della lista
            	if (mVisualizationMode == VisualizationMode.PORTRAIT_ONLY_RIGHT) {
            		getActionBar().setDisplayHomeAsUpEnabled(false); 
            		boolean processedEvent = onFragmentCommandEvent(refRightFragment, android.R.id.home, getObjectModel());
            		if (!processedEvent) mLogger.warn("Ignored message home event (%d) ", android.R.id.home);
            	}
                return true;
                
        }
        return super.onOptionsItemSelected(item);
    }
	
	
//	/**
//	 * Gestione dell'action BAR.
//	 * 
//	 * @return Restituisce la lista di oggetti da usare per l'actionBar. 
//	 * Come label per l'action bar viene utilizzato il metodo <code>toString()</code> dell'oggetto
//	 */
//	public abstract List<Object> getActionBarLabel();
//
//	public void initActionBar() {
//		
//		actionBarItemList = getActionBarLabel();
//		if (actionBarItemList == null || actionBarItemList.size() == 0) return;
//				
//		final ActionBar actionBar = getActionBar();
//		actionBar.setDisplayShowTitleEnabled(false);
//		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
//		
//		// Set up the dropdown list navigation in the action bar.
//		actionBar.setListNavigationCallbacks(
//				new ArrayAdapter<Object>(getActionBarThemedContextCompat(),
//						android.R.layout.simple_list_item_1,
//						android.R.id.text1, actionBarItemList), this);
//	}
//	
//	
//	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//	private Context getActionBarThemedContextCompat() {
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//			return getActionBar().getThemedContext();
//		} else {
//			return this;
//		}
//	}
//	
//	/**
//	 * Callback method to change the objectModel on selecting an item on actionBar
//	 * @param actionBarLabel
//	 * @return
//	 */
//	public abstract A onActionBarItemSelected(Object actionBarItem);
//	
//	/*
//	 * Manage the selection of items on actionBar
//	 */
//	@Override
//	public boolean onNavigationItemSelected(int position, long id) {
//		mLogger.trace("onNavigationItemSelected() - Position=%d. id=%d", position, id);
//		if (actionBarItemList == null) return false;
//		if (mActionBarSelectedPosition == position) {
//			mLogger.trace(" +-- Object already selected. Operation ignored");
//			return false;
//		}
//		
//		Object selectedItem = actionBarItemList.get(position);
//		A newObjectModel = onActionBarItemSelected(selectedItem);
//		if (newObjectModel == null) {
//			mLogger.trace(" +-- Invalid selected item (null) - Operation ignored");
//			return false;
//		}
//		if (newObjectModel.equals(objectModel)) {
//			mLogger.trace(" +-- Same item selected. Operatiokn ignored");
//			return false;
//		}
//		
//		// Change the objectModel and update fragments
//		setObjectModel(newObjectModel);
//		if (mVisualizationMode == VisualizationMode.LANDSCAPE) replaceFragments(createLeftFragmentInstance(), createRightFragmentInstance());
//		else {
//			if (refLeftFragment != null) replaceFragments(createLeftFragmentInstance(), null);
//			else replaceFragments(null, createRightFragmentInstance());
//		}
//		return true;
//	}
	
	
	
	
	// -------------------------------------------------------
	
	
	
	
}
