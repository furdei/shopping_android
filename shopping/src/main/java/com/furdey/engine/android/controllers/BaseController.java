package com.furdey.engine.android.controllers;

import java.sql.SQLException;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;

import com.furdey.engine.android.activities.DataLinkActivity;
import com.furdey.engine.android.utils.LogicException;
import com.furdey.engine.android.utils.Settings;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

public class BaseController<Model, H extends OrmLiteSqliteOpenHelper> {
	
	public static final String OBJECT_TO_EDIT_PARAM_NAME = "com.furdey.engine.android.controllers.BaseController.objectToEdit";	
	public static final String STATE_PARAM_NAME = "com.furdey.engine.android.controllers.BaseController.state";
	
	public static final int STATE_UNSTATED = 0;
	public static final int STATE_GRID = 1;
	public static final int STATE_FORM_ADD = 2;
	public static final int STATE_FORM_EDIT = 3;
	public static final int STATE_DELETE = 4;

	private int state;
	private DataLinkActivity<H> activity;
	private Model model;
	private OnCursorLoadListener onCursorLoadListener;
	
	public BaseController(DataLinkActivity<H> activity) {
		this.setActivity(activity);
		this.setState(activity.getIntent().getIntExtra(STATE_PARAM_NAME, STATE_UNSTATED));
	}

	/**
	 * Use this listener to make a button that closes the activity
	 * with the result RESULT_CANCELED.
	 */
	public final OnClickListener getCancelButtonOnClickListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				activity.setResult(DataLinkActivity.RESULT_CANCELED);
				activity.finish();
			}
		};
	}

	/**
	 * Use this listener to make a button that starts a background
	 * thread that stores something in the database. After saving
	 * is completed this listener closes the activity with the 
	 * result RESULT_OK.
	 * 
	 * <p/>Note that the only two states are allowed when this
	 * listeners fires: STATE_FORM_ADD and STATE_FORM_EDIT.
	 */
	public final OnClickListener getSaveButtonOnClickListener(final OnSaveCompleteListener listener) {
		return new OnClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onClick(View arg0) {
				Model model = null; 

				switch (state) {
				case STATE_FORM_ADD:
					model = onBeforeInsert();
					break;
					
				case STATE_FORM_EDIT:
					model = onBeforeUpdate();
					break;
					
				default:
					throw new IllegalStateException(String.format("State %d is not allowed for the getSaveButtonOnClickListener", state));
				}
				
				new AsyncTask<Model, Void, Intent>() {
					@Override
					protected Intent doInBackground(Model... model) {
						try {
							switch (state) {
							case STATE_FORM_ADD:
								return onInsert(model[0]);
								
							case STATE_FORM_EDIT:
								return onUpdate(model[0]);
								
							default:
								throw new IllegalStateException(String.format("State %d is not allowed for the getSaveButtonOnClickListener", state));
							}
							
						} catch (Exception e) {
							throw new LogicException(activity, Settings.getInstance().getUnknownError(), e);
						}
					}
					
				    @Override
				    protected void onPostExecute(Intent result) {
				    	if (listener != null)
				    		listener.onSaveComplete(result);
				    	else {
							activity.setResult(DataLinkActivity.RESULT_OK, result);
							activity.finish();
				    	}
				    }
					
				}.execute(model);
			}
		};
	}
	
	/**
	 * Call this method to load a model by the ID that was supplied to
	 * the corresponding activity using OBJECT_TO_EDIT_ID_PARAM_NAME
	 * parameter of the Intent. Model loading is performed in the
	 * background thread through calling loadModelById method and then
	 * Model is passed to the loading event listener which is called
	 * in the UI thread. Loaded model is also stored in the controller's
	 * model property. 
	 */
