package com.example.beethere.eventclasses;

import com.example.beethere.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;


public class Event {

    private User organizer;
    private String eventID;
    private String posterPath;

    //status on if the event is still active or not
    private Boolean status;

    // geolocation requirement for joining event
    private Boolean geoloc;

    // event details
    private String title;
    private String description;

    // waitlist opens/closes dates
    private String regStart;
    private String regEnd;

    // actual event dates and time
    private String eventDateStart;
    private String eventDateEnd;
    private String eventTimeStart;
    private String eventTimeEnd;

    // randomly selects new invite when person declines
    Boolean autoRandomSelection;

    private Integer maxWaitlist;
    private ArrayList<User> waitList;

    private Map<String, Boolean> invited;

    private Integer entrantMax;
    private ArrayList<User> registered;


    /** Constructors */
    // empty constructor for database
    public Event() {}

    /**
     *
     * @param organizer User, user who can create events, identified by unique deviceID
     * @param title String,title of the event
     * @param description String, details of the event
     * @param posterPath URI, advertisement poster of the event
     * @param status boolean, status of if the event is active or not
     * @param regStart Date, when registration for the event begins
     * @param regEnd Date, when registration for the event ends
     * @param eventDateStart Date, when the event itself begins
     * @param eventDateEnd Date, when the event itself ends
     * @param eventTimeStart Date, the time the event starts
     * @param eventTimeEnd  Date, the time the event ends
     * @param entrantMax   Integer, max number of entrants, individuals who can attend the event
     * @param getLocation boolean, organizer requires geolocation of participants to be collected
     * @param autoRandomSelection boolean, if those in the waiting list should be selected on invitees cancellation
     */
    public Event(User organizer, String eventID, String title, String description, String posterPath,
                 Boolean status, String regStart, String regEnd, String eventDateStart,
                 String eventDateEnd, String eventTimeStart, String eventTimeEnd,
                 Integer entrantMax, Boolean getLocation, ArrayList<User> waitList, Map<String, Boolean> invited,
                 ArrayList<User> registered, Boolean autoRandomSelection) {
        this.organizer = organizer;
        this.eventID = eventID;
        this.title = title;
        this.description = description;
        this.posterPath = posterPath;
        this.status = status;
        this.regStart = regStart;
        this.regEnd = regEnd;
        this.eventDateStart = eventDateStart;
        this.eventDateEnd = eventDateEnd;
        this.eventTimeStart = eventTimeStart;
        this.eventTimeEnd = eventTimeEnd;
        this.entrantMax = entrantMax;
        this.geoloc = getLocation;
        this.waitList = waitList;
        this.invited = invited;
        this.registered = registered;
        this.autoRandomSelection = autoRandomSelection;
        this.maxWaitlist = Integer.MAX_VALUE;
    }

    public Event(User organizer, String eventID, String title, String description, String posterPath,
                 Boolean status,String regStart, String regEnd, String eventDateStart,
                 String eventDateEnd, String eventTimeStart, String eventTimeEnd,
                 Integer entrantMax, Boolean getLocation, ArrayList<User> waitList, Map<String, Boolean> invited,
                 ArrayList<User> registered, Boolean autoRandomSelection, Integer maxWaitlist) {
        this.organizer = organizer;
        this.eventID = eventID;
        this.title = title;
        this.description = description;
        this.posterPath = posterPath;
        this.status = status;
        this.regStart = regStart;
        this.regEnd = regEnd;
        this.eventDateStart = eventDateStart;
        this.eventDateEnd = eventDateEnd;
        this.eventTimeStart = eventTimeStart;
        this.eventTimeEnd = eventTimeEnd;
        this.geoloc = getLocation;
        this.entrantMax = entrantMax;
        this.waitList = waitList;
        this.invited = invited;
        this.registered = registered;
        this.autoRandomSelection = autoRandomSelection;
        this.maxWaitlist = maxWaitlist;
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

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Boolean getGeoloc() {
        return geoloc;
    }

    public void setGeoloc(Boolean geoloc) {
        this.geoloc = geoloc;
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

    public Boolean getAutoRandomSelection() {
        return autoRandomSelection;
    }

    public void setAutoRandomSelection(Boolean autoRandomSelection) {
        this.autoRandomSelection = autoRandomSelection;
    }

    public Integer getMaxWaitlist() {
        return maxWaitlist;
    }

    public void setMaxWaitlist(Integer maxWaitlist) {
        this.maxWaitlist = maxWaitlist;
    }

    // PROBLEM IN ULM WITH NOTIFICATIONS
    public ArrayList<User> getWaitList() {
        return waitList;
    }

    public void setWaitList(ArrayList<User> waitList) {
        this.waitList = waitList;
    }

    // PROBLEM IN ULM WITH NOTIFICATIONS
    public Map<String, Boolean> getInvited() {
        return invited;
    }

    public void setInvited(Map<String, Boolean> invited) {
        this.invited = invited;
    }

    // EVENT DETAILS FRAGMENT
    public Integer getEntrantMax() {
        return entrantMax;
    }

    public void setEntrantMax(Integer entrantMax) {
        this.entrantMax = entrantMax;
    }

    public ArrayList<User> getRegistered() {
        return registered;
    }

    public void setRegistered(ArrayList<User> registered) {
        this.registered = registered;
    }


    public LocalDate convertRegEnd() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return LocalDate.parse(this.regEnd, dateFormatter);
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
