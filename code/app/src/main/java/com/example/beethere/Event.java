package com.example.beethere;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Event {
    private User organizer;
    private String title;
    private String description;
    private int image;
    private int qrCode;

    private Boolean status;

    private LocalDate regStart;
    private LocalDate regEnd;

    private LocalDate eventDateStart;
    private LocalDate eventDateEnd;
    private LocalTime eventTimestart;
    private LocalTime eventTimeEnd;


    private int entrantMax;

    private Boolean geoloc;

    //move these 2 attributes into waitlist manager class, make into 1 attribute
    // that class would control the display of number of people in waitlist
    // of adding and removing from waitlist
    // of randomly selecting from waitlist
    // etc?
    
    private ArrayList<User> waitlist;
    private int waitlistMax;


    private ArrayList<User> inviteList;
    private ArrayList<User> registered;

    private Boolean autoRandomSelection;

    // all have getters and setters

    // Display lists are handled by the fragment

    // Random selection from waitlist into invite
    // includes sending notification

    // export registered as CSV


    // NEW CLASS???
    // EventDetailsAcvitiy class


    // addtowaitlist
    // includes checking for max


    // add to entrants
    // includes checking for max

    // autoRandomSelection
    // how to check declining invite
    // if there are multiple people declining what happens?





}
