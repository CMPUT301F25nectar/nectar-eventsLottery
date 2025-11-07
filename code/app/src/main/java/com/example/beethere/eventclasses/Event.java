package com.example.beethere.eventclasses;

import android.net.Uri;

import com.example.beethere.User;
import com.google.zxing.common.BitMatrix;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;

public class Event {
    private User organizer;
    private String title;
    private String description;
    private int eventID;

    private String posterPath;
    private BitMatrix qrCode;

    private Boolean status; //status on if the event is still active or not

    private LocalDateTime regStart;
    private LocalDateTime regEnd;

    private LocalDateTime eventDateStart;
    private LocalDateTime eventDateEnd;
    private LocalDateTime eventTimeStart;
    private LocalDateTime eventTimeEnd;

    private Boolean geoloc;

    private UserListManager entrantList;


    /**
     *
     * @param organizer User, user who can create events, identified by unique deviceID
     * @param title String,title of the event
     * @param description String, details of the event
     * @param posterPath URI, advertisement poster of the event
     * @param qrCode idk, qr code to take user to page for event
     * @param status boolean, status of if the event is active or not
     * @param regStart Date, when registration for the event begins
     * @param regEnd Date, when registration for the event ends
     * @param eventDateStart Date, when the event itself begins
     * @param eventDateEnd Date, when the event itself ends
     * @param eventTimeStart Date, the time the event starts
     * @param eventTimeEnd  Date, the time the event ends
     * @param entrantMax   integer, max number of entrants, individuals who can attend the event
     * @param getLocation boolean, organizer requires geolocation of participants to be collected
     * @param autoRandomSelection boolean, if those in the waiting list should be selected on invitees cancellation
     */

    public Event(User organizer, int eventID, String title, String description, String posterPath, BitMatrix qrCode,
                 Boolean status, LocalDateTime regStart, LocalDateTime regEnd, LocalDateTime eventDateStart,
                 LocalDateTime eventDateEnd, LocalDateTime eventTimeStart, LocalDateTime eventTimeEnd,
                 int entrantMax, Boolean getLocation,
                 Boolean autoRandomSelection) {
        this.organizer = organizer;
        this.eventID = eventID;
        this.title = title;
        this.description = description;
        this.posterPath = posterPath;
        this.qrCode = qrCode;
        this.status = status;
        this.regStart = regStart;
        this.regEnd = regEnd;
        this.eventDateStart = eventDateStart;
        this.eventDateEnd = eventDateEnd;
        this.eventTimeStart = eventTimeStart;
        this.eventTimeEnd = eventTimeEnd;
        this.geoloc = getLocation;
        this.entrantList = new UserListManager(autoRandomSelection, entrantMax);
    }

    public Event(User organizer, int eventID, String title, String description, String posterPath, BitMatrix qrCode,
                 Boolean status, LocalDateTime regStart, LocalDateTime regEnd, LocalDateTime eventDateStart,
                 LocalDateTime eventDateEnd, LocalDateTime eventTimeStart, LocalDateTime eventTimeEnd,
                 int entrantMax, Boolean getLocation,
                 Boolean autoRandomSelection, int maxWaitlist) {
        this.organizer = organizer;
        this.eventID = eventID;
        this.title = title;
        this.description = description;
        this.posterPath = posterPath;
        this.qrCode = qrCode;
        this.status = status;
        this.regStart = regStart;
        this.regEnd = regEnd;
        this.eventDateStart = eventDateStart;
        this.eventDateEnd = eventDateEnd;
        this.eventTimeStart = eventTimeStart;
        this.eventTimeEnd = eventTimeEnd;
        this.geoloc = getLocation;
        this.entrantList = new UserListManager(autoRandomSelection, entrantMax, maxWaitlist);
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public BitMatrix getQrCode() {
        return qrCode;
    }

    public void setQrCode(BitMatrix qrCode) {
        this.qrCode = qrCode;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public LocalDateTime getRegStart() {
        return regStart;
    }

    public void setRegStart(LocalDateTime regStart) {
        this.regStart = regStart;
    }

    public LocalDateTime getRegEnd() {
        return regEnd;
    }

    public void setRegEnd(LocalDateTime regEnd) {
        this.regEnd = regEnd;
    }

    public LocalDateTime getEventDateStart() {
        return eventDateStart;
    }

    public void setEventDateStart(LocalDateTime eventDateStart) {
        this.eventDateStart = eventDateStart;
    }

    public LocalDateTime getEventDateEnd() {
        return eventDateEnd;
    }

    public void setEventDateEnd(LocalDateTime eventDateEnd) {
        this.eventDateEnd = eventDateEnd;
    }

    public LocalDateTime getEventTimeStart() {
        return eventTimeStart;
    }

    public void setEventTimeStart(LocalDateTime eventTimeStart) {
        this.eventTimeStart = eventTimeStart;
    }

    public LocalDateTime getEventTimeEnd() {
        return eventTimeEnd;
    }

    public void setEventTimeEnd(LocalDateTime eventTimeEnd) {
        this.eventTimeEnd = eventTimeEnd;
    }

    public Boolean getGeoloc() {
        return geoloc;
    }

    public void setGeoloc(Boolean geoloc) {
        this.geoloc = geoloc;
    }

    public UserListManager getEntrantList() {
        return entrantList;
    }

    public void setEntrantList(UserListManager entrantList) {
        this.entrantList = entrantList;
    }
}
