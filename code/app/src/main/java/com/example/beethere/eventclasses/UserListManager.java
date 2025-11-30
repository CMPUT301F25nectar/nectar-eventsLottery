package com.example.beethere.eventclasses;

import com.example.beethere.DatabaseFunctions;
import com.example.beethere.User;
import com.example.beethere.notifications_classes.NotificationHandler;
import com.example.beethere.notifications_classes.Notification;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 *
 */
public class UserListManager {

    private Event event;
    private Random random;
    private DatabaseFunctions dbFunctions;

    // Constructors

    /**
     * Empty constructor so that the class can be reused
     * initializes random
     * initializes database functions
     */
    public UserListManager (){
        this.random = new Random();
        this.dbFunctions = new DatabaseFunctions();
    }

    /**
     * Non-empty constructor to be initialized with specific event
     * @param event
     *          The event that the manager is working
     */
    public UserListManager (Event event){
        this.event = event;
        this.random = new Random();
        this.dbFunctions = new DatabaseFunctions();
    }

    //Getters and Setter

    public Event getEvent() {
        return event;
    }
    public void setEvent(Event event) {
        this.event = event;
    }

    public Random getRandom() {
        return random;
    }
    public void setRandom(Random random) {
        this.random = random;
    }

    public DatabaseFunctions getDbFunctions() {
        return dbFunctions;
    }
    public void setDbFunctions(DatabaseFunctions dbFunctions) {
        this.dbFunctions = dbFunctions;
    }

// Waitlist Management
    /**
     * Finds and returns the size of the waitlist
     * @return the size of the waitlist
     */
    public Integer waitlistSize(){
        return event.getWaitList().size();
    }

    /**
     * This adds a user to an the event's waitlist
     * @param user: the user to be added
     */
    public void addWaitlist(User user) {

        if (waitlistSize() < event.getMaxWaitlist() && !inWaitlist(user)) {
            event.getWaitList().add(user);
        }
        dbFunctions.addUserToEventDB(event, user, "waitList");
    }

    /**
     * This removes a user from the event's waitlist
     * @param user the user to be removed
     */
    public void removeWaitlist(User user) {

        ArrayList<User> waitlist = event.getWaitList();
        // this looks really complex bc of
        // diff objects existing while having the same field
        // are still diff objects
        // need to make sure the right object is deleted
        if (this.inWaitlist(user)) {
            for (User waitlistUser : waitlist) {
                if (waitlistUser.getDeviceid() == user.getDeviceid()) {
                    event.getWaitList().remove(waitlistUser);
                    dbFunctions.removeUserFromEventDB(event, waitlistUser, "waitList");
                    break;
                }
            }
        }
    }

