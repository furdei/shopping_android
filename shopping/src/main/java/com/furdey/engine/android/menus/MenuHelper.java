package com.furdey.engine.android.menus;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;

import com.furdey.engine.android.activities.DataLinkActivity;

/**
 * Use this helper class to bind the activity to the options menu, as in the example:
 * <code>MenuHelper.bindActivityToOptionsMenu(R.id.menu_units, UnitsListActivity.class);</code>
 * 
 * <p/>See MenuHelper.bindActivityToOptionsMenu for details
 * @see MenuHelper#bindActivityToOptionsMenu
 *
 * @author Stepan Furdey
 */
public class MenuHelper {
	
	private static final String TAG = MenuHelper.class.getSimpleName();

	private static volatile MenuHelper instance;

	private SparseArray<ActivityHolder> optionsMenuActivities;
	private Map<Class<? extends DataLinkActivity<?>>, Integer> optionsMenuActivitiesTransp;
	
	private MenuHelper() {
		optionsMenuActivities = new SparseArray<ActivityHolder>();
		optionsMenuActivitiesTransp = new HashMap<Class<? extends DataLinkActivity<?>>, Integer>();
	}
	
	public static MenuHelper getInstance() {
		if (instance == null)
			synchronized (MenuHelper.class) {
				if (instance == null)
					instance = new MenuHelper();
			}

		return instance;
	}
	
	/**
	 * Binds an activity to the menu. When bound and the user selects the menu item
	 * <code>menuItemId</code> then the engine will automatically create the <code>activity</code>
	 * and starts it.
	 * 
	 * <p/>You can specify some extra parameters to pass to the newly created activity
	 * 
	 * <p/>Another helpful feature is that when the <code>activity</code> is shown, it's
	 * corresponding menu item becomes hidden to prevent cycled calls. 
	 * 
	 * @param menuItemId
	 * @param activity
	 * @param extras
	 */
	public static void bindActivityToOptionsMenu(Integer menuItemId, Class<? extends DataLinkActivity<?>> activityClass, Bundle extras) {
		if (MenuHelper.getInstance().optionsMenuActivities.indexOfKey(menuItemId) >= 0)
			throw new RuntimeException("Some activity has been already bound to menu item ".concat(Integer.toString(menuItemId)));
		
		if (MenuHelper.getInstance().optionsMenuActivitiesTransp.containsKey(activityClass))
			throw new RuntimeException("Some menu key has been already bound to activity ".concat(activityClass.getSimpleName()));
		
		ActivityHolder holder = new ActivityHolder();
		holder.setClazz(activityClass);
		holder.setParameters(extras);
		
		MenuHelper.getInstance().optionsMenuActivities.put(menuItemId, holder);
		MenuHelper.getInstance().optionsMenuActivitiesTransp.put(activityClass, menuItemId);
	}
	
	/**
	 * Binds an activity to the menu. When bound and the user selects the menu item
	 * <code>menuItemId</code> then the engine will automatically create the <code>activity</code>
	 * and starts it.
	 * 
	 * <p/>Another helpful feature is that when the <code>activity</code> is shown, it's
	 * corresponding menu item becomes hidden to prevent cycled calls. 
	 * 
	 * @param menuItemId
	 * @param activity
	 */
	public static void bindActivityToOptionsMenu(Integer menuItemId, Class<? extends DataLinkActivity<?>> activityClass) {
		bindActivityToOptionsMenu(menuItemId, activityClass, null);
	}
	
	public static void unbindActivityFromOptionsMenu(Integer menuItemId, Class<? extends DataLinkActivity<?>> activityClass) {
		if (MenuHelper.getInstance().optionsMenuActivities.indexOfKey(menuItemId) < 0)
			throw new RuntimeException("No any activity has been bound to menu item ".concat(Integer.toString(menuItemId)));
		
		if (!MenuHelper.getInstance().optionsMenuActivitiesTransp.containsKey(activityClass))
			throw new RuntimeException("No any menu key has been bound to activity ".concat(activityClass.getSimpleName()));
		
		MenuHelper.getInstance().optionsMenuActivities.remove(menuItemId);
		MenuHelper.getInstance().optionsMenuActivitiesTransp.remove(activityClass);
	}

	public static ActivityHolder getOptionsMenuActivityForId(Integer id) {
		return MenuHelper.getInstance().optionsMenuActivities.get(id);
	}
	
	public static Integer getOptionsMenuIdForActivity(Class<? extends DataLinkActivity<?>> activity) {
		Log.d(TAG, MenuHelper.getInstance().optionsMenuActivitiesTransp == null ? "optionsMenuActivitiesTransp is null" : "optionsMenuActivitiesTransp is NOT null");
		Log.d(TAG, activity == null ? "activity is null" : "activity is NOT null");
		return MenuHelper.getInstance().optionsMenuActivitiesTransp.get(activity);
	}
	
}
