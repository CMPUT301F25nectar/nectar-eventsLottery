package com.example.beethere;

import android.app.ProgressDialog;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.beethere.eventclasses.Event;
import com.example.beethere.eventclasses.UserListManager;
import com.example.beethere.notifications_classes.Notification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.beethere.eventclasses.Event;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class DatabaseFunctions {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "Error";


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

    public void deleteUserDB(String user){
        CollectionReference users = db.collection("users");
        DocumentReference docref = users.document(user);
        docref.delete().addOnSuccessListener(unused -> Log.d("DeleteUser", "User deleted successfully"))
                .addOnFailureListener(fail -> Log.d(TAG, "Error deleting account"));
    }

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
     * @param filter True if any filter is made, False if viewing all events
     * @param callback Database Callback to return the database
     */
    public void getEventsDB(Boolean filter, DatabaseCallback<ArrayList<Event>> callback) {
        CollectionReference events = db.collection("events");
        ArrayList<Event> eventArrayList = new ArrayList<>();

        events.get().addOnCompleteListener(task -> {
            if(!task.isSuccessful()){
                callback.onError(task.getException());
                return;
            }
            for(QueryDocumentSnapshot document : task.getResult()) {
                Event event = document.toObject(Event.class);
                if(filter == Boolean.TRUE){
                    /*if(regStart != null && event.getRegStart() != regStart){
                        continue;
                    }
                    if(regEnd != null && event.getRegStart() != regEnd){
                        continue;
                    }
                    if(eventDateStart != null && event.getEventDateStart() != eventDateStart){
                        continue;
                    }
                    if(eventDateEnd != null && event.getEventDateEnd() != eventDateEnd){
                        continue;
                    }
                    if(eventTimeStart != null && event.getEventTimeStart() != eventTimeStart){
                        continue;
                    }
                    if(eventTimeEnd != null && event.getEventTimeEnd() != eventTimeEnd){
                        continue;
                    }
                    if (!userlist.getWaitlist().contains(waitlistID)) {
                        continue;
                    }
                    */
                }
                eventArrayList.add(event);
            }
            callback.onCallback(eventArrayList);
        });
    }

    /**
     * This methods returns the events a user has waitlisted
     * @param waitlistID User class of user if they don't want events that they've already added to waitlist
     * @param callback Database Callback to return the database
     */
    public void getWaitlistEventsDB(User waitlistID, DatabaseCallback<ArrayList<Event>> callback){
        CollectionReference events = db.collection("events");
        ArrayList<Event> eventArrayList = new ArrayList<>();

        events.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Event event = document.toObject(Event.class);
                    UserListManager userlist = new UserListManager(event);
                    // Assuming 'event.getWaitlistUserIds()' returns your ArrayList<String>
                    if (userlist.inWaitlist(waitlistID)) {
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









