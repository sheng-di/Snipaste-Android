package com.to3g.snipasteandroid.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.to3g.snipasteandroid.QDMainActivity;
import com.to3g.snipasteandroid.R;

public class NotificationService extends Service {
    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, ScreenshotService.class);
        PendingIntent pendingIntent =
                PendingIntent.getService(this, 0, notificationIntent, 0);

        NotificationManager notificationManager= (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        NotificationChannel channel = null;
        String CHANNEL_DEFAULT_IMPORTANCE = "foreground";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_DEFAULT_IMPORTANCE, "foregroundName", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification =
                new Notification.Builder(this, CHANNEL_DEFAULT_IMPORTANCE)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.notification_content))
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentIntent(pendingIntent)
                        .setTicker(getString(R.string.notification_content))
                        .build();

        startForeground(1, notification);
        return super.onStartCommand(intent, flags, startId);
    }
}
