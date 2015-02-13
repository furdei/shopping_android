package com.furdey.shopping;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

/**
 * Created by Masya on 13.02.2015.
 */
public class ShoppingApplication extends Application {

    /**
     * Enum used to identify the tracker that needs to be used for tracking.
     *
     * A single tracker is usually enough for most purposes. In case you do need multiple trackers,
     * storing them all in Application object helps ensure that they are created only once per
     * application instance.
     */
    public enum TrackerName {
        APP_TRACKER // Tracker used only in this app.
    }

    private HashMap<TrackerName, Tracker> trackers = new HashMap<TrackerName, Tracker>();

    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!trackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t;
            
            if (trackerId == TrackerName.APP_TRACKER) {
                t = analytics.newTracker(R.xml.app_tracker);
            } else {
                throw new IllegalArgumentException("Unknown tracker name: " + trackerId.name());
            }
            
            trackers.put(trackerId, t);

        }
        
        return trackers.get(trackerId);
    }

    public void trackViewScreen(String screen) {
        // Get tracker.
        Tracker t = getTracker(TrackerName.APP_TRACKER);

        // Set screen name.
        // Where path is a String representing the screen name.
        t.setScreenName(screen);

        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    public void trackViewScreen(Class<?> screenClass) {
        trackViewScreen(screenClass.getCanonicalName());
    }

    public void trackEvent(String category, String action, String label) {
        Tracker t = getTracker(TrackerName.APP_TRACKER);
        t.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }

    public void trackEvent(int category, int action, String label) {
        trackEvent(getString(category), getString(action), label);
    }

    public void trackEvent(String category, String action) {
        trackEvent(category, action, null);
    }

    public void trackEvent(int category, int action) {
        trackEvent(getString(category), getString(action));
    }
}
