package com.example.beethere.ui.myEvents;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.beethere.R;
import com.example.beethere.User;
import com.example.beethere.eventclasses.Event;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewEntrantsFragment extends Fragment {

    private InvitedAdapter invitedAdapter;
    private WaitListandRegisteredAdapter waitListandRegisteredAdapter;

    private ArrayList<User> waitList;
    private Map<User, Boolean> invited;
    private ArrayList<User> registered;

    private String eventID;
    private Event event;

    private ListView entrantList;
    private Button waitListButton, invitedButton, registeredButton;

    public ViewEntrantsFragment(String eventID) {
        this.eventID = eventID;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_events_entrants, container, false);

        // Initialize views
        waitListButton = view.findViewById(R.id.button_entrant_waitlist);
        invitedButton = view.findViewById(R.id.button_entrant_invited);
        registeredButton = view.findViewById(R.id.button_entrant_registered);
        entrantList = view.findViewById(R.id.event_entrant_list);

        // Button selection handling
        Button[] buttons = {waitListButton, invitedButton, registeredButton};
        View.OnClickListener selectionListener = v -> {
            for (Button b : buttons) b.setSelected(false);
            v.setSelected(true);
        };
        for (Button b : buttons) b.setOnClickListener(selectionListener);

        // Load event from Firestore
        FirebaseFirestore.getInstance()
                .collection("events1")
                .document(eventID)
                .get()
                .addOnSuccessListener(snap -> {
                    if (snap.exists()) {
                        event = snap.toObject(Event.class);

                        // Assign to fields (NOT local variables!)
                        waitList = event.getWaitList();
                        invited = event.getInvited();
                        registered = event.getRegistered();

                        // Set default list (waitlist) after data loads
                        if (waitList != null) {
                            waitListandRegisteredAdapter = new WaitListandRegisteredAdapter(getContext(), waitList);
                            entrantList.setAdapter(waitListandRegisteredAdapter);
                        }

                        // Set click listeners AFTER data is loaded
                        waitListButton.setOnClickListener(v -> {
                            if (waitList != null) {
                                waitListandRegisteredAdapter = new WaitListandRegisteredAdapter(getContext(), waitList);
                                entrantList.setAdapter(waitListandRegisteredAdapter);
                            }
                        });

                        invitedButton.setOnClickListener(v -> {
                            if (invited != null) {
                                invitedAdapter = new InvitedAdapter(getContext(), invited);
                                entrantList.setAdapter(invitedAdapter);
                            }
                        });

                        registeredButton.setOnClickListener(v -> {
                            if (registered != null) {
                                waitListandRegisteredAdapter = new WaitListandRegisteredAdapter(getContext(), registered);
                                entrantList.setAdapter(waitListandRegisteredAdapter);
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> Log.e("ViewEntrants", "Failed to load event.", e));

        // Initial button selection
        for (Button b : buttons) b.setSelected(false);
        waitListButton.setSelected(true);

        return view;
    }
}






//for the edititng fragment, just copy paste the create fragment 1 and take off switches and pre fill fields
//im sure theres things to consider with that tho

//TODO
//first, create the content view for invited, keep in mind, this is also going to have a dropdown menu to delete
//then, create content view for registered and wait list
//then, create adapters for each content view
//then, in the adapter add the activity (this one)
//qrcode display class
//honestly just do the whole damn thing




