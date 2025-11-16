package com.example.beethere.eventclasses;

import android.net.Uri;

import com.example.beethere.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;

public class Event {
    private User organizer;
    private String title;
    private String description;
    private String eventID;

    private String posterPath;
    private int qrCode;

    private Boolean status; //status on if the event is still active or not

    private LocalDate regStart;
    private LocalDate regEnd;

    private LocalDate eventDateStart;
    private LocalDate eventDateEnd;
    private LocalTime eventTimeStart;
    private LocalTime eventTimeEnd;

    private Boolean geoloc;

    private UserListManager entrantList;


    public Event(){

    }

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

    public Event(User organizer, String eventID, String title, String description, String posterPath, int qrCode,
                 Boolean status, LocalDate regStart, LocalDate regEnd, LocalDate eventDateStart,
                 LocalDate eventDateEnd, LocalTime eventTimeStart, LocalTime eventTimeEnd,
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

    public Event(User organizer, String eventID, String title, String description, String posterPath, int qrCode,
                 Boolean status, LocalDate regStart, LocalDate regEnd, LocalDate eventDateStart,
                 LocalDate eventDateEnd, LocalTime eventTimeStart, LocalTime eventTimeEnd,
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

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
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

    public int getQrCode() {
        return qrCode;
    }

    public void setQrCode(int qrCode) {
        this.qrCode = qrCode;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public LocalDate getRegStart() {
        return regStart;
    }

    public void setRegStart(LocalDate regStart) {
        this.regStart = regStart;
    }

    public LocalDate getRegEnd() {
        return regEnd;
    }

    public void setRegEnd(LocalDate regEnd) {
        this.regEnd = regEnd;
    }

    public LocalDate getEventDateStart() {
        return eventDateStart;
    }

    public void setEventDateStart(LocalDate eventDateStart) {
        this.eventDateStart = eventDateStart;
    }

    public LocalDate getEventDateEnd() {
        return eventDateEnd;
    }

    public void setEventDateEnd(LocalDate eventDateEnd) {
        this.eventDateEnd = eventDateEnd;
    }

    public LocalTime getEventTimeStart() {
        return eventTimeStart;
    }

    public void setEventTimeStart(LocalTime eventTimeStart) {
        this.eventTimeStart = eventTimeStart;
    }

    public LocalTime getEventTimeEnd() {
        return eventTimeEnd;
    }

    public void setEventTimeEnd(LocalTime eventTimeEnd) {
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
