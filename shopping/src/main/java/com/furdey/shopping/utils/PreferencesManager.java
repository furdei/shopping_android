package com.furdey.shopping.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.furdey.shopping.contentproviders.PurchasesContentProvider;

public class PreferencesManager {
    private static final String APP_PREFERENCES = "com.furdey.shopping";
    private static final String PREF_LAST_RUN_DATE = "lastRunDate";
    private static final String PREF_PURCHASES_SORT_ORDER = "purchasesSortOrder";
    private static final String PREF_SHARE_STATE = "shareState";
    private static final String PREF_SHARE_CHECK_DATE = "shareCheckDate";
    private static final String PREF_SHARE_CHECK_PERIOD = "shareCheckPeriod";
    private static final String PREF_LIKE_STATE = "likeState";
    private static final String PREF_LIKE_CHECK_DATE = "likeCheckDate";
    private static final String PREF_LIKE_CHECK_PERIOD = "likeCheckPeriod";
    private static final String PREF_RUN_COUNT = "runCount";
    private static final String PREF_ALARM_HOUR = "alarmHour";
    private static final String PREF_ALARM_MINUTE = "alarmMinute";
    private static final String PREF_ALARM_REPEAT = "alarmRepeat";
    private static final String PREF_LANGUAGE = "language";
    private static final String PREF_WIDGET_TRACKED = "widgetTracked";
    private static final String PREF_ALARM_TRACKED = "alarmTracked";
    private static final String PREF_SEND_LIST_TRACKED = "sendListTracked";
    private static final String PREF_SHARE_TRACKED = "shareTracked";

    public static final long SHARE_RUN_THRESHOLD = 5;
    public static final long SHARE_DELAY = 1000 * 3600 * 24 * 7;
    public static final long SHARE_LATER_DELAY = 1000 * 3600 * 24 * 2;
    public static final long LIKE_RUN_THRESHOLD = 10;
    public static final long LIKE_DELAY = 1000 * 3600 * 24 * 7;
    public static final long LIKE_LATER_DELAY = 1000 * 3600 * 24 * 2;
    public static final int ALARM_HOUR_UNSPECIFIED = -1;
    public static final String DEF_LANGUAGE = "en";

    public enum ShareState {
        NOT_SHARED, SHARED
    }

    ;

    public enum LikeState {
        NOT_LIKED, LIKED
    }

    ;

    public enum PurchasesSortOrder {
        ORDER_BY_CATEGORY(PurchasesContentProvider.Columns.GOODSCATEGORY_NAME.getDbName() +
                ", " + PurchasesContentProvider.Columns.GOODS_NAME.getDbName()),
        ORDER_BY_NAME(PurchasesContentProvider.Columns.GOODS_NAME.getDbName()),
        ORDER_BY_ADD_TIME(PurchasesContentProvider.Columns.STRDATE.getDbName());

        private String sortOrder;

        private PurchasesSortOrder(String sortOrder) {
            this.sortOrder = sortOrder;
        }

        public String getSortOrder() {
            return sortOrder;
        }
    }

    ;

