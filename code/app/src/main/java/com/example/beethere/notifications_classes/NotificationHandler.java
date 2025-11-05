package com.example.beethere.notifications_classes;

import java.util.ArrayList;
import java.util.List;

public class NotificationHandler {
    //storing stuff in firebase ...

    //creating a notification object
    private List<Notification> notificationsList;
    public NotificationHandler(){
        this.notificationsList = new ArrayList<>();
    }

    public void addNotification(Notification notification){
        notificationsList.add(notification);
    }


    // sending notifications (Stored in an array list)

    //gonna send the same messages to multiple device ids
    public void sendToUsers(String eventId, String eventName, List<String> deviceIds, String message){
        long timestamp = System.currentTimeMillis();
        for(String id: deviceIds){
            Notification notif = new Notification(generateNotificationId(), eventId, eventName, message, timestamp, "")
        }
    }

    //get all notifications for a user
    public List<Notification> getNotificationsForUser(String deviceId){
        List<Notification> userNotifs = new ArrayList<>();

        for (Notification n: notificationsList){
            if (n.getDeviceIds().contains(deviceId)){
                userNotifs.add(n);
            }
        }
        return userNotifs;
    }

    //delete notifications of a user if the profile is deleted

    //admin view log of all notifications
    public List<Notification> getNotificationsList() {
        return notificationsList;
    }



}
