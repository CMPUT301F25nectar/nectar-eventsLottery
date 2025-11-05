package com.example.beethere.notifications_classes;

import java.util.List;

public class Notification {
    private String notificationId;
    private String eventId;
    private String eventName;
    private String message;
    private long timestamp;
    private String type;
    private List<String> deviceIds;
    private String createdBy;

    //add no arg constructor for firebase girll

    public Notification(String notificationId, String eventId, String eventName, String message, long timestamp, String type, List<String> deviceIds, String createdBy ){
        this.notificationId = notificationId;
        this.eventId = eventId;
        this.eventName = eventName;
        this.message=message;
        this.timestamp = timestamp;
        this.type = type;
        this.deviceIds = deviceIds;
        this.createdBy = createdBy;
    }

    //getters
    public String getNotificationId() {
        return notificationId;
    }
    public String getEventId() {
        return eventId;
    }
    public String getEventName() {
        return eventName;
    }
    public String getMessage() {
        return message;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public String getType() {
        return type;
    }
    public List<String> getDeviceIds() {
        return deviceIds;
    }
    public String getCreatedBy() {
        return createdBy;
    }
    //setters

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDeviceIds(List<String> deviceIds) {
        this.deviceIds = deviceIds;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
