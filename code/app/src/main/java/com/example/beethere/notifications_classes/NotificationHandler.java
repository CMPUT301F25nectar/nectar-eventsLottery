package com.example.beethere.notifications_classes;

import android.util.Log;

import com.example.beethere.DatabaseCallback;
import com.example.beethere.DatabaseFunctions;
import com.example.beethere.User;
import com.example.beethere.eventclasses.Event;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *This is a class that handles all notifications operations for the BEE-THERE app
 * Manages sending notifications and retrieving them from Firebase Firestore
 */
public class NotificationHandler {
    /** Firebase Firestore instance for database operations*/

    // Notification types
    /**Notification type constant for lottery winner*/
    public String TYPE_LOTTERY_WON = "lotteryWon";
    /**Notification type constant for lottery loser*/
    public String TYPE_LOTTERY_LOST = "lotteryLost";
    /**Notification type constant for organizer messages*/
    public String TYPE_ORGANIZER_MESSAGE = "organizerMessage";
    public String TYPE_ADMIN_MESSAGE = "adminMessage";

    private DatabaseFunctions dbfunctions;

    /**
     * Constructor that initializes Firebase instance
     */
    public NotificationHandler(){
        dbfunctions = new DatabaseFunctions();
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
                                         Map<String, Boolean> inviteList,
                                         ArrayList<User> waitlist,
                                         String organizerDeviceId){
        for (String winner : inviteList.keySet()){
            sendLotteryWonNotification(winner, eventId, eventName, organizerDeviceId);
        }

        for (User loser: waitlist){
            sendLotteryLostNotification(loser, eventId, eventName, organizerDeviceId);
        }
    }

    /**
     * Sends "you won the lottery!" notification to a selected user
     * @param deviceID The deviceID of the user who won/selected
     * @param eventId The unique identifier of the event
     * @param eventName The event name
     * @param organizerDeviceId The device id of the organizer
     */
    private void sendLotteryWonNotification(String deviceID, String eventId, String eventName, String organizerDeviceId) {
        dbfunctions.getUserDB(deviceID, new DatabaseCallback<User>() {
            @Override
            public void onCallback(User user) {
                if (user != null && user.getReceiveWinningNotifs()) {
                    String message = "Congratulations! You've been selected for " + eventName + ". Accept your invitation now!";

                    List<String> deviceIds = new ArrayList<>();
                    deviceIds.add(deviceID);

                    Notification notification = new Notification(
                            eventId,
                            eventName,
                            message,
                            System.currentTimeMillis(),
                            TYPE_LOTTERY_WON,
                            deviceIds,
                            organizerDeviceId
                    );

                    dbfunctions.addNotifsDB(notification);
                }else {
                    Log.d("NotificationHandler", "User opted out of winning notifications");
                }
            }
            @Override
            public void onError(Exception e) {
                Log.e("NotificationHandler", "Error checking user preferences", e);
            }
        });
    }


    /**
     * Sends "Not selected" notification to a user
     * @param user The user who didn't win
     * @param eventId The unique identifier of the event
     * @param eventName The event name
     * @param organizerDeviceId The device id of the organizer
     */
    private void sendLotteryLostNotification(User user, String eventId, String eventName, String organizerDeviceId){
        dbfunctions.getUserDB(user.getDeviceid(), new DatabaseCallback<User>() {
            @Override
            public void onCallback(User result) {
                if(user != null && user.getReceiveLosingNotifs()){
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

                    dbfunctions.addNotifsDB(notification);
                }
                else {
                    Log.d("NotificationHandler", "User opted out of losing notifications!!");
                }
            }

            @Override
            public void onError(Exception e) {
                Log.d("NotificationHandler", "Error checking user's preferences.", e);
            }
        });

    }


    public void sendOrganizerMessage(String eventId, String eventName,
                                     ArrayList<User> waitlist,
                                     String customMessage,
                                     String organizerDeviceId){
        List<String> deviceIds = new ArrayList<>();
        for (User user : waitlist) {
            if (user.getReceiveOrganizerNotifs()) {
                deviceIds.add(user.getDeviceid());
            }
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

            dbfunctions.addNotifsDB(notification);
        }else {
            Log.d("NotificationHandler", "No users opted in to receive organizer messages");
        }
    }

    /**
     * Sends admin message to a list of users
     * Only sends to users who have opted in to receive admin messages
     * @param userList List of users to send message to
     * @param customMessage The message from admin
     * @param adminDeviceId The admin's device ID
     */
    public void sendAdminMessage(ArrayList<User> userList,
                                 String customMessage,
                                 String adminDeviceId){
        List<String> deviceIds = new ArrayList<>();

        // Only add users who have opted in to receive admin messages
        for (User user : userList) {
            if (user.getReceiveAdminNotifs()) {
                deviceIds.add(user.getDeviceid());
            }
        }

        if (!deviceIds.isEmpty()) {
            Notification notification = new Notification(
                    null,  // No specific event
                    "Admin Message",
                    customMessage,
                    System.currentTimeMillis(),
                    TYPE_ADMIN_MESSAGE,
                    deviceIds,
                    adminDeviceId
            );

            dbfunctions.addNotifsDB(notification);
        } else {
            Log.d("NotificationHandler", "No users opted in to receive admin messages");
        }
    }
}