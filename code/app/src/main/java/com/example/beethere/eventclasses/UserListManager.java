package com.example.beethere.eventclasses;

import com.example.beethere.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

/**
 *
 */
public class UserListManager {

    private Integer maxWaitlist;
    private ArrayList<User> waitlist;

    private HashMap<User, Boolean> inviteList;
    private Boolean autoSelect;

    private ArrayList<User> registered;
    private Integer maxRegistered;

    private Random random;

    // Constructors
    /**
     * This initializes the 3 different types of user lists associated with an event
     * and a random object.
     * It defines and saves the max number of people who can be registered.
     * It defines and saves the max number of people who can join the waitlist.
     * @param maxRegistered The max number of people of who can register/enroll
     * @param maxWaitlist The max number of people who can join the waitlist
     */
    public UserListManager(Boolean autoSelect,int maxRegistered, int maxWaitlist){
        this.maxWaitlist = maxWaitlist;
        this.waitlist = new ArrayList<User>();

        this.inviteList = new HashMap<User, Boolean>();
        this.autoSelect = autoSelect;

        this.maxRegistered = maxRegistered;
        this.registered = new ArrayList<User>();

        this.random = new Random();
    }

    /**
     * This initializes the 3 different types of user lists associated with an event
     * and a random object.
     * It defines and saves the max number of people who can be registered.
     * It defines and saves the max number of people who can join the waitlist as the max Integer Value.
     * @param maxRegistered the max number of people who can register/enroll
     */
    public UserListManager(Boolean autoSelect, int maxRegistered){
        this.maxWaitlist = Integer.MAX_VALUE;
        this.waitlist = new ArrayList<User>();

        this.inviteList = new HashMap<User, Boolean>();
        this.autoSelect = autoSelect;

        this.maxRegistered = maxRegistered;
        this.registered = new ArrayList<User>();

        this.random = new Random();
    }

    //Getters and Setter
    public Integer getMaxWaitlist() {
        return maxWaitlist;
    }

    public void setMaxWaitlist(Integer maxWaitlist) {
        this.maxWaitlist = maxWaitlist;
    }

    public ArrayList<User> getWaitlist() {
        return waitlist;
    }

    public void setWaitlist(ArrayList<User> waitlist) {
        this.waitlist = waitlist;
    }

    public HashMap<User, Boolean> getInviteList() {
        return inviteList;
    }

    public void setInviteList(HashMap<User, Boolean> inviteList) {
        this.inviteList = inviteList;
    }

    public ArrayList<User> getRegistered() {
        return registered;
    }

    public void setRegistered(ArrayList<User> registered) {
        this.registered = registered;
    }

    public Integer getMaxRegistered() {
        return maxRegistered;
    }

    public void setMaxRegistered(Integer maxRegistered) {
        this.maxRegistered = maxRegistered;
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public Boolean getAutoSelect() {
        return autoSelect;
    }

    public void setAutoSelect(Boolean autoSelect) {
        this.autoSelect = autoSelect;
    }


// Waitlist Management
    /**
     * Finds and returns the size of the waitlist
     * @return the size of the waitlist
     */
    public Integer waitlistSize(){
        return waitlist.size();
    }

    /**
     * This adds a user to an the event's waitlist
     * @param user: the user to be added
     */
    public void addWaitlist(User user) {

        if (waitlistSize() < maxWaitlist && !inWaitlist(user)) {
            waitlist.add(user);
        }
    }

    /**
     * This removes a user from the event's waitlist
     * @param user the user to be removed
     */
    public void removeWaitlist(User user) {
        // TODO
        // check if user in waitlist
        if(inWaitlist(user)) {waitlist.remove(user);}

    }

    // Invite list management
    /**
     * This adds a user to the event's invite list
     * @param user the user to be added
     */
    // option to make this private
    public void addInvite(User user){
        // must be in waitlist to get invite in the first place
        if(inWaitlist(user)){
            inviteList.put(user, Boolean.TRUE);
            removeWaitlist(user);
        }
        // option to also automatically send the invite notif
    }

    /**
     * This removes a user from the event's invite list
     * @param user the user to be removed
     */

    private void removeInvite(User user){
        // check if user has been invited/in invite list
        if(inInvite(user)){

        }
        inviteList.remove(user);
    }

    // Registered list management
    /**
     * This adds a user to the registered list
     * @param user the user to be added
     */
    private void addRegistered(User user){
        // check if user is in registered
        if(maxRegistered > registered.size()){
            registered.add(user);
        }
    }

    /**
     * This removes a user from the registered list
     * @param user the user to be removed
     */
    public void removeRegistered(User user){
        // check if user is in registered
        registered.remove(user);
    }

    /**
     * This adds the user to the registered list
     * and remove them from the invite list
     * @param user the user who is move from one list to the other
     */
    public void acceptInvite(User user){
        if(maxRegistered > registered.size()) {
            addRegistered(user);
            removeInvite(user);
        }
    }

    /**
     * This changes the status of the user in the invite list
     * from Boolean True (has not accepted or declined invitation)
     * to Boolean False (has declined invitation)
     * @param user the user whose status is to be change
     */
    public void declineInvite(User user){
        inviteList.replace(user, Boolean.FALSE);
        if(autoSelect){
            selectNewInvite();
        }
    }

    /**
     * Selects a random user from the waitlist and adds them to the invite list
     */
    public void selectNewInvite(){
        User user = waitlist.get(random.nextInt(waitlistSize()));
        addInvite(user);
    }

    /**
     * Randomly selects max number of users who can register
     * from the waitlist and adds them to the invite list
     */
    public void selectInvitations(){
        Integer range = maxRegistered;
        if(waitlistSize() < maxRegistered) range = waitlistSize();
        for(int i = 0; i < range; i++){
            selectNewInvite();
        }
        
        NotificationHandler notificationHandler = new NotificationHandler();
        notificationHandler.sendLotteryNotifications(
                eventId,
                eventName,
                this.inviteList,    // Winners
                this.waitlist,      // Losers (remaining on waitlist)
                organizerDeviceId
        );
    }

    public Boolean inWaitlist(User user) {
        return waitlist.contains(user);
    }

    public boolean inInvite(User user) {
        return inviteList.containsKey(user);
    }

    public Boolean isDeclined(User user) {
        return inviteList.get(user);
    }

    public Boolean inRegistered(User user){
        return registered.contains(user);
    }
    public Boolean waitlistFull() {
        return (Objects.equals(maxWaitlist, waitlistSize()));
    }

    // TODO
    // export invite list in CSV format
}
