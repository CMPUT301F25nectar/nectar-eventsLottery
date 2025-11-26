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

        dbfunctions.addNotifsDB(notification);
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

        dbfunctions.addNotifsDB(notification);
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

            dbfunctions.addNotifsDB(notification);
        }
    }
}