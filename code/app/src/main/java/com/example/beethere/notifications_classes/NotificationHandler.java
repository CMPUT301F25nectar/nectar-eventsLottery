package com.example.beethere.notifications_classes;

import com.example.beethere.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NotificationHandler {
    private FirebaseFirestore db;

    // Notification types
    public String TYPE_LOTTERY_WON = "lotteryWon";
    public String TYPE_LOTTERY_LOST = "lotteryLost";
    public String TYPE_ORGANIZER_MESSAGE = "organizerMessage";

    public NotificationHandler(){
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Send lottery result notifications to winners and losers
     */
    public void sendLotteryNotifications(String eventId, String eventName,
                                         HashMap<User, Boolean> inviteList,
                                         ArrayList<User> waitlist,
                                         String organizerDeviceId){
        for (User winner : inviteList.keySet()){
            sendLotteryWonNotification(winner, eventId, eventName, organizerDeviceId);
        }

        for (User loser: waitlist){
            sendLotteryLostNotification(loser, eventId, eventName, organizerDeviceId);
        }
    }

    /**
     * Send "You won!" notification
     */
    private void sendLotteryWonNotification(User user, String eventId, String eventName, String organizerDeviceId) {
        String message = "Congratulations! You've been selected for " + eventName + ". Accept your invitation now!";

        List<String> deviceIds = new ArrayList<>();
        deviceIds.add(user.getDeviceid());

        Notification notification = new Notification(
                db.collection("notifications").document().getId(),
                eventId,
                eventName,
                message,
                System.currentTimeMillis(),
                TYPE_LOTTERY_WON,
                deviceIds,
                organizerDeviceId
        );

        saveNotificationToFirebase(notification);
    }

    /**
     * Send "Not selected" notification
     */
    private void sendLotteryLostNotification(User user, String eventId, String eventName, String organizerDeviceId){
        String message = "Sorry! You weren't selected for " + eventName + " this time. You'll remain on the waitlist.";

        List<String> deviceIds = new ArrayList<>();
        deviceIds.add(user.getDeviceid());

        Notification notification = new Notification(
                db.collection("notifications").document().getId(),
                eventId,
                eventName,
                message,
                System.currentTimeMillis(),
                TYPE_LOTTERY_LOST,
                deviceIds,
                organizerDeviceId
        );

        saveNotificationToFirebase(notification);
    }

    /**
     * Send custom message from organizer to waitlist
     */
    public void sendOrganizerMessage(String eventId, String eventName,
                                     ArrayList<User> waitlist,
                                     String customMessage,
                                     String organizerDeviceId){
        List<String> deviceIds = new ArrayList<>();
        for (User user : waitlist) {
            deviceIds.add(user.getDeviceid());
        }

        if (!deviceIds.isEmpty()) {
            Notification notification = new Notification(
                    db.collection("notifications").document().getId(),
                    eventId,
                    eventName,
                    customMessage,
                    System.currentTimeMillis(),
                    TYPE_ORGANIZER_MESSAGE,
                    deviceIds,
                    organizerDeviceId
            );

            saveNotificationToFirebase(notification);
        }
    }

    /**
     * Set up real-time listener for notifications
     */
    public void setupNotificationListener(String deviceId, NotificationCallback callback) {
        db.collection("notifications")
                .whereArrayContains("deviceIds", deviceId)
                //.orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        callback.onError(error.getMessage());
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        List<Notification> notifications = new ArrayList<>();
                        for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                            Notification notif = queryDocumentSnapshots.getDocuments().get(i).toObject(Notification.class);
                            if (notif != null) {
                                notifications.add(notif);
                            }
                        }
                        callback.onSuccess(notifications);
                    }
                });
    }

    private void saveNotificationToFirebase(Notification notification){
        db.collection("notifications")
                .document(notification.getNotificationId())
                .set(notification)
                .addOnSuccessListener(aVoid -> {
                    android.util.Log.d("NotificationHandler", "notif saved" +notification.getEventName());
                })
                .addOnFailureListener(e ->{
                    android.util.Log.e("NotificationHandler", "failed " + e.getMessage());
                });
                //.set(notification);
    }

    public interface NotificationCallback {
        void onSuccess(List<Notification> notifications);
        void onError(String error);
    }
}