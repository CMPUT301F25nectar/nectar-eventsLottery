package com.example.beethere;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.example.beethere.eventclasses.Event;


public class DatabaseFunctions {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "Error";


    /*public void addEventDB(Event event){
        CollectionReference events = db.collection("events");
        DocumentReference docref = events.document(event.getEventID());
        docref.set(event);
    }*/

    public void getEventDB(){}

    public void addUserDB(){} // this has been done, maybe transfer (?)

    public void editUserDB(){}

    /**
     * This methods returns the info profiles need to display a user
     * @param id the user's id to find in list of profiles
     */
    public void getUserDB(String id) { // comment code for reference
        /*
        // Create a reference to the users collection
        * CollectionReference users = db.collection("users");
        *

        // Create a query against the collection.
        Query query = users.whereEqualTo("id", id);
        *
        * // call said query
        * db.collection("users").whereEqualTo("capital", true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                    // result here (return ? updated ui ? etc)
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        *
        * */
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
     * @param waitlistID ID of user if they don't want events that they've already added to waitlist
     */
    public void getEventsDB(Boolean filter, String waitlistID, DatabaseCallback<ArrayList<Event>> callback) {

        CollectionReference events = db.collection("events");
        ArrayList<Event> eventArrayList = new ArrayList<>();

        if (filter == Boolean.FALSE){
            // return
            events.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        eventArrayList.add(document.toObject(Event.class));
                    }
                    callback.onCallback(eventArrayList);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    callback.onError(task.getException());
                }
            });
        } else {
        // Create a query against the collection.
        Query query = events.whereEqualTo("id", waitlistID);
        // call said query
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    eventArrayList.add(document.toObject(Event.class));
                }
                callback.onCallback(eventArrayList);
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
                callback.onError(task.getException());
            }
        });
        }


    }


    /**
     * This methods returns the notifications for the user
     * @param id the user's id to find all the notifications pertaining them
     */
    public void getNotifsDB(String id){
        /*
        // Create a reference to the users collection
        * CollectionReference users = db.collection("Users");
        *

        // Create a query against the collection.
        Query query = notif.whereArrayContains("deviceIds", id);
        *
        * // call said query
        * query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                    // result here (return ? updated ui ? etc)
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        *
        * */
    }

    public void uploadImageDB(){}

    public void deleteImageDB(){}
}









