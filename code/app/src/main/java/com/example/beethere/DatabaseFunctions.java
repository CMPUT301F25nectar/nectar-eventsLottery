package com.example.beethere;

import com.example.beethere.eventclasses.UserListManager;
import com.example.beethere.notifications_classes.Notification;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.example.beethere.eventclasses.Event;

public class DatabaseFunctions {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "Error";

    // add an event to the database
    public void addEventDB(Event event){
        CollectionReference events = db.collection("events");
        DocumentReference docref = events.document(event.getEventID());
        docref.set(event).addOnSuccessListener(unused -> Log.d("AddEvent", "Event created successfully"))
                .addOnFailureListener(fail -> Log.d(TAG, "Error creating event"));
    }

    /**
     * This methods can be used to add new users or edit existing ones
     * @param user User class of the user that needs to be added or edited
     */
    public void addUserDB(User user){
        CollectionReference users = db.collection("users");
        DocumentReference docref = users.document(user.getDeviceid());
        // check if user already exists
        docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // if they do exist then the admin and organizer flags must stay consistent
                        User existing = document.toObject(User.class);
                        User u = new User();
                        u.setName(user.getName());
                        u.setEmail(user.getEmail());
                        u.setPhone(user.getPhone());
                        u.setDeviceid(user.getDeviceid());
                        u.setAdmin(existing.getAdmin());
                        u.setOrganizer(existing.getOrganizer());
                        docref.set(u).addOnSuccessListener(unused -> Log.d("AddUser", "User edited successfully"))
                                .addOnFailureListener(fail -> Log.d(TAG, "Error editing account"));

                    } else {
                        // if they don't exist it will stick to the presets of not an admin and is an organizer
                        docref.set(user).addOnSuccessListener(unused -> Log.d("AddUser", "User created successfully"))
                                .addOnFailureListener(fail -> Log.d(TAG, "Error creating account"));
                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                }
            }
        });

    }

    /*public void deleteUserDB(String userID){
        CollectionReference users = db.collection("users");
        DocumentReference docref = users.document(userID);

        ArrayList<Event> eventList = new ArrayList<>();

        // delete user notifs, notifcallback needs to be made before the time it is used
        DatabaseCallback<List<Notification>> notifCallback = new DatabaseCallback<List<Notification>>() {
            @Override
            public void onCallback(List<Notification> result) {
                deleteUserNotifs(result, userID);
            }

            @Override
            public void onError(Exception e) {
                Log.d("DatabaseFunction", "DatabaseFunctions error getting notifs from database");
            }
        };

        // delete user from event lists, and delete their events
        // userCallback needs to be made before it is used
        // user notifCallback
        DatabaseCallback<User> userCallback = new DatabaseCallback<User>() {
            @Override
            public void onCallback(User result) {
                checkDeleteUserDB(eventList, result);
                getNotifsDB(userID, notifCallback);
            }
            @Override
            public void onError(Exception e) {
                Log.d("DatabaseFunction", "DatabaseFunctions error getting user from database");
            }
        };

        // callback for getting eventlist
        // use userCallback
        DatabaseCallback<ArrayList<Event>> eventCallback = new DatabaseCallback<>() {
            @Override
            public void onCallback(ArrayList<Event> result) {
                eventList.addAll(result);
                getUserDB(userID, userCallback);
            }
            @Override
            public void onError(Exception e) {
                Log.d("DatabaseFunction", "DatabaseFunctions error getting events from database");
            }
        };

        getEventsDB(eventCallback);

        docref.delete().addOnSuccessListener(unused -> Log.d("DeleteUser", "User deleted successfully"))
                .addOnFailureListener(fail -> Log.d(TAG, "Error deleting account"));
    }

    private void checkDeleteUserDB(ArrayList<Event> eventList, User user){
        UserListManager manager = new UserListManager();
        for (Event event: eventList){
            manager.setEvent(event);
            if (manager.inWaitlist(user)) {
                manager.removeWaitlist(user);
            } else if (manager.inInvite(user)){
                manager.removeInvite(user);
            } else if (manager.inRegistered(user)){
                manager.removeRegistered(user);
            }

            if (event.getOrganizer().getDeviceid() == user.getDeviceid()) {
                deleteEventDB(event.getEventID());
            }
        }
    }*/

    // future javadoc comments
    //* @param regStart UNUSED CURRENTLY The day registration to waitlist opens
    //     * @param regEnd UNUSED CURRENTLY The day registration to waitlist ends
    //     * @param eventDateStart UNUSED CURRENTLY The day the event starts
    //     * @param eventDateEnd UNUSED CURRENTLY The day the event ends
    //     * @param eventTimeStart UNUSED CURRENTLY The time the event starts
    //     * @param eventTimeEnd UNUSED CURRENTLY The time the event ends
    //     * however we're handling tags (??)

    /**
     * This methods returns all the events created
     * This can either be all events or events filtered by the user
     * This is intended to only be used for the "All Events" page
     * @param callback Database Callback to return the database
     */
    public void getEventsDB(DatabaseCallback<ArrayList<Event>> callback) {
        CollectionReference events = db.collection("events");
        ArrayList<Event> eventArrayList = new ArrayList<>();

        events.get().addOnCompleteListener(task -> {
            if(!task.isSuccessful()){
                callback.onError(task.getException());
                return;
            }
            for(QueryDocumentSnapshot document : task.getResult()) {
                Event event = document.toObject(Event.class);
                eventArrayList.add(event);
            }
            callback.onCallback(eventArrayList);
        });
    }

    /**
     * This methods returns the events a user has created
     * @param waitlistID User class of user who is the organizer
     * @param callback Database Callback to return the database
     */
    public void getCreatedEventsDB(User waitlistID, DatabaseCallback<ArrayList<Event>> callback){
        CollectionReference events = db.collection("events");
        ArrayList<Event> eventArrayList = new ArrayList<>();

        events.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Event event = document.toObject(Event.class);
                    // Assuming 'event.getWaitlistUserIds()' returns your ArrayList<String>
                    if (Objects.equals(event.getOrganizer().getDeviceid(), waitlistID.getDeviceid())) {
                        eventArrayList.add(event);
                    }
                }
                callback.onCallback(eventArrayList);
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
                callback.onError(task.getException());
            }
        });

    }

    /**
     * Saves a notification to Firebase Firestore
     * @param notif The notification object to save
     */
    public void addNotifsDB(Notification notif){
        CollectionReference notifications = db.collection("notifications");
        notifications.add(notif).addOnSuccessListener(unused -> Log.d("AddNotif", "Notif created successfully"))
                .addOnFailureListener(fail -> Log.d(TAG, "Error creating notif"));
    }

    public void getUserDB(String userid, DatabaseCallback<User>callback){
        CollectionReference users = db.collection("users");
        DocumentReference docref = users.document(userid);
        docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        callback.onCallback(document.toObject(User.class));
                    } else {
                        callback.onError(task.getException());
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    callback.onError(task.getException());
                }
            }
        });
    }

    // possible fields: waitList, registered
    public void addUserToEventDB(Event event, User user, String field){
        CollectionReference events = db.collection("events");
        DocumentReference docref = events.document(event.getEventID());
        docref.update(field, FieldValue.arrayUnion(user)).addOnSuccessListener(unused -> Log.d("AddUser", "User added to event successfully"))
                .addOnFailureListener(fail -> Log.d(TAG, "Error adding user to event"));
    }

    public void removeUserFromEventDB(Event event, User user, String field){
        CollectionReference events = db.collection("events");
        DocumentReference docref = events.document(event.getEventID());
        docref.update(field, FieldValue.arrayRemove(user)).addOnSuccessListener(unused -> Log.d("AddUser", "User removed from event successfully"))
                .addOnFailureListener(fail -> Log.d(TAG, "Error removing user from event"));
    }

    //Map<String, Boolean>
    public void addInviteDB(Event event, String userID, Boolean interact){
        CollectionReference events = db.collection("events");
        DocumentReference docref = events.document(event.getEventID());
        Map<String, Object> updates = new HashMap<>();
        updates.put("invited." + userID, interact); // NOTE not sure if it should be set to false or true on default
        docref.update(updates).addOnSuccessListener(unused -> Log.d("AddUser", "User added to event invited successfully"))
                .addOnFailureListener(fail -> Log.d(TAG, "Error adding user to event invited"));
    }

    public void removeInviteDB(Event event, User user){

        CollectionReference events = db.collection("events");
        DocumentReference docref = events.document(event.getEventID());
        String id = "invited." + user.getDeviceid();
        docref.update(id, FieldValue.delete());
    }

    public void getNotifsDB(String deviceId, DatabaseCallback<List<Notification>> callback){
        CollectionReference notifcol = db.collection("notifications");
        notifcol.whereArrayContains("deviceIds", deviceId)
                //.orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        callback.onError(error);
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        List<Notification> notifications = new ArrayList<>();
                        for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                            Notification notif = queryDocumentSnapshots.getDocuments().get(i).toObject(Notification.class);
                            if (notif != null) {
                                notifications.add(notif);
                            }
                        }
                        callback.onCallback(notifications);
                    }
                });
    }

}









