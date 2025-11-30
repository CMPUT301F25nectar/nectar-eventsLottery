package com.example.beethere.notifications_classes;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.beethere.DatabaseFunctions;
import com.example.beethere.MainActivity;
import com.example.beethere.R;
import com.example.beethere.device.DeviceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import android.content.pm.PackageManager;
import android.os.Build;

public class NotificationFirebaseMessagingService extends FirebaseMessagingService {
    private DatabaseFunctions dbFunctions;
//    @Override
//    public void onCreate(){
//        super.onCreate();
//        dbFunctions = new DatabaseFunctions();
//
    @Override
    public void onNewToken(@NonNull String token){
        super.onNewToken(token);
        Log.d("fcm", "New FCM token generated:" + token);

        String deviceId = DeviceId.get(this);
        DatabaseFunctions db = new DatabaseFunctions();
        db.saveFCMToken(deviceId, token);
        Log.d("fcm","new token saved to now firestore");
    }
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        String title = "BeeThere Notification";
        String body = "";
        if (message.getNotification() != null) {
            title = message.getNotification().getTitle();
            body = message.getNotification().getBody();
        }
        String type = message.getData().get("type");
        String eventId = message.getData().get("eventId");

        Log.d("fcm", "title" + title);
        Log.d("fcm", "event id:" + eventId);

        showNotification(title, body, eventId);
    }
    public void showNotification(String title, String body, String eventId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!= PackageManager.PERMISSION_GRANTED){
                Log.w("fcm", "Notification permission is not granted yet.");
                return;
            }
        }
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //Add eventId to intent so MainActivity can navigate to event details
        if (eventId != null){
            intent.putExtra("eventId", eventId);
        }
        //create pending intent
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );
        //Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this,
                BeeThereChannel.CHANNEL_ID
        )
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, builder.build());
    }

}
