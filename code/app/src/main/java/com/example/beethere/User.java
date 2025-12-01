package com.example.beethere;


/**
 * This is a model that defines a user profile for entrant/organizer/admin profile data used across the app.
 */






public class User {

    private String name;
    private String email;
    private String phone; //optional
    private String deviceid;
    private Boolean admin; //admin flag
    private Boolean organizer;//organizer flag
    private Boolean violation;

    public User(){}

    /**
     *Constructor with mandatory fields needed for a profile
     * @param name user full name
     * @param email user email address
     */
    public User (String name, String email){
        this.name = name;
        this.email = email;
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
    public void setAdmin(Boolean admin){
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
    public void setOrganizer(Boolean organizer){
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
}


