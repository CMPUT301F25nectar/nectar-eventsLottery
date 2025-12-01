package com.example.beethere.notifications_classes;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class BeeThereChannel extends Application {
    public static String CHANNEL_ID = "beethere_lottery_channel";
    @Override
    public void onCreate(){
        super.onCreate();
        createNotificationChannel();
    }
    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Lottery Notifications";
            String description = "Notifications for lottery results, event updates, and organizer messages";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
