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
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

    private ListView events;
    private TextView message;

    private User user;

    private ArrayList<Event> eventList;
    private ArrayList<Event> userWaitlist;
    private ArrayList<Event> userEnrollList;
    private ArrayList<Event> userHistory;

    EventsAdapter waitlistAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // inflate view
        View view = inflater.inflate(R.layout.fragment_joined, container, false);

        // get deviceID
        deviceID = new ViewModelProvider(requireActivity()).get(DeviceIDViewModel.class);
        // initialize formatter
        dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        message = view.findViewById(R.id.joined_fragment_message);
        displayMessage("Loading...");

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // checking if user exists for deviceID
        user = null;
        checkUserDB();

        // loading all events that user has joined to eventList
        // eventList stays empty if !userCreated
        eventList = new ArrayList<>();


        // each user list (stored, so not calced everytime)
        userWaitlist = new ArrayList<>();
        userEnrollList = new ArrayList<>();
        userHistory = new ArrayList<>();
        waitlistAdapter = new EventsAdapter(getContext(), userWaitlist);
        EventsAdapter enrolledAdapter = new EventsAdapter(getContext(), userEnrollList);
        EventsAdapter historyAdapter = new EventsAdapter(getContext(), userHistory);

        // setting views
        Button waitlisted = view.findViewById(R.id.button_waitlisted);
        Button enrolled = view.findViewById(R.id.button_enrolled);
        Button history = view.findViewById(R.id.button_history);
        events = view.findViewById(R.id.event_display);


        // start with waitlisted selected
        buttonSelected(waitlisted, enrolled, history);
        //switchDisplay(userWaitlist, waitlistAdapter);

        // switch to event details when event is clicked on
        events.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                NavController nav = Navigation.findNavController(view);
                nav.navigate(R.id.joinedToEventDetails);

                EventDataViewModel event = new ViewModelProvider(getActivity()).get(EventDataViewModel.class);
                event.setEvent((Event) parent.getItemAtPosition(position));
            }
        });


        waitlisted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSelected(waitlisted, enrolled, history);
                switchDisplay(userWaitlist, waitlistAdapter);
            }
        });

        enrolled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSelected(enrolled, waitlisted, history);
                switchDisplay(userEnrollList, enrolledAdapter);
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSelected(history,waitlisted, enrolled);
                switchDisplay(userHistory, historyAdapter);
            }
        });



        super.onViewCreated(view, savedInstanceState);
    }

    public LocalDate convertDate(String stringDate) {
        return LocalDate.parse(stringDate, dateFormatter);
    }

    public void checkUserDB(){
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(deviceID.getDeviceID())
                .get()
                .addOnSuccessListener((DocumentSnapshot snapshot) -> {
                    // User does not exists related to deviceID
                    if (!snapshot.exists()){
                        user = null;
                    }
                    // User does exist related to deviceID
                    user = snapshot.toObject(User.class);
                })
                .addOnFailureListener(fail ->
                        user = null
                );

        loadEvents();
    }

    public void loadEvents(){
        DatabaseFunctions functions = new DatabaseFunctions();
        // setting up the callback
        DatabaseCallback<ArrayList<Event>> callback = new DatabaseCallback<>() {
            @Override
            public void onCallback(ArrayList<Event> result) {
                // if there is a user account attached to deviceID
                if (user != null) {
                    // get event manager
                    UserListManager manager = new UserListManager();
                    // go through each event
                    for (Event event : result) {
                        // set manager for event
                        manager.setEvent(event);
                        // if user in event waitlist/invite/registered
                        // add event to list
                        if(manager.inWaitlist(user)
                                || manager.inInvite(user)
                                || manager.inRegistered(user)) {
                            eventList.add(event);
                        }
                    }
                    loadLists();
                }
                switchDisplay(userWaitlist, waitlistAdapter);
                // if not user created handled elsewhere, no events added
            }
            @Override
            public void onError(Exception e) {
                Log.d("Joined", "Joined fragment error getting events from database");
            }
        };
        // no filtering for actually getting the events
        // getting the events function call
        functions.getEventsDB(callback);

    }


    // user must exist
    // events are filtered already
    // for user existing & being in waitlist/invited/registered of event
    public void loadLists(){
        UserListManager manager = new UserListManager();
        LocalDate currentDate = LocalDate.now();
        LocalDate eventEnd, eventStart;

        userWaitlist.clear();
        userEnrollList.clear();
        userHistory.clear();

        for (Event event : eventList){
            // set event manager, and conver event dates
            manager.setEvent(event);
            eventStart = convertDate(event.getEventDateStart());
            eventEnd = convertDate(event.getEventDateEnd());

            // currentDate before the start of the event
            // so waitlist has opened
            // waitlist may have closed
            // user is in waitlist or user has been invited to register
            if(currentDate.isBefore(eventStart) && !manager.inRegistered(user)){
                userWaitlist.add(event);
            }
            // before the end of the event
            // user has registered for the event
            if(currentDate.isBefore(eventEnd) && manager.inRegistered(user)) {
                userEnrollList.add(event);
            }
            // after end of the event
            // eventList only has events where the user is included
            if(currentDate.isAfter(eventEnd)) {
                userHistory.add(event);
            }

        }

        switchDisplay(userWaitlist, waitlistAdapter);
    }

    public void buttonSelected(Button selected, Button notSelected1, Button notSelected2){
        selected.setSelected(true);
        notSelected1.setSelected(false);
        notSelected2.setSelected(false);
    }

    public void switchDisplay(ArrayList<Event> display, EventsAdapter eventsAdapter){
        if (user == null) {
            displayMessage("Make an account to join an event!");
        } else if (display.isEmpty()) {
            displayMessage("No events joined...");
        } else {
            message.setVisibility(GONE);
            events.setAdapter(eventsAdapter);
        }
    }

    public void displayMessage(String text){
        message.setText(text);
        message.setVisibility(VISIBLE);
    }

}
