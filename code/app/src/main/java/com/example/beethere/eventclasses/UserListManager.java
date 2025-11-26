package com.example.beethere.eventclasses;

import com.example.beethere.User;
import com.example.beethere.notifications_classes.NotificationHandler;
import com.example.beethere.notifications_classes.Notification;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

/**
 *
 */
public class UserListManager {

    private Event event;
    private Random random;

    // Constructors

    public UserListManager (Event event){
        this.event = event;
        this.random = new Random();
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
    }

    /**
     * This removes a user from the event's waitlist
     * @param user the user to be removed
     */
    public void removeWaitlist(User user) {
        // TODO
        // check if user in waitlist
        if(inWaitlist(user)) {event.getWaitList().remove(user);}

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
            event.getInvited().put(user.getName(), Boolean.TRUE);
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
            event.getInvited().remove(user.getName());
        }
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
        }
    }

    /**
     * This removes a user from the registered list
     * @param user the user to be removed
     */
    public void removeRegistered(User user){
        // check if user is in registered
        event.getRegistered().remove(user);
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
        if(event.autoRandomSelection){
            selectNewInvite();
        }
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

        if(waitlistSize() < event.getEntrantMax()) range = waitlistSize();

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

    public Boolean inWaitlist(User user) {
        return event.getWaitList().contains(user);
    }

    public boolean inInvite(User user) {
        return event.getInvited().containsKey(user);
    }

    public Boolean isDeclined(User user) {
        return event.getInvited().get(user);
    }

    public Boolean inRegistered(User user){
        return event.getRegistered().contains(user);
    }

    public Boolean waitlistFull() {
        if (event.getMaxWaitlist() > waitlistSize()) return Boolean.FALSE;
        return Boolean.TRUE;
    }

    /**
     * Creates CSV file of users in registered list
     * @throws IOException
     *          if fails to create file
     *          if fails to create writer
     *          if fails to write with writer
     *          if fails to close writer
     */
    public void exportCSV() throws IOException {
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
