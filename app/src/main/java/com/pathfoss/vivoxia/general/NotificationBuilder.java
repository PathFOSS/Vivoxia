package com.pathfoss.vivoxia.general;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class NotificationBuilder extends Application {

    public static final String CHANNEL_ID = "Download Progress";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    // Create method to generate notification channels
    private void createNotificationChannels(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,"Download Notification", android.app.NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Show progress of downloading");
            getSystemService(NotificationManager.class).createNotificationChannel(notificationChannel);
        }
    }
}
