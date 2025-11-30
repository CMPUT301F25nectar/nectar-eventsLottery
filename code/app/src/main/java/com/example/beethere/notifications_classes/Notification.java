package com.example.beethere.notifications_classes;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a class that defines a Notification object.
 * Notifications are sent to users for lottery results, organizer messages, etc.
 */
public class Notification {
    /**Unique identifier for this notification*/
    private String notifId;
    /**The event this notification is related to*/
    private String eventId;
    /**Name of the event*/
    private String eventName;
    /**The content of the notification message*/
    private String message;
    /**Timestamp when notification was sent in milliseconds*/
    private long timestamp;
    /**Type of notification like lotteryWon, lotteryLost etc*/
    private String type;
    /**List of device IDs who should recieve this notification*/
    private List<String> deviceIds;
    /**List of device IDs who responded to the notification*/
    private List<String> respondedDeviceIds;
    /**Device id of who created this notification*/
    private String organizerDeviceId;


    /**
     * No-argument constructor for Firestore
     * */
    public Notification(){
        this.notifId = "";
        this.eventId = "";
        this.eventName = "";
        this.message = "";
        this.timestamp = 0L;
        this.type = "custom";
        this.deviceIds = new ArrayList<>();
        this.respondedDeviceIds = new ArrayList<>();
        this.organizerDeviceId = "";
    }


    /**
     * Constructor to create a new Notification
     * @param notificationId Unique identifier for this notification
     * @param eventId The event this notification relates to
     * @param eventName Name of the event
     * @param message The notification message
     * @param timestamp The time when notification was created
     * @param type Type of the notification
     * @param deviceIds List of users that will get the notifications
     * @param organizerDeviceId Who created this notification
     */
    public Notification(String notificationId, String eventId, String eventName, String message, long timestamp, String type, List<String> deviceIds, List<String> respondedDeviceIds, String organizerDeviceId ){
        this.notifId = notificationId;
        this.eventId = eventId;
        this.eventName = eventName;
        this.message=message;
        this.timestamp = timestamp;
        this.type = type;
        this.deviceIds = deviceIds;
        this.deviceIds = deviceIds;
        this.organizerDeviceId = organizerDeviceId;
    }

    //getters

    /**
     * Gets the notification ID
     * @return The notif identifier
     */
    public String getNotifId() {
        return notifId;
    }
    /**
     * Gets the event ID
     * @return The event identifier
     */
    public String getEventId() {
        return eventId;
    }
    /**
     * Gets the event name
     * @return The name of the event
     */
    public String getEventName() {
        return eventName;
    }
    /**
     * Gets the notification message
     * @return The content of the notification message
     */
    public String getMessage() {
        return message;
    }
    /**
     * Gets the timestamp
     * @return When the notification was sent in milliseconds
     */
    public long getTimestamp() {
        return timestamp;
    }
    /**
     * Gets the notification type
     * @return type of notification
     */
    public String getType() {
        return type;
    }
    /**
     * Gets the list of recipients device IDs
     * @return List of recipients' devide IDs
     */
    public List<String> getDeviceIds() {
        return deviceIds;
    }

    /**
     * Gets the list of device IDs of users who responded
     * @return List of recipients' who responded device IDs
     */
    public List<String> getRespondedDeviceIds() { return respondedDeviceIds; }

    /**
     * Gets the organzier who created this notification
     * @return Device ID of the organizer
     */
    public String getOrganizerDeviceId() {
        return organizerDeviceId;
    }

    //setters

    /**
     * Sets the notification ID
     * @param notifId The event identifier
     */
    public void setNotifId(String notifId) {
        this.notifId = notifId;
    }
    /**
     * Sets the event ID
     * @param eventId The event identifier
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Sets the event name
     * @param eventName The name of the event
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * Sets the notification message
     * @param message The notification message content
     */
    public void setMessage(String message) {
        this.message = message;
    }


    /**
     * Sets the timestamp
     * @param timestamp Time when the notification was sent in milliseconds
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Sets the notification type
     * @param type Type of notification
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Sets the list of device ID
     * @param deviceIds List of recipients' device IDs
     */
    public void setDeviceIds(List<String> deviceIds) {
        this.deviceIds = deviceIds;
    }

    /**
     * Sets the list of device IDs of users who responded
     * @param respondedDeviceIds List of recipients' who responded device IDs
     */
    public void setRespondedDeviceIds(List<String> respondedDeviceIds) {
        this.respondedDeviceIds = respondedDeviceIds;
    }

    /**
     * Sets who created this notification
     * @param organizerDeviceId Device ID of the creator
     */
    public void setOrganizerDeviceId(String organizerDeviceId) {
        this.organizerDeviceId = organizerDeviceId;
    }
}
