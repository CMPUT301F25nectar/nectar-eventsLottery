package com.example.beethere;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

import java.util.Collections;

public class NotificationHandler {
    private String lotteryResultsChannelID = "lottery_results_channel";
    private String organizerChannelID = "organizer_updates_channel";
    private String adminChannelID = "admin_alerts_channel";

    //stores references to the app
    private Context context;
    private FirebaseFirestore firestore;

    public NotificationHandler(Context context){
        this.context = context;
        this.firestore = FirebaseFirestore.getInstance();
        createNotificationChannels(); // automatically creates all 3 channels
    }

    private void createNotificationChannels(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){

            //Lottery_Results_Channel
            CharSequence lotteryName = "Lottery Results";
            String lotteryDescription = "Notifications about lottery selection results";
            int lotteryImportance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel lotteryChannel = new NotificationChannel(lotteryResultsChannelID, lotteryName, lotteryImportance);
            lotteryChannel.setDescription(lotteryDescription);

            // Organizer_Updates_Channel
            CharSequence organizerName = "Event Updates";
            String organizerDescription = "Updates about your events and entrants' acceptance/declines";
            int organizerImportance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel organizerChannel = new NotificationChannel(organizerChannelID, organizerName, organizerImportance);
            organizerChannel.setDescription(organizerDescription);

            // Admin_Alerts_Channel
            CharSequence adminName  = "Admin Alerts";
            String adminDescription= "Important Notifications for Admin to review";
            int adminImportance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel adminChannel = new NotificationChannel(adminChannelID, adminName , adminImportance);
            adminChannel.setDescription(adminDescription);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannels(Collections.singletonList(lotteryChannel));
            notificationManager.createNotificationChannels(Collections.singletonList(organizerChannel));
            notificationManager.createNotificationChannels(Collections.singletonList(adminChannel));

        }
    }

    public void sendWinnerNotifications(String deviceid, String title, String eventId){
        checkUserPreferencesAndSend(deviceid, () -> {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            //adding action button to open EventDetailsActivity when tapped
            Intent intent = new Intent(context, EventDetailsActivity.class);
            intent.putExtra("EVENT_ID", eventId);
            intent.putExtra("FROM_NOTIFICATION", true);
            //intent.setFlags()

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        );

        //build the notification, priority, contentintent , set auto cancel true
    }



}
