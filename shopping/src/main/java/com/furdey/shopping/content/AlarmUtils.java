package com.furdey.shopping.content;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.furdey.shopping.R;
import com.furdey.shopping.notifications.PurchasesListAlarmNotificationService;
import com.furdey.shopping.utils.PreferencesManager;

import java.util.Calendar;

/**
 * Created by Masya on 27.09.2014.
 */
public class AlarmUtils {

    public static void setAlarmTime(Context context, int hour, int minute, int repeat) {
        Calendar now = Calendar.getInstance();
        Calendar firstGoOff = Calendar.getInstance();
        firstGoOff.set(Calendar.HOUR_OF_DAY, hour);
        firstGoOff.set(Calendar.MINUTE, minute);

        if (firstGoOff.before(now)) {
            firstGoOff.add(Calendar.DATE, 1);
        }

        Intent intent = new Intent(context, PurchasesListAlarmNotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 1, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        if (repeat > 0) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, firstGoOff.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, firstGoOff.getTimeInMillis(),
                    pendingIntent);
        }

        PreferencesManager.setAlarmTime(context, hour, minute, repeat);
    }

    public static boolean[] getRepeatArray(int repeat) {
        boolean[] result = new boolean[7];

        for (int i = 0; i < 7; i++) {
            result[i] = isSelected(repeat, i);
        }

        return result;
    }

    public static boolean isSelected(int repeat, int day) {
        int mask = 1 << day;
        int isRepeated = repeat & mask;
        return isRepeated != 0;
    }

    public static String getRepeatString(Context context, int repeat) {
        if (repeat == 0) {
            return context.getString(R.string.alertTimerDialogRepeatNever);
        }

        String[] daysOfWeek = context.getResources().getStringArray(R.array.daysOfWeek);
        String[] daysOfWeekShort = context.getResources().getStringArray(R.array.daysOfWeekShort);

        String selectedString = "";
        String lastSelectedString = null;
        String delimiter = context.getString(R.string.alertTimerDialogDaysDelimiter);
        int selectedCount = 0;

        for (int i = 0; i < 7; i++) {
            if (isSelected(repeat, i)) {
                if (lastSelectedString != null) {
                    selectedString = selectedString + delimiter;
                }

                selectedString = selectedString + daysOfWeekShort[i];
                lastSelectedString = daysOfWeek[i];
                selectedCount++;
            }
        }

        return selectedCount > 1 ? selectedString : lastSelectedString;
    }
}