/*	@SuppressWarnings("unchecked")
	public final void loadModelToEdit(OnModelLoadListener<Model> onLoadComplete) {
		new AsyncTask<OnModelLoadListener<Model>, Void, Model>() {
			private OnModelLoadListener<Model> onLoadComplete;
			
			@Override
			protected Model doInBackground(OnModelLoadListener<Model>... param) {
				onLoadComplete = param[0];
				Integer id = getActivity().getIntent().getExtras().getInt(OBJECT_TO_EDIT_PARAM_NAME);

				if (id == null)
					throw new IllegalStateException("Parameter ".concat(OBJECT_TO_EDIT_PARAM_NAME).concat(" is required for action ").concat(Integer.toString(getState())));
				
				try {
					return onSelectById(id);
				} catch (SQLException e) {
					throw new LogicException(getActivity(), Settings.getInstance().getUnknownError(), e);
				}
			}
			
		    @Override
		    protected void onPostExecute(Model model) {
		    	setModel(model);
		    	onLoadComplete.onLoadComplete(model);
		    }
			
		}.execute(onLoadComplete);
	}*/
	
	/**
	 * Call this method to tell the controller that there is some 
	 * list control in the activity and we are ready to pass the
	 * cursor with our models to that control.
	 */
	public void registerCursorLoadListener(OnCursorLoadListener onCursorLoadListener) {
		this.onCursorLoadListener = onCursorLoadListener;
		//refreshCursor();
	}
	
	/**
	 * Call this method to extract model data to the cursor from
	 * a database or some other storage
	 */
	public final void refreshCursor() {
		new AsyncTask<Void, Void, Cursor>() {
			@Override
			protected Cursor doInBackground(Void... param) {
				try {
					return onSelect();
				} catch (SQLException e) {
					throw new LogicException(getActivity(), Settings.getInstance().getUnknownError(), e);
				}
			}
			
		    @Override
		    protected void onPostExecute(Cursor cursor) {
		    	onCursorLoadListener.onLoadComplete(cursor);
		    }
			
		}.execute();		
	}
	
	/**
	 * Call this method to load some cursor and do some tasks
	 * on UI thread after the cursor is loaded.
	 */
	public void loadCursor(CursorLoader cursorLoader, final OnCursorLoadListener onCursorLoadListener) {
		new AsyncTask<CursorLoader, Void, Cursor>() {
			@Override
			protected Cursor doInBackground(CursorLoader... cursorLoader) {
				try {
					return cursorLoader[0].loadCursor();
				} catch (SQLException e) {
					throw new LogicException(getActivity(), Settings.getInstance().getUnknownError(), e);
				}
			}
			
		    @Override
		    protected void onPostExecute(Cursor cursor) {
		    	onCursorLoadListener.onLoadComplete(cursor);
		    }
			
		}.execute(cursorLoader);		
	}
	
	/**
	 * Call this method to delete something from database or other storage
	 * using object's id to find an object to delete. This will start
	 * background thread. To perform actually delete operation itself
	 * override onDelete method.
	 */
	public final void delete(Integer id) {
		new AsyncTask<Integer, Void, Void>() {

			@Override
			protected Void doInBackground(Integer... params) {
				try {
					onDelete(params[0]);
				} catch (SQLException e) {
					throw new LogicException(getActivity(), Settings.getInstance().getUnknownError(), e);
				}
				
				return null;
			}
			
		    @Override
		    protected void onPostExecute(Void result) {
		    	refreshCursor();
		    }			
		}.execute(id);
	}
	
	/**
	 * Override this method to perform some actions <b>on the UI
	 * thread<b> before calling the onInsert method in the
	 * background. The main purpose of the onBeforeInsert method 
	 * is to construct model object from some UI controls. 
	 */
	protected Model onBeforeInsert() {
		return null;
	}
	
	/**
	 * Override this method to perform the storing of a new object
	 * or record in the database or somewhere else. 
	 * 
	 * <p/>Note that this method is called in the background thread, 
	 * not in the UI thread.
	 * 
	 * @param model - object created with the onBeforeInsert method
	 */
	protected Intent onInsert(Model model) throws SQLException {
		return null;
	}
	
	/**
	 * Override this method to perform some actions <b>on the UI
	 * thread<b> before calling the onUpdate method in the
	 * background. The main purpose of the onBeforeUpdate method 
	 * is to construct model object from some UI controls. 
	 */
	protected Model onBeforeUpdate() {
		return null;
	}
	
	/**
	 * Override this method to perform the storing of a changed
	 * (edited) object or record in the database or somewhere else. 
	 * 
	 * <p/>Note that this method is called in the background thread, 
	 * not in the UI thread.
	 */
	protected Intent onUpdate(Model model) throws SQLException {
		return null;
	}
	
	/**
	 * Override this method to perform model loading to display
	 * in the form. 
	 * 
	 * <p/>Note that this method is called in the background thread, 
	 * not in the UI thread.
	 */
/*	protected Model onSelectById(int id) throws SQLException {
		return null;
	}*/
	
	/**
	 * Override this method to perform model loading into the
	 * cursor to attach it to some list control.
	 * 
	 * <p/>Note that this method is called in the background thread, 
	 * not in the UI thread.
	 */
	protected Cursor onSelect() throws SQLException {
		return null;
	}
	
	/**
	 * Override this method to perform model deleting operation
	 * based on model's ID.
	 * 
	 * <p/>Note that this method is called in the background thread, 
	 * not in the UI thread.
	 */
	protected void onDelete(Integer id) throws SQLException  {
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	
	public DataLinkActivity<H> getActivity() {
		return activity;
	}

	private void setActivity(DataLinkActivity<H> activity) {
		this.activity = activity;
	}
	
	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

}
