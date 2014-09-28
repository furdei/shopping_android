package com.furdey.shopping.notifications;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.furdey.shopping.R;
import com.furdey.shopping.activities.PurchasesActivity;
import com.furdey.shopping.content.AlarmUtils;
import com.furdey.shopping.content.PurchasesUtils;
import com.furdey.shopping.utils.PreferencesManager;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Calendar;

public class PurchasesListAlarmNotificationService extends IntentService {

    private static final int MAX_NOTIFICATION_LENGTH = 60;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public PurchasesListAlarmNotificationService() {
        super(PurchasesListAlarmNotificationService.class.getSimpleName());
    }

    /**
     * Check if there is a list to remind about
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        int repeat = PreferencesManager.getAlarmRepeat(getApplicationContext());
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 2;
        dayOfWeek = dayOfWeek < 0 ? 6 : dayOfWeek;

        if (!AlarmUtils.isSelected(repeat, dayOfWeek) && repeat > 0) {
            return;
        }

        String notificationText = PurchasesUtils.getPurchasesListNotificationString(
                getApplicationContext(), MAX_NOTIFICATION_LENGTH);

        if (notificationText != null) {
            // Start the main activity when user clicks a notification
            Intent showPurchasesListIntent = new Intent(this, PurchasesActivity.class);
            showPurchasesListIntent.putExtra(PurchasesActivity.MODE_PARAMETER,
                    PurchasesActivity.Mode.PURCHASES_LIST.toString());
            PendingIntent showPurchasesListPendingIntent = PendingIntent.getActivity(this,
                    0, showPurchasesListIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder builder =
                    new Notification.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                            .setContentTitle(getString(R.string.purchasesLiSendListNotificationHeader))
                            .setContentText(notificationText)
                            .setContentIntent(showPurchasesListPendingIntent)
                            .setAutoCancel(true);

            NotificationManager notifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Notification notification;

            if (android.os.Build.VERSION.SDK_INT < 16)
                notification = builder.getNotification();
            else
                notification = builder.build();

            notifyMgr.notify(1, notification);

            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            try {
                AssetFileDescriptor assetFileDescriptor = getResources().
                        openRawResourceFd(R.raw.alert_sound1);
                FileDescriptor fileDescriptor = assetFileDescriptor.getFileDescriptor();
                mediaPlayer.setDataSource(fileDescriptor, assetFileDescriptor.getStartOffset(),
                        assetFileDescriptor.getLength());
                assetFileDescriptor.close();
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
