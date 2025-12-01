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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.example.beethere.eventclasses.Event;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

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

    /**
     * This methods deletes users from the database
     * @param user  User object of the user that needs to be deleted
     */
    public void deleteUserDB(User user){
        CollectionReference users = db.collection("users");
        DocumentReference docref = users.document(user.getDeviceid());
        ArrayList<Event> eventList = new ArrayList<>();

        // Get interacted notifs happens last
        DatabaseCallback<List<Notification>> interactedCallback = new DatabaseCallback<List<Notification>>() {
            @Override
            public void onCallback(List<Notification> result) {
                deleteUserFromNotifDB(user.getDeviceid(), result, "respondedDeviceIds");
                docref.delete().addOnSuccessListener(unused -> Log.d("DeleteUser", "User deleted successfully"))
                        .addOnFailureListener(fail -> Log.d(TAG, "Error deleting account"));
            }

            @Override
            public void onError(Exception e) {
                Log.d("DatabaseFunction", "DatabaseFunctions error getting notifs from database");
            }
        };

        // Get Non-interacted notifs happens second
        DatabaseCallback<List<Notification>> notifCallback = new DatabaseCallback<List<Notification>>() {
            @Override
            public void onCallback(List<Notification> result) {
                deleteUserFromNotifDB(user.getDeviceid(), result, "deviceIds");
                getInteractedNotifsDB(user.getDeviceid(), interactedCallback);
            }

            @Override
            public void onError(Exception e) {
                Log.d("DatabaseFunction", "DatabaseFunctions error getting notifs from database");
            }
        };

        // Get Event Callback, gets called first
        DatabaseCallback<ArrayList<Event>> eventCallback = new DatabaseCallback<>() {
            @Override
            public void onCallback(ArrayList<Event> result) {
                eventList.addAll(result);
                checkDeleteUserDB(eventList, user);
                getNotifsDB(user.getDeviceid(), notifCallback);
            }
            @Override
            public void onError(Exception e) {
                Log.d("DatabaseFunction", "DatabaseFunctions error getting events from database");
            }
        };
        getEventsDB(eventCallback);
    }

    /**
     * This method deletes a user from all the events they've interacted with
     * and deletes events the user has created
     * @param eventList All events to sift through
     * @param user User to compare with
     */
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

            if (Objects.equals(event.getOrganizer().getDeviceid(), user.getDeviceid())) {
                deleteEventDB(event.getEventID());
            }
        }
    }

    /**
     * This method returns all the events in the database via callback
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
     * Get a single event by its ID
     * @param eventId The event ID to fetch
     * @param callback Callback with the Event object
     */
    public void getEvent(String eventId, DatabaseCallback<Event> callback) {
        CollectionReference events = db.collection("events");
        DocumentReference docref = events.document(eventId);

        docref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Event event = document.toObject(Event.class);
                    callback.onCallback(event);
                } else {
                    Log.d(TAG, "Event not found: " + eventId);
                    callback.onCallback(null);
                }
            } else {
                Log.d(TAG, "Error getting event: ", task.getException());
                callback.onError(task.getException());
            }
        });
    }

    /**
     * This methods returns the events a user has created
     * @param waitlistID String UserID of user who is the organizer
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

    /**
     * This methods returns user from the database that matches the id specified
     * @param userid String UserID to be found
     * @param callback Database Callback to return the database result
     */
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
    /**
     * This method returns all users in the database via callback
     * @param callback Database Callback to return the list of users
     */
    public void getUsersDB(DatabaseCallback<List<User>> callback) {
        CollectionReference users = db.collection("users");

        users.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<User> userList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User user = document.toObject(User.class);
                        userList.add(user);
                    }
                    callback.onCallback(userList);
                } else {
                    Log.d(TAG, "Error getting users: ", task.getException());
                    callback.onError(task.getException());
                }
            }
        });
    }

    public void updateNotificationPreference(String deviceId, String fieldName, boolean value){
        DocumentReference userRef = db.collection("users").document(deviceId);
        userRef.update(fieldName, value)
                .addOnSuccessListener(aVoid -> Log.d("UpdatePref", fieldName + "updated to" + value))
                .addOnFailureListener(e -> Log.d(TAG, "Error updating" + fieldName, e));
    }

    /**
     * This methods adds user to an event's waitlist or registered list
     * @param event Event object the user wants to interact with
     * @param user User object to be added to the event
     * @param field can be either waitList or registered*/
    public void addUserToEventDB(Event event, User user, String field){
        CollectionReference events = db.collection("events");
        DocumentReference docref = events.document(event.getEventID());
        docref.update(field, FieldValue.arrayUnion(user)).addOnSuccessListener(unused -> Log.d("AddUser", "User added to event successfully"))
                .addOnFailureListener(fail -> Log.d(TAG, "Error adding user to event"));
    }

    /**
     * This methods removes user from an event's waitlist or registered list
     * @param event Event object the user wants to interact with
     * @param user User object to be added to the event
     * @param field can be either waitList or registered*/
    public void removeUserFromEventDB(Event event, User user, String field){
        CollectionReference events = db.collection("events");
        DocumentReference docref = events.document(event.getEventID());
        docref.update(field, FieldValue.arrayRemove(user)).addOnSuccessListener(unused -> Log.d("RemoveUser", "User removed from event successfully"))
                .addOnFailureListener(fail -> Log.d(TAG, "Error removing user from event"));
    }

    /**
     * This methods adds user to an event's invited list
     * can also be called if wanting to edit a user's boolean from true to false
     * @param event Event object the user wants to interact with
     * @param userID User's ID string to be added to the event
     * @param interact True hasn't interacted, False means they declined */
    public void addInviteDB(Event event, String userID, Boolean interact){
        CollectionReference events = db.collection("events");
        DocumentReference docref = events.document(event.getEventID());
        Map<String, Object> updates = new HashMap<>();
        updates.put("invited." + userID, interact); // true hasn't interacted, false means they declined
        docref.update(updates).addOnSuccessListener(unused -> Log.d("AddUser", "User added to event invited successfully"))
                .addOnFailureListener(fail -> Log.d(TAG, "Error adding user to event invited"));
    }

    /**
     * This methods removes user from an event's invited list
     * @param event Event object the user wants to interact with
     * @param userID User's ID string to be added to the event*/
    public void removeInviteDB(Event event, String userID){
        CollectionReference events = db.collection("events");
        DocumentReference docref = events.document(event.getEventID());
        Map<String, Object> updates = new HashMap<>();
        updates.put("invited." + userID, FieldValue.delete());
        docref.update(updates).addOnSuccessListener(unused -> Log.d("RemoveUser", "User removed from event invited successfully"))
                .addOnFailureListener(fail -> Log.d(TAG, "Error removing user from event invited"));
    }

    /**
     * This method returns a user's notifications they haven't interacted with via callback
     * @param deviceId User's ID string to fetch the notifications for
     * @param callback Database Callback to return the database
     */
    public void getNotifsDB(String deviceId, DatabaseCallback<List<Notification>> callback){
        CollectionReference notifcol = db.collection("notifications");
        notifcol.whereArrayContains("deviceIds", deviceId)
                //.orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(( task) -> {
                    if (task.isSuccessful()) {
                        List<Notification> notifications = new ArrayList<>();
                        for (QueryDocumentSnapshot document: task.getResult()){
                            Notification notif = document.toObject(Notification.class);
                            if (notif != null) {
                                notifications.add(notif);
                            }
                        }
                        Collections.sort(notifications, (n1, n2) ->
                                Long.compare(n2.getTimestamp(), n1.getTimestamp()));
                        callback.onCallback(notifications);
                        return;
                    } else {
                        Log.e(TAG, "error getting notifications", task.getException());
                        callback.onError(task.getException());
                    }


                });
    }

    public void saveFCMToken(String deviceId, String fcmToken) {
        DocumentReference userRef = db.collection("users").document(deviceId);
        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("fcmToken", fcmToken);

        userRef.set(tokenData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d("fcm", "FCM token saved!"))
                .addOnFailureListener(e -> Log.e("fcm", "Error saving fcm token", e));
    }

    /**
     * This method lets the database know a user responded to a notification
     * @param notifID String ID of the notification to update
     * @param userID String ID of the user to change in the notification
     * */
    public void userRespondedDB(String notifID, String userID){
        CollectionReference notifcol = db.collection("notifications");
        DocumentReference docref = notifcol.document(notifID);
        docref.update("deviceIds", FieldValue.arrayRemove(userID)).addOnSuccessListener(unused -> Log.d("userResponded", "User removed from notif successfully"))
                .addOnFailureListener(fail -> Log.d(TAG, "Error removing user from notification"));
        docref.update("respondedDeviceIds", FieldValue.arrayUnion(userID)).addOnSuccessListener(unused -> Log.d("userResponded", "User added to notif successfully"))
                .addOnFailureListener(fail -> Log.d(TAG, "Error adding user to notif"));
    }
    /**
     * This methods deletes an event from the database
     * @param eventID String EventID of the event that needs to be deleted
     */
    public void deleteEventDB(String eventID){
        deleteEventNotif(eventID);
        CollectionReference events = db.collection("events");
        DocumentReference docref = events.document(eventID);
        docref.delete().addOnSuccessListener(unused -> Log.d("DeleteEvent", "Event deleted successfully"))
                .addOnFailureListener(fail -> Log.d(TAG, "Error deleting event"));
    }

    public void deleteEventNotif(String eventID){
        CollectionReference notifcol = db.collection("notifications");
        notifcol.whereEqualTo("eventId", eventID)
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        Log.d(TAG, "Error getting documents");
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                            DocumentSnapshot notif = queryDocumentSnapshots.getDocuments().get(i);
                            DocumentReference notifref = notif.getReference();
                            notifref.delete().addOnSuccessListener(unused -> Log.d("DeleteEventNotif", "Event notif deleted successfully"))
                                    .addOnFailureListener(fail -> Log.d(TAG, "Error deleting event notif"));
                        }
                        Log.d(TAG, "Error getting documents");
                    }
                });
    }

    /**
     * This method returns a user's notifications they did interact with via callback
     * @param deviceId User's ID string to fetch the notifications for
     * @param callback Database Callback to return the database
     */
    public void getInteractedNotifsDB(String deviceId, DatabaseCallback<List<Notification>> callback){
        CollectionReference notifcol = db.collection("notifications");
        notifcol.whereArrayContains("respondedDeviceIds", deviceId)
                //.orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener((task) -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Notification> notifications = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Notification notif = document.toObject(Notification.class);
                            if (notif != null) {
                                notifications.add(notif);
                            }
                        }
                        Collections.sort(notifications, (n1, n2) ->
                                Long.compare(n2.getTimestamp(), n1.getTimestamp()));
                        callback.onCallback(notifications);
                        return;
                    } else {
                        Log.e(TAG, "error getting notifications in interacted one", task.getException());
                    }


                });
    }

    /**
     * This methods removes user from an notification set
     * @param userID User's ID string to be removed from the notification
     * @param notifs List of Notification the user wants to interact with
     * @param field Can be either deviceIds or respondedDeviceIds*/
    public void deleteUserFromNotifDB(String userID, List<Notification> notifs, String field){
        CollectionReference notifcol = db.collection("notifications");
        for(Notification not : notifs){
            DocumentReference docref = notifcol.document(not.getNotifId());
            docref.update(field, FieldValue.arrayRemove(userID)).addOnSuccessListener(unused -> Log.d("DeleteUserNotif", "User removed from notif successfully"))
                    .addOnFailureListener(fail -> Log.d(TAG, "Error removing user from notification"));
        }
    }
    /**
     * Get all notifications from the notifications collection (for admin review - US 03.08.01)
     * @param callback Callback with list of all notifications
     */
    public void getAllNotifications(DatabaseCallback<List<Notification>> callback) {
        db.collection("notifications")
                //.orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Notification> notifications = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Notification notif = document.toObject(Notification.class);
                            if (notif != null) {
                                notifications.add(notif);
                            }
                        }
                        Collections.sort(notifications, (n1, n2) ->
                                Long.compare(n2.getTimestamp(), n1.getTimestamp()));

                        callback.onCallback(notifications);
                    } else {
                        Log.e(TAG, "Error getting all notifications", task.getException());
                        if (task.getException() != null) {
                            callback.onError(task.getException());
                        } else {
                            callback.onError(new Exception("Unknown error"));
                        }
                    }
                });
    }


}









