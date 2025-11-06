package com.example.beethere.eventclasses;

import com.example.beethere.User;

import java.time.LocalDate;
import java.time.LocalTime;

public class Event {
    private User organizer;
    private String name;
    private String description;

    private int poster;
    private int qrCode;

    private Boolean status;


    private LocalDate regStart;
    private LocalDate regEnd;

    private LocalDate eventDateStart;
    private LocalDate eventDateEnd;
    private LocalTime eventTimestart;
    private LocalTime eventTimeEnd;


    private Boolean geoloc;
    private Boolean autoRandomSelection;

    private UserListManager listManager;




    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPoster() {
        return poster;
    }

    public void setPoster(int poster) {
        this.poster = poster;
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

    public LocalTime getEventTimestart() {
        return eventTimestart;
    }

    public void setEventTimestart(LocalTime eventTimestart) {
        this.eventTimestart = eventTimestart;
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

    public Boolean getAutoRandomSelection() {
        return autoRandomSelection;
    }

    public void setAutoRandomSelection(Boolean autoRandomSelection) {
        this.autoRandomSelection = autoRandomSelection;
    }

    public UserListManager getListManager() {
        return listManager;
    }

    public void setListManager(UserListManager listManager) {
        this.listManager = listManager;
    }
}
