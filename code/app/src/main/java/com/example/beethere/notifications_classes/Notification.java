package com.example.beethere.notifications_classes;

import java.util.ArrayList;
import java.util.List;

public class Notification {
    private String notificationId;
    private String eventId;
    private String eventName;
    private String message;
    private long timestamp;
    private String type;
    private List<String> deviceIds;
    private String organizerDeviceId;

    //generating primary composite key:
    // @Entity (primaryKeys = {"column1","column2"})

    //no arg constructor for firebase
    public Notification(){
        this.notificationId = "";
        this.eventId = "";
        this.eventName = "";
        this.message = "";
        this.timestamp = 0L;
        this.type = "custom";
        this.deviceIds = new ArrayList<>();
        this.organizerDeviceId = "";


    }

    public Notification(String notificationId, String eventId, String eventName, String message, long timestamp, String type, List<String> deviceIds, String createdBy ){
        this.notificationId = notificationId;
        this.eventId = eventId;
        this.eventName = eventName;
        this.message=message;
        this.timestamp = timestamp;
        this.type = type;
        this.deviceIds = deviceIds;
        this.organizerDeviceId = createdBy;
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
    public String getOrganizerDeviceId() {
        return organizerDeviceId;
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

    public void setOrganizerDeviceId(String createdBy) {
        this.organizerDeviceId = organizerDeviceId;
    }
}