    private static SharedPreferences loadPreferences(Context context) {
        return context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public static String getLastRunDate(Context context) {
        return loadPreferences(context).getString(PREF_LAST_RUN_DATE, "");
    }

    public static void setLastRunDate(Context context, String lastRunDate) {
        Editor e = loadPreferences(context).edit();
        e.putString(PREF_LAST_RUN_DATE, lastRunDate);
        e.commit();
    }

    public static PurchasesSortOrder getPurchasesSortOrder(Context context) {
        return PurchasesSortOrder.valueOf(loadPreferences(context).getString(PREF_PURCHASES_SORT_ORDER,
                PurchasesSortOrder.ORDER_BY_CATEGORY.toString()));
    }

    public static void setPurchasesSortOrder(Context context, PurchasesSortOrder purchasesSortOrder) {
        Editor e = loadPreferences(context).edit();
        e.putString(PREF_PURCHASES_SORT_ORDER, purchasesSortOrder.toString());
        e.commit();
    }

    public static boolean isOkToShare(Context context) {
        long date = loadPreferences(context).getLong(PREF_SHARE_CHECK_DATE, 0);
        long period = loadPreferences(context).getLong(PREF_SHARE_CHECK_PERIOD, 0);

        if (date == 0) {
            // Первый запуск, установим интервал проверки
            date = System.currentTimeMillis();
            period = SHARE_DELAY;
            setShareDelay(context, date, period);
            return false;
        }

        return (System.currentTimeMillis() > (date + period))
                && (getRunCount(context) >= SHARE_RUN_THRESHOLD);
    }

    public static void setShareDelay(Context context, long date, long period) {
        Editor e = loadPreferences(context).edit();
        e.putLong(PREF_SHARE_CHECK_DATE, date);
        e.putLong(PREF_SHARE_CHECK_PERIOD, period);
        e.commit();
    }

    public static ShareState getShareState(Context context) {
        String shareStateStr = loadPreferences(context).getString(PREF_SHARE_STATE, null);
        ShareState shareState;

        if (shareStateStr == null)
            shareState = ShareState.NOT_SHARED;
        else
            shareState = ShareState.valueOf(shareStateStr);

        return shareState;
    }

    public static void setShareState(Context context, ShareState shareState) {
        Editor e = loadPreferences(context).edit();
        e.putString(PREF_SHARE_STATE, shareState.toString());
        e.commit();
    }

    public static boolean isOkToLike(Context context) {
        long date = loadPreferences(context).getLong(PREF_LIKE_CHECK_DATE, 0);
        long period = loadPreferences(context).getLong(PREF_LIKE_CHECK_PERIOD, 0);

        if (date == 0) {
            // Первый запуск, установим интервал проверки
            date = System.currentTimeMillis();
            period = LIKE_DELAY;
            setLikeDelay(context, date, period);
            return false;
        }

        return (System.currentTimeMillis() > (date + period))
                && (getRunCount(context) >= LIKE_RUN_THRESHOLD);
    }

    public static void setLikeDelay(Context context, long date, long period) {
        Editor e = loadPreferences(context).edit();
        e.putLong(PREF_LIKE_CHECK_DATE, date);
        e.putLong(PREF_LIKE_CHECK_PERIOD, period);
        e.commit();
    }

    public static LikeState getLikeState(Context context) {
        String likeStateStr = loadPreferences(context).getString(PREF_LIKE_STATE, null);
        LikeState likeState;

        if (likeStateStr == null)
            likeState = LikeState.NOT_LIKED;
        else
            likeState = LikeState.valueOf(likeStateStr);

        return likeState;
    }

    public static void setLikeState(Context context, LikeState likeState) {
        Editor e = loadPreferences(context).edit();
        e.putString(PREF_LIKE_STATE, likeState.toString());
        e.commit();
    }

    public static void setRunCount(Context context, long runCount) {
        Editor e = loadPreferences(context).edit();
        e.putLong(PREF_RUN_COUNT, runCount);
        e.commit();
    }

    public static long getRunCount(Context context) {
        return loadPreferences(context).getLong(PREF_RUN_COUNT, 0);
    }

    public static void setAlarmTime(Context context, int hour, int minute, int repeat) {
        Editor e = loadPreferences(context).edit();
        e.putInt(PREF_ALARM_HOUR, hour);
        e.putInt(PREF_ALARM_MINUTE, minute);
        e.putInt(PREF_ALARM_REPEAT, repeat);
        e.commit();
    }

    public static int getAlarmHour(Context context) {
        return loadPreferences(context).getInt(PREF_ALARM_HOUR, ALARM_HOUR_UNSPECIFIED);
    }

    public static int getAlarmMinute(Context context) {
        return loadPreferences(context).getInt(PREF_ALARM_MINUTE, 0);
    }

    public static int getAlarmRepeat(Context context) {
        return loadPreferences(context).getInt(PREF_ALARM_REPEAT, 0);
    }

    public static String getLanguage(Context context) {
        return loadPreferences(context).getString(PREF_LANGUAGE, DEF_LANGUAGE);
    }

    public static void setLanguage(Context context, String language) {
        Editor e = loadPreferences(context).edit();
        e.putString(PREF_LANGUAGE, language);
        e.commit();
    }

    public static boolean isWidgetTracked(Context context) {
        return loadPreferences(context).getBoolean(PREF_WIDGET_TRACKED, false);
    }

    public static void setWidgetTracked(Context context, boolean tracked) {
        Editor e = loadPreferences(context).edit();
        e.putBoolean(PREF_WIDGET_TRACKED, tracked);
        e.commit();
    }

    public static boolean isAlarmTracked(Context context) {
        return loadPreferences(context).getBoolean(PREF_ALARM_TRACKED, false);
    }

    public static void setAlarmTracked(Context context, boolean tracked) {
        Editor e = loadPreferences(context).edit();
        e.putBoolean(PREF_ALARM_TRACKED, tracked);
        e.commit();
    }

    public static boolean isSendListTracked(Context context) {
        return loadPreferences(context).getBoolean(PREF_SEND_LIST_TRACKED, false);
    }

    public static void setSendListTracked(Context context, boolean tracked) {
        Editor e = loadPreferences(context).edit();
        e.putBoolean(PREF_SEND_LIST_TRACKED, tracked);
        e.commit();
    }

    public static boolean isShareTracked(Context context) {
        return loadPreferences(context).getBoolean(PREF_SHARE_TRACKED, false);
    }

    public static void setShareTracked(Context context, boolean tracked) {
        Editor e = loadPreferences(context).edit();
        e.putBoolean(PREF_SHARE_TRACKED, tracked);
        e.commit();
    }

}
