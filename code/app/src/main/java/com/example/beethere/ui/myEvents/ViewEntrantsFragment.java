package com.example.beethere.ui.myEvents;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.beethere.R;
import com.example.beethere.User;
import com.example.beethere.eventclasses.Event;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewEntrantsFragment extends Fragment {

    private static final String ARG_EVENT_ID = "eventID";

    private InvitedAdapter invitedAdapter;
    private WaitListandRegisteredAdapter waitListAdapter;
    private WaitListandRegisteredAdapter registeredAdapter;

    private ArrayList<User> waitList = new ArrayList<>();
    private Map<String, Boolean> invited = new HashMap<>();
    private ArrayList<User> registered = new ArrayList<>();

    private String eventID;
    private Event event;

    private ListView entrantList;
    private Button waitListButton, invitedButton, registeredButton
    private ImageButton backButton;

    public ViewEntrantsFragment() {}

    public static ViewEntrantsFragment newInstance(String eventID) {
        ViewEntrantsFragment fragment = new ViewEntrantsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventID = getArguments().getString(ARG_EVENT_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_events_entrants, container, false);


        waitListButton = view.findViewById(R.id.button_entrant_waitlist);
        invitedButton = view.findViewById(R.id.button_entrant_invited);
        registeredButton = view.findViewById(R.id.button_entrant_registered);
        entrantList = view.findViewById(R.id.event_entrant_list);
        backButton = view.findViewById(R.id.button_back_to_prev);


        waitListAdapter = new WaitListandRegisteredAdapter(getContext(), waitList);
        registeredAdapter = new WaitListandRegisteredAdapter(getContext(), registered);
        invitedAdapter = new InvitedAdapter(getContext(), invited);


        Button[] buttons = {waitListButton, invitedButton, registeredButton};
        View.OnClickListener selectionListener = v -> {
            for (Button b : buttons) b.setSelected(false);
            v.setSelected(true);

            if (v == waitListButton) {
                entrantList.setAdapter(waitListAdapter);
            } else if (v == registeredButton) {
                entrantList.setAdapter(registeredAdapter);
            } else if (v == invitedButton) {
                entrantList.setAdapter(invitedAdapter);
            }
        };

        backButton.setOnClickListener(v -> {
            NavController nav = Navigation.findNavController(view);
            nav.navigate(R.id.ViewEntrantsToMyEvents);
        });

        for (Button b : buttons) b.setOnClickListener(selectionListener);

        // Default selected is waitlist
        waitListButton.setSelected(true);
        entrantList.setAdapter(waitListAdapter);

        FirebaseFirestore.getInstance()
                .collection("events")
                .document(eventID)
                .get()
                .addOnSuccessListener(snap -> {
                    if (snap.exists()) {
                        event = snap.toObject(Event.class);

                        if (event != null) {

                            waitList.clear();
                            waitList.addAll(event.getWaitList() != null ? event.getWaitList() : new ArrayList<>());

                            invited.clear();
                            invited.putAll(event.getInvited() != null ? event.getInvited() : new HashMap<>());

                            registered.clear();
                            registered.addAll(event.getRegistered() != null ? event.getRegistered() : new ArrayList<>());

                            waitListAdapter.notifyDataSetChanged();
                            registeredAdapter.notifyDataSetChanged();
                            invitedAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("ViewEntrants", "Failed to load event.", e));

        return view;
    }
}





