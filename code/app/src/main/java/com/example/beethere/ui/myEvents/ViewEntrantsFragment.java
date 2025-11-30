package com.example.beethere.ui.myEvents;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

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
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.beethere.R;
import com.example.beethere.User;
import com.example.beethere.adapters.InvitedAdapter;
import com.example.beethere.adapters.WaitListandRegisteredAdapter;
import com.example.beethere.eventclasses.Event;
import com.example.beethere.eventclasses.UserListManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
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
    private UserListManager userListManager;

    private ListView entrantList;
    private AppCompatButton waitListButton, invitedButton,
            registeredButton, exportCSV, backButton;

    public ViewEntrantsFragment() {}

    /**
     *
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventID = getArguments().getString(ARG_EVENT_ID);
        }
    }

    /**
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return view
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_events_entrants, container, false);


        waitListButton = view.findViewById(R.id.button_entrant_waitlist);
        invitedButton = view.findViewById(R.id.button_entrant_invited);
        registeredButton = view.findViewById(R.id.button_entrant_registered);

        backButton = view.findViewById(R.id.button_back_to_prev);
        exportCSV = view.findViewById(R.id.exportCSV);

        entrantList = view.findViewById(R.id.event_entrant_list);
        userListManager = new UserListManager(event);

        waitListAdapter = new WaitListandRegisteredAdapter(getContext(), waitList);
        registeredAdapter = new WaitListandRegisteredAdapter(getContext(), registered);
        invitedAdapter = new InvitedAdapter(getContext(), invited);


        AppCompatButton[] buttons = {waitListButton, invitedButton, registeredButton};
        View.OnClickListener selectionListener = v -> {
            for (Button b : buttons) b.setSelected(false);
            v.setSelected(true);

            if (v == waitListButton) {
                entrantList.setAdapter(waitListAdapter);
                exportCSV.setVisibility(GONE);
            } else if (v == registeredButton) {
                entrantList.setAdapter(registeredAdapter);
                exportCSV.setVisibility(VISIBLE);
            } else if (v == invitedButton) {
                entrantList.setAdapter(invitedAdapter);
                exportCSV.setVisibility(GONE);
            }
        };

        exportCSV.setOnClickListener(v -> {
            try {
                userListManager.exportCSV();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        backButton.setOnClickListener(v -> {
            NavController nav = Navigation.findNavController(view);
            nav.navigate(R.id.ViewEntrantsToMyEvents);
        });

        for (Button b : buttons) b.setOnClickListener(selectionListener);
        //default to waitlist list
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





