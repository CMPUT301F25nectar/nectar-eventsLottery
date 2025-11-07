package com.example.beethere.notifications_classes;

import com.example.beethere.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.prefs.Preferences;

public class NotificationHandler {
    //storing stuff in firebase
    private FirebaseFirestore db;

    public String typeLotteryWon = "lotteryWon";
    public String typeLotteryLost = "lotteryLost";
    public String typeUserAccepted = "userAccepted";
    public String typeUserDeclined = "userDeclined";
    public String typeOrganizerMessage = "organizerMessage";
    public String typeEventCancelled = "eventCancelled";
    // admin alerts?

    public NotificationHandler(){
        this.db = FirebaseFirestore.getInstance();
    }

    // sending notifications (Stored in an array list)
    //gonna send the same messages to multiple device ids

    /**
     * @param eventId
     * @param eventName
     * @param inviteList
     * @param waitlist
     * @param organizerDeviceId
     */
    public void sendLotteryNotifications(String eventId, String eventName, HashMap<User, Boolean> inviteList, ArrayList<User> waitlist, String organizerDeviceId){
        for (User winner : inviteList.keySet()){
            sendLotteryWonNotification(winner, eventId, eventName, organizerDeviceId);
        }
        for (User loser: waitlist){
            sendLotteryLostNotification(loser, eventId, eventName, organizerDeviceId);
        }
    }

    /**
     * @param user
     * @param eventId
     * @param eventName
     * @param organizerDeviceId
     */
    //gonna send the same messages to multiple device ids
    private void sendLotteryWonNotification(User user, String eventId, String eventName, String organizerDeviceId) {
        checkNotificationPreference(user.getDeviceid(), isEnabled -> {
            if (!isEnabled) {
                return;
            }
            //String title = "Congratulations! You are invited.";
            String message = "You have been selected for " + eventName + "Accept it now!";

            // device ids list with single user
            List<String> deviceIds = new ArrayList<>();
            deviceIds.add(user.getDeviceid());

            //creating notifications
            Notification notification = new Notification(db.collection("notifications").document().getId(), eventId, eventName, message, System.currentTimeMillis(), typeLotteryWon, deviceIds, organizerDeviceId);

            //saving it to firebase
            saveNotificationToFirebase(notification);
        });

    }

    /**
     * @param user
     * @param eventId
     * @param eventName
     * @param organizerDeviceId
     */
    private void sendLotteryLostNotification(User user, String eventId, String eventName, String organizerDeviceId){
        checkNotificationPreference(user.getDeviceid(), isEnabled -> {
            if (!isEnabled) {
                return;
            }
            String message = "Sorry! You weren't selected for " + eventName + "this time. You will remain on the waitlist.";
            List<String> deviceIds = new ArrayList<>();
            deviceIds.add(user.getDeviceid());

            //creating notifications
            Notification notification = new Notification(db.collection("notifications").document().getId(), eventId, eventName, message, System.currentTimeMillis(), typeLotteryWon, deviceIds, organizerDeviceId);
            saveNotificationToFirebase(notification);
        });
    }


    /**
     * @param notification
     */
    private void saveNotificationToFirebase(Notification notification){
        db.collection("notifications").document(notification.getNotificationId()).set(notification);
    }


    /**
     * @param deviceId
     */
    //get all notifications for a user, should we do callback?
    public void getUserNotifications(String deviceId) {
        db.collection("notifications")
                .whereArrayContains("deviceIds", deviceId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Notification> notifications = new ArrayList<>();
                    queryDocumentSnapshots.forEach(doc -> {
                        Notification notif = doc.toObject(Notification.class);
                        notifications.add(notif);
                    });
                    //callback.onSuccess(notifications);
                })
                .addOnFailureListener(e -> {
                    //callback.onError(e.getMessage());
                });
    }

    //set up real time listener for notifs
    public void setupNotificationListener(String deviceId) {
        db.collection("notifications")
                .whereArrayContains("deviceIds", deviceId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((queryDocumentSnapshots, error) -> {

                    if (queryDocumentSnapshots != null) {
                        List<Notification> notifications = new ArrayList<>();
                        queryDocumentSnapshots.forEach(doc -> {
                            Notification notif = doc.toObject(Notification.class);
                            notifications.add(notif);
                        });
                    }
                });
    }



}
