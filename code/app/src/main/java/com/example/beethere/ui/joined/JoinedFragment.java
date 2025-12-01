package com.example.beethere.ui.joined;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.beethere.DatabaseCallback;
import com.example.beethere.DatabaseFunctions;
import com.example.beethere.R;
import com.example.beethere.User;
import com.example.beethere.device.DeviceIDViewModel;
import com.example.beethere.eventclasses.Event;
import com.example.beethere.eventclasses.EventDataViewModel;
import com.example.beethere.adapters.EventsAdapter;
import com.example.beethere.eventclasses.UserListManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class JoinedFragment extends Fragment {

    private DeviceIDViewModel deviceID;
    private DateTimeFormatter dateFormatter;

    private TextView message;

    private User user;

    private ArrayList<Event> eventList;

    private ArrayList<Event> userWaitlist;
    private ArrayList<Event> userEnrollList;
    private ArrayList<Event> userHistory;
    JoinedListFragment displayList;

    private FrameLayout layout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // inflate view
        View view = inflater.inflate(R.layout.fragment_joined, container, false);

        // get deviceID
        deviceID = new ViewModelProvider(requireActivity()).get(DeviceIDViewModel.class);
        // initialize formatter
        dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        layout = view.findViewById(R.id.joined_list_layout);
        layout.setVisibility(View.INVISIBLE);

        message = view.findViewById(R.id.joined_fragment_message);
        displayMessage("Loading...");

        displayList = new JoinedListFragment();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // checking if user exists for deviceID
        user = null;
        eventList = new ArrayList<Event>();
        displayEvents();

        // each user list (stored, so not calced everytime)
        userWaitlist = new ArrayList<Event>();
        userEnrollList = new ArrayList<Event>();
        userHistory = new ArrayList<Event>();

        // setting buttons
        Button waitlisted = view.findViewById(R.id.button_waitlisted);
        Button enrolled = view.findViewById(R.id.button_enrolled);
        Button history = view.findViewById(R.id.button_history);

        // start with waitlisted selected
        buttonSelected(waitlisted, enrolled, history);

        waitlisted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSelected(waitlisted, enrolled, history);
                switchDisplay(userWaitlist);
            }
        });

        enrolled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSelected(enrolled, waitlisted, history);
                switchDisplay(userEnrollList);
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSelected(history,waitlisted, enrolled);
                switchDisplay(userHistory);
            }
        });

        checkUserDB();
    }

    public LocalDate convertDate(String stringDate) {
        return LocalDate.parse(stringDate, dateFormatter);
    }

    public void checkUserDB(){
        DatabaseFunctions dbFunctions = new DatabaseFunctions();

        DatabaseCallback<User> userCallback = new DatabaseCallback<User>() {
            @Override
            public void onCallback(User result) {
                user = result;
                if (user != null){
                    loadEvents();
                }
            }
            @Override
            public void onError(Exception e) {
                user = null;
            }
        };

        dbFunctions.getUserDB(deviceID.getDeviceID(), userCallback);
    }

    public void loadEvents(){
        DatabaseFunctions functions = new DatabaseFunctions();
        // setting up the callback
        DatabaseCallback<ArrayList<Event>> callback = new DatabaseCallback<>() {
            @Override
            public void onCallback(ArrayList<Event> result) {
                // if there is a user account attached to deviceID
                eventList.clear();
                eventList.addAll(result);
                if (user != null) {
                    loadLists();
                } else {
                    displayEvents();
                }
                // if not user created handled elsewhere, no events added
            }
            @Override
            public void onError(Exception e) {
                Log.d("Joined", "Joined fragment error getting events from database");
            }
        };

        functions.getEventsDB(callback);
    }

    // user must exist
    // events are filtered already
    // for user existing & being in waitlist/invited/registered of event
    public void loadLists(){
        UserListManager manager = new UserListManager();
        LocalDate currentDate = LocalDate.now();
        LocalDate eventEnd, eventStart;

        for (Event event : eventList){
            // set event manager, and conver event dates
            manager.setEvent(event);
            eventStart = convertDate(event.getEventDateStart());
            eventEnd = convertDate(event.getEventDateEnd());

            // currentDate before the start of the event
            // so waitlist has opened
            // waitlist may have closed
            // user is in waitlist or user has been invited to register
            if(currentDate.isBefore(eventStart) && (manager.inWaitlist(user) || manager.inInvite(user))){
                userWaitlist.add(event);
                Log.d("Joined", "eventid: " + event.getEventID());
            }
            // before the end of the event
            // user has registered for the event
            if(currentDate.isBefore(eventEnd) && manager.inRegistered(user)) {
                userEnrollList.add(event);
            }
            // after end of the event
            // eventList only has events where the user is included
            if(currentDate.isAfter(eventEnd) &&
                    (manager.inWaitlist(user)
                    || manager.inInvite(user)
                    || manager.inRegistered(user))) {
                userHistory.add(event);
            }
        }

        switchDisplay(userWaitlist);
    }

    public void buttonSelected(Button selected, Button notSelected1, Button notSelected2){
        selected.setSelected(true);
        notSelected1.setSelected(false);
        notSelected2.setSelected(false);
    }

    public void switchDisplay(ArrayList<Event> display){
        if (user == null) {
            displayMessage("Make an account to join an event!");
            layout.setVisibility(GONE);
        } else if (display.isEmpty()) {
            displayMessage("No events joined...");
            layout.setVisibility(GONE);
        } else {
            message.setVisibility(GONE);
            layout.setVisibility(VISIBLE);
            displayList.updateEventList(display);

            /*events.setAdapter(eventsAdapter);*/
        }
    }

    public void displayEvents(){
        displayList.setEventList(eventList);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.joined_list_layout, displayList).commit();
    }

    public void displayMessage(String text){
        message.setText(text);
        message.setVisibility(VISIBLE);
    }

}
