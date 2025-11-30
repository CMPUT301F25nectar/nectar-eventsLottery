package com.example.beethere;


/**
 * This is a model that defines a user profile for entrant/organizer/admin profile data used across the app.
 */






public class User {

    private String name;
    private String email;
    private String phone; //optional
    private String deviceid;
    private Boolean admin = false; //admin flag
    private Boolean organizer = false;//organizer flag
    private Boolean violation = false;
    private Boolean receiveWinningNotifs;
    private Boolean receiveLosingNotifs;
    private Boolean receiveCancelledNotifs;
    private Boolean receiveOrganizerNotifs;
    private Boolean receiveAdminNotifs;

    public User(){}

    /**
     *Constructor with mandatory fields needed for a profile
     * @param name user full name
     * @param email user email address
     */
    public User (String name, String email){
        this.name = name;
        this.email = email;
        this.receiveWinningNotifs = false;
        this.receiveLosingNotifs = false;
        this.receiveCancelledNotifs = false;
        this.receiveOrganizerNotifs = false;
        this.receiveAdminNotifs = false;
    }

    /**
     * returns the users full name
     * @return full name
     */
    public String getName(){
        return name;
    }

    /**
     * sets the users full name
     * @param name full name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * users email address
     * @return email
     */
    public String getEmail(){
        return email;
    }

    /**
     * sets the users email address
     * @param email email
     */
    public void setEmail(String email){
        this.email = email;
    }

    /**
     * returns the users phone number
     * @return phone
     */
    public String getPhone(){
        return phone;
    }

    /**
     * sets the users phone number
     * @param phone phone number (optional)
     */
    public void setPhone(String phone){
        this.phone=phone;
    }

    /**
     * returns the device id used for the users profile
     * @return device id
     */
    public String getDeviceid(){
        return deviceid;
    }

    /**
     * sets the device id as the users firestore document id
     * @param deviceid device id(ANDROID_ID)
     */
    public void setDeviceid(String deviceid){
        this.deviceid = deviceid;
    }

    /**
     * sets the admin flag
     * @param admin true for admin, false otherwise (default)
     */
    public void setAdmin(boolean admin){
        this.admin = admin;
    }

    /**
     * returns the admin flag
     * @return true if is admin, false otherwise
     */
    public Boolean getAdmin(){
        return admin;
    }

    /**
     * returns the organizer flag
     * @return true if organizer, false otherwise
     */
    public Boolean getOrganizer(){
        return organizer;
    }

    /**
     * sets the organizer flag
     * @param organizer true for organizer(default), false otherwise
     */
    public void setOrganizer(boolean organizer){
        this.organizer=organizer;
    }

    /**
     * returns the violation flat
     * @return true if the organizer has violated a policy by admin decision, false otherwise
     */
    public Boolean getViolation() {return violation;} //CHANGE: CHANGE MADE HERE

    /**
     * sets the violation flag
     * @param violation initially false
     */
    public void setViolation(Boolean violation) {this.violation = violation;} //CHANGE: CHANGE MADE HERE


    /**
     * returns whether user wants to receive winning lottery notifications
     * @return true if enabled, false otherwise
     */
    public Boolean getReceiveWinningNotifs() {
        if (receiveWinningNotifs == null){
            return false;
        }
        return receiveWinningNotifs;
    }

    /**
     * sets the preference for receiving winning lottery notifications
     * @param receiveWinningNotifs true to receive, false to opt out
     */
    public void setReceiveWinningNotifs(Boolean receiveWinningNotifs) {
        this.receiveWinningNotifs = receiveWinningNotifs;
    }

    /**
     * returns whether user wants to receive losing lottery notifications
     * @return true if enabled, false otherwise
     */
    public Boolean getReceiveLosingNotifs() {
        if (receiveLosingNotifs == null){
            return false;
        }
        return receiveLosingNotifs;
    }

    /**
     * sets the preference for receiving losing lottery notifications
     * @param receiveLosingNotifs true to receive, false to opt out
     */
    public void setReceiveLosingNotifs(Boolean receiveLosingNotifs) {
        this.receiveLosingNotifs = receiveLosingNotifs;
    }

    /**
     * returns whether user wants to receive cancelled event notifications
     * @return true if enabled, false otherwise
     */
    public Boolean getReceiveCancelledNotifs(){
        if (receiveCancelledNotifs == null){
            return false;
        }
        return receiveCancelledNotifs;
    }

    /**
     * sets the preference for receiving cancelled event notifications
     * @param receiveCancelledNotifs true to receive, false to opt out
     */
    public void setReceiveCancelledNotifs(Boolean receiveCancelledNotifs) {
        this.receiveCancelledNotifs = receiveCancelledNotifs;
    }

    /**
     * returns whether user wants to receive organizer messages
     * @return true if enabled, false otherwise
     */
    public Boolean getReceiveOrganizerNotifs() {
        if (receiveOrganizerNotifs == null){
            return false;
        }
        return receiveOrganizerNotifs;
    }

    /**
     * sets the preference for receiving organizer messages
     * @param receiveOrganizerNotifs true to receive, false to opt out
     */
    public void setReceiveOrganizerNotifs(Boolean receiveOrganizerNotifs) {
        this.receiveOrganizerNotifs = receiveOrganizerNotifs;
    }

    /**
     * returns whether user wants to receive admin notifications
     * @return true if enabled, false otherwise
     */
    public Boolean getReceiveAdminNotifs() {
        if (receiveAdminNotifs == null){
            return false;
        }
        return receiveAdminNotifs;
    }

    /**
     * sets the preference for receiving admin notifications
     * @param receiveAdminNotifs true to receive, false to opt out
     */
    public void setReceiveAdminNotifs(Boolean receiveAdminNotifs) {
        this.receiveAdminNotifs = receiveAdminNotifs;
    }
}


