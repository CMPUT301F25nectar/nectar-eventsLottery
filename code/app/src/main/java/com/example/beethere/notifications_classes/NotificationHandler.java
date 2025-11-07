package com.example.beethere.notifications_classes;

import com.example.beethere.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *This is a class that handles all notifications operations for the BEE-THERE app
 * Manages sending notifications and retrieving them from Firebase Firestore
 */
public class NotificationHandler {
    /** Firebase Firestore instance for database operations*/
    private FirebaseFirestore db;

    // Notification types
    /**Notification type constant for lottery winner*/
    public String TYPE_LOTTERY_WON = "lotteryWon";
    /**Notification type constant for lottery loser*/
    public String TYPE_LOTTERY_LOST = "lotteryLost";
    /**Notification type constant for organizer messages*/
    public String TYPE_ORGANIZER_MESSAGE = "organizerMessage";

    /**
     * Constructor that initializes Firebase instance
     */
    public NotificationHandler(){
        this.db = FirebaseFirestore.getInstance();
    }


    /**
     * Sends lottery result notifications to winners and losers
     * @param eventId The unique identifier for the event
     * @param eventName The name of the event
     * @param inviteList HashMap of users who won the lottery
     * @param waitlist ArrayList of users who didn't win
     * @param organizerDeviceId The device id of the organizer who sends the notifications
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
     * Sends "you won the lottery!" notification to a selected user
     * @param user The user who won/selected
     * @param eventId The unique identifier of the event
     * @param eventName The event name
     * @param organizerDeviceId The device id of the organizer
     */
    private void sendLotteryWonNotification(User user, String eventId, String eventName, String organizerDeviceId) {
        String message = "Congratulations! You've been selected for " + eventName + ". Accept your invitation now!";

        List<String> deviceIds = new ArrayList<>();
        deviceIds.add(user.getDeviceid());

        Notification notification = new Notification(
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
     * Sends "Not selected" notification to a user
     * @param user The user who didn't win
     * @param eventId The unique identifier of the event
     * @param eventName The event name
     * @param organizerDeviceId The device id of the organizer
     */
    private void sendLotteryLostNotification(User user, String eventId, String eventName, String organizerDeviceId){
        String message = "Sorry! You weren't selected for " + eventName + " this time. You'll remain on the waitlist.";

        List<String> deviceIds = new ArrayList<>();
        deviceIds.add(user.getDeviceid());

        Notification notification = new Notification(
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
     * Sets up real-time listener for notifications
     * Automatically updates when new notifications arrive
     * @param deviceId The device id of the user
     * @param callback Callback interface for success/error handling
     */
    public void setupNotificationListener(String deviceId, NotificationCallback callback) {
        db.collection("notifications")
                .whereArrayContains("deviceIds", deviceId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
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

    /**
     * Saves a notification to Firebase Firestore
     * @param notification The notification object to save
     */
    private void saveNotificationToFirebase(Notification notification){
        db.collection("notifications")
                .add(notification)
                .addOnSuccessListener(aVoid -> {
                    android.util.Log.d("NotificationHandler", "notif saved" +notification.getEventName());
                })
                .addOnFailureListener(e ->{
                    android.util.Log.e("NotificationHandler", "failed " + e.getMessage());
                });
                //.set(notification);
    }

    /**
     * Callback interface for asynchronous notification retrieval
     */
    public interface NotificationCallback {
        /**
         * Called when notifications are successfully retrieved
         * @param notifications List of notifications
         */
        void onSuccess(List<Notification> notifications);

        /**
         * Called when an error occurs
         * @param error Error message
         */
        void onError(String error);
    }
}