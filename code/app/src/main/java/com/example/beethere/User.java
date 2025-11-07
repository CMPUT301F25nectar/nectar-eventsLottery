package com.example.beethere;

/**
 * This is a class that defines a user profile for entrant/organizer/admin profile data used across the app.
 */
public class User {
    private String name;
    private String email;
    private String phone; //optional
    private String deviceid;
    private Boolean admin;
    private Boolean organizer; //admin flag

    public User (String name, String email){
        this.name = name;
        this.email = email;
    }
    public String getName(){
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public String getPhone(){
        return phone;
    }
    public void setPhone(String phone){
        this.phone=phone;
    }
    public String getDeviceid(){
        return deviceid;
    }
    public void setDeviceid(String deviceid){
        this.deviceid = deviceid;
    }
    public boolean isAdmin(){
        return admin;
    }
    public void setAdmin(boolean admin){
        this.admin = admin;
    }
    public Boolean getOrganizer() {return organizer;}
    public void setOrganizer(Boolean organizer) {this.organizer = organizer;}
}
