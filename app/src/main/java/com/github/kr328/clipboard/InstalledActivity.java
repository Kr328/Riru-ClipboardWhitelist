package com.github.kr328.clipboard;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

public class InstalledActivity extends Activity {
    private static final String NOTIFICATION_CHANNEL = "installation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NotificationChannel channel = new NotificationChannel(
                NOTIFICATION_CHANNEL,
                getString(R.string.installation),
                NotificationManager.IMPORTANCE_DEFAULT
        );

        getSystemService(NotificationManager.class)
                .createNotificationChannel(channel);

        try {
            Service.getService();

            notifyInstalled();
        } catch (Exception e) {
            notifyException();
        } finally {
            finish();
        }
    }

    private void notifyInstalled() {
        notify(R.string.notification_installed, R.string.notification_installed_content);
    }

    private void notifyException() {
        notify(R.string.notification_exception, R.string.notification_exception_content);
    }

    private void notify(int title, int content) {
        PendingIntent intent = PendingIntent.getActivity(
                this,
                114514,
                new Intent(this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        Notification notification = new Notification.Builder(this, NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.ic_notify)
                .setContentTitle(getString(title))
                .setContentText(getString(content))
                .setContentIntent(intent)
                .setAutoCancel(true)
                .setStyle(new Notification.BigTextStyle().bigText(getString(content)))
                .build();

        getSystemService(NotificationManager.class).notify(114514, notification);
    }
}
