package com.example.beethere.eventclasses;

import android.net.Uri;

import com.example.beethere.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Event {
    private User organizer;

    private String title;
    private String description;
    private String eventID;

    private String posterPath;
    private int qrCode;

    private Boolean status; //status on if the event is still active or not

    private String regStart;
    private String regEnd;

    private String eventDateStart;
    private String eventDateEnd;
    private String eventTimeStart;
    private String eventTimeEnd;

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
                 Boolean status, String regStart, String regEnd, String eventDateStart,
                 String eventDateEnd, String eventTimeStart, String eventTimeEnd,
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
                 Boolean status, String regStart, String regEnd, String eventDateStart,
                 String eventDateEnd, String eventTimeStart, String eventTimeEnd,
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

    public String getRegStart() {
        return regStart;
    }

    public void setRegStart(String regStart) {
        this.regStart = regStart;
    }

    public String getRegEnd() {
        return regEnd;
    }

    public void setRegEnd(String regEnd) {
        this.regEnd = regEnd;
    }

    public String getEventDateStart() {
        return eventDateStart;
    }

    public void setEventDateStart(String eventDateStart) {
        this.eventDateStart = eventDateStart;
    }

    public String getEventDateEnd() {
        return eventDateEnd;
    }

    public void setEventDateEnd(String eventDateEnd) {
        this.eventDateEnd = eventDateEnd;
    }

    public String getEventTimeStart() {
        return eventTimeStart;
    }

    public void setEventTimeStart(String eventTimeStart) {
        this.eventTimeStart = eventTimeStart;
    }

    public String getEventTimeEnd() {
        return eventTimeEnd;
    }

    public void setEventTimeEnd(String eventTimeEnd) {
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


    public LocalDate convertDate(String stringDate) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return LocalDate.parse(stringDate, dateFormatter);
    }

    public LocalTime convertTime(String stringTime) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US);

        return LocalTime.parse(stringTime, timeFormatter);
    }
}