    /**
     * Checks if user is in waitlist
     * @param user
     *          The user who is being checked if they are in the waitlist
     * @return
     *      Boolean, true/false if they are in or not
     */
    public Boolean inWaitlist(User user) {
        User waitlistUser;
        Boolean result = Boolean.FALSE;
        for (int i = 0; i < this.waitlistSize(); i++){
            waitlistUser = event.getWaitList().get(i);
            if (Objects.equals(waitlistUser.getDeviceid(), user.getDeviceid())){
                result = Boolean.TRUE;
            }
        }

        return result;
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
            event.getInvited().put(user.getDeviceid(), Boolean.TRUE);
            dbFunctions.addInviteDB(event, user.getDeviceid(), Boolean.TRUE);
            removeWaitlist(user);
        }
    }

    /**
     * This removes a user from the event's invite list
     * @param user the user to be removed
     */
    public void removeInvite(User user){
        // check if user has been invited/in invite list
        if(inInvite(user)){
            event.getInvited().remove(user.getDeviceid());
            dbFunctions.removeInviteDB(event, user.getDeviceid());
        }
    }

    /**
     * Checks if user is in invite
     * @param user
     *          The user who is being checked if they are in the invited
     * @return
     *      Boolean, true/false if they are in or not
     */
    public boolean inInvite(User user) {
        return event.getInvited().containsKey(user.getDeviceid());
    }

    // Registered list management
    /**
     * This adds a user to the registered list
     * @param user the user to be added
     */
    private void addRegistered(User user){
        // check if user is in registered
        if(event.getEntrantMax() > event.getRegistered().size()){
            event.getRegistered().add(user);
            dbFunctions.addUserToEventDB(event, user, "registered");
        }
    }

    /**
     * This removes a user from the registered list
     * @param user the user to be removed
     */
    public void removeRegistered(User user){
        // check if user is in registered
        ArrayList<User> registered = event.getRegistered();

        // this looks really complex bc of
        // diff objects existing while having the same field
        // are still diff objects
        // need to make sure the right object is deleted
        if (this.inRegistered(user)){
            for (User registeredUser : registered){
                if (registeredUser.getDeviceid() == user.getDeviceid()){
                    event.getRegistered().remove(registeredUser);
                    dbFunctions.removeUserFromEventDB(event, registeredUser, "registered");
                    break;
                }
            }
        }
    }

    /**
     * Checks if user is in registered list
     * @param user
     *       The user who is being checked if they are in the registered
     *  @return
     *      Boolean, true/false if they are in or not
     */
    public Boolean inRegistered(User user){
        Boolean result = Boolean.FALSE;
        User registeredUser;
        ArrayList<User> registered = event.getRegistered();
        for (int i = 0; i < registered.size(); i++){
            registeredUser = registered.get(i);
            if (Objects.equals(registeredUser.getDeviceid(), user.getDeviceid())){
                result = Boolean.TRUE;
                break;
            }
        }
        return result;
    }

    /**
     * This adds the user to the registered list
     * and remove them from the invite list
     * @param user the user who is move from one list to the other
     */
    public void acceptInvite(User user){
        if(event.getEntrantMax() > event.getRegistered().size()) {
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
        event.getInvited().replace(user.getName(), Boolean.FALSE);
        dbFunctions.addInviteDB(event, user.getDeviceid(), Boolean.FALSE);
        if(event.autoRandomSelection){
            selectNewInvite();
        }
    }

    /**
     * Checks if user has declined their invite
     * @param user
     * @return
     */
    public Boolean isDeclined(User user) {
        Boolean result = Boolean.FALSE;
        Map<String, Boolean> invited = event.getInvited();

        // user is in invite list
        // and user has declined invite (Boolean.FALSE)
        if (inInvite(user) && invited.get(user.getDeviceid()) == Boolean.FALSE) {
            result = Boolean.TRUE;
        }

        return result;
    }

    /**
     * Selects a random user from the waitlist and adds them to the invite list
     */
    public void selectNewInvite(){
        User user = event.getWaitList().get(random.nextInt(waitlistSize()));
        addInvite(user);
    }

    /**
     * Randomly selects max number of users who can register
     * from the waitlist and adds them to the invite list
     */
    public void selectInvitations(Integer range){
        // if the registered list would be overfilled if all invites sent were accepted, don't do it

        if(waitlistSize() < range) range = waitlistSize();

        for(int i = 0; i < range; i++){
            selectNewInvite();
        }

        // Send lottery notifications
        NotificationHandler notificationHandler = new NotificationHandler();
        notificationHandler.sendLotteryNotifications(
                event.getEventID(),
                event.getTitle(),
                event.getInvited(),
                event.getWaitList(),
                event.getOrganizer().getDeviceid()
        );
    }

    public Boolean waitlistFull() {
        Boolean result = Boolean.FALSE;
        //return Boolean.FALSE;
        if(event.getWaitList() == null || event.getMaxWaitlist() == null) {
            result = Boolean.FALSE;
        }  else if (event.getMaxWaitlist() > waitlistSize()) {
            result = Boolean.FALSE;
        } else if (event.getMaxWaitlist() == waitlistSize()){
            result = Boolean.TRUE;
        }
        return result;
    }

    /**
     * Creates CSV file of users in registered list
     * @throws IOException
     *          if fails to create file
     *          if fails to create writer
     *          if fails to write with writer
     *          if fails to close writer
     */
    public void exportCSV() throws IOException { //TODO this doesnt work, crashes after with read only exception
        File file = new File("Registered.csv");
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        writer.write("name,email,phone\n");

        for (User user : event.getRegistered()){
            String phone = "";
            if (user.getPhone() != null) phone = user.getPhone();
            writer.write(
                    user.getName() + "," +
                            user.getEmail() + "," +
                            phone +
                            "\n");
        }
        writer.close();
    }


}
