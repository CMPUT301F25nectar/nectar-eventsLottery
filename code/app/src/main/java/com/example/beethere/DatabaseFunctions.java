package com.example.beethere;

import android.app.ProgressDialog;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.beethere.eventclasses.Event;
import com.example.beethere.eventclasses.UserListManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.beethere.eventclasses.Event;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseFunctions {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "Error";


    public void addEventDB(Event event){
        CollectionReference events = db.collection("Events");
        DocumentReference docref = events.document(event.getEventID());
        docref.set(event).addOnSuccessListener(unused -> Log.d("AddEvent", "Event created successfully"))
                .addOnFailureListener(fail -> Log.d(TAG, "Error creating account"));
    }

    public void getEventDB(){}

    public void addUserDB(){} // this has been done, will transfer later

    public void editUserDB(){} // this has been done, will transfer later

    public void getUserDB() {}// this has been done, will transfer later

    // future javadoc comments
    //* @param regStart UNUSED CURRENTLY The day registration to waitlist opens
    //     * @param regEnd UNUSED CURRENTLY The day registration to waitlist ends
    //     * @param eventDateStart UNUSED CURRENTLY The day the event starts
    //     * @param eventDateEnd UNUSED CURRENTLY The day the event ends
    //     * @param eventTimeStart UNUSED CURRENTLY The time the event starts
    //     * @param eventTimeEnd UNUSED CURRENTLY The time the event ends
    //     * however we're handling tags (??)
//
//    /**
//     * This methods returns all the events created
//     * This can either be all events or events filtered by the user
//     * This is intended to only be used for the "All Events" page
//     * @param filter True if any filter is made, False if viewing all events
//     * @param waitlistID User class of user if they don't want events that they've already added to waitlist
//     * @param callback Database Callback to return the database
//     */
//    public void getEventsDB(Boolean filter, User waitlistID, DatabaseCallback<ArrayList<Event>> callback) {
//
//        CollectionReference events = db.collection("Events");
//        ArrayList<Event> eventArrayList = new ArrayList<>();
//
//        if (filter == Boolean.FALSE){
//            // return
//            events.get().addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        eventArrayList.add(document.toObject(Event.class));
//                    }
//                    callback.onCallback(eventArrayList);
//                } else {
//                    Log.d(TAG, "Error getting documents: ", task.getException());
//                    callback.onError(task.getException());
//                }
//            });
//        } else {
//            events.get().addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        Event event = document.toObject(Event.class);
//                        UserListManager userlist = event.getEntrantList();
//                        // Assuming 'event.getWaitlistUserIds()' returns your ArrayList<String>
//                        if (!userlist.getWaitlist().contains(waitlistID)) {
//                            eventArrayList.add(event);
//                        }
//                    }
//                    callback.onCallback(eventArrayList);
//                } else {
//                    Log.d(TAG, "Error getting documents: ", task.getException());
//                    callback.onError(task.getException());
//                }
//            });
//        }
//    }
//
//    /**
//     * This methods returns the events a user has waitlisted
//     * @param waitlistID User class of user if they don't want events that they've already added to waitlist
//     * @param callback Database Callback to return the database
//     */
//    public void getWaitlistEventsDB(User waitlistID, DatabaseCallback<ArrayList<Event>> callback){
//        CollectionReference events = db.collection("Events");
//        ArrayList<Event> eventArrayList = new ArrayList<>();
//
//        events.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                for (QueryDocumentSnapshot document : task.getResult()) {
//                    Event event = document.toObject(Event.class);
//                    UserListManager userlist = event.getEntrantList();
//                    // Assuming 'event.getWaitlistUserIds()' returns your ArrayList<String>
//                    if (userlist.getWaitlist().contains(waitlistID)) {
//                        eventArrayList.add(event);
//                    }
//                }
//                callback.onCallback(eventArrayList);
//            } else {
//                Log.d(TAG, "Error getting documents: ", task.getException());
//                callback.onError(task.getException());
//            }
//        });
//
//    }

    /**
     * This methods returns the events a user has created
     * @param waitlistID User class of user who is the organizer
     * @param callback Database Callback to return the database
     */
    public void getCreatedEventsDB(User waitlistID, DatabaseCallback<ArrayList<Event>> callback){
        CollectionReference events = db.collection("Events");
        ArrayList<Event> eventArrayList = new ArrayList<>();

        events.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Event event = document.toObject(Event.class);
                    // Assuming 'event.getWaitlistUserIds()' returns your ArrayList<String>
                    if (event.getOrganizer() == waitlistID) {
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

    public void addNotifsDB(){}// this has been done, will transfer later
    public void getNotifsDB(){} // this has been done, will transfer later

    public void uploadImageDB(Uri image){
        /*CollectionReference images = db.collection("Images");
        // Check if an image has been selected
        if (image != null) {
            // Create and show a progress dialog during upload
            // Create a unique path under 'images/' using UUID
            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());

            // Upload the file to Firebase Storage
            ref.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Dismiss the dialog and show success message
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Dismiss the dialog and show failure message
                        Log.d(TAG, "Error couldn't upload image");
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        // Calculate and update progress percentage in the dialog
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    });
        } else {
            // Show message if no image is selected
            Toast.makeText(this, "No image selected!", Toast.LENGTH_SHORT).show();
        }*/
    }

    public void deleteImageDB(){}
}









