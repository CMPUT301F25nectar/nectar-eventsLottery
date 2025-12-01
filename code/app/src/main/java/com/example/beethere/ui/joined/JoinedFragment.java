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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class JoinedFragment extends Fragment {

    private DeviceIDViewModel deviceID;
    private DateTimeFormatter dateFormatter;

    private TextView message;
    private ListView events;

    private User user;

    private ArrayList<Event> eventList;
    private EventsAdapter eventsAdapter;

    private ArrayList<Event> userWaitlist;
    private ArrayList<Event> userEnrollList;
    private ArrayList<Event> userHistory;

    private EventsAdapter waitlistAdapter;
    private EventsAdapter enrolledAdapter;
    private EventsAdapter historyAdapter;

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
        super.onViewCreated(view, savedInstanceState);
        // checking if user exists for deviceID
        user = null;
        eventList = new ArrayList<Event>();
        eventsAdapter = new EventsAdapter(getContext(), eventList);

        // each user list (stored, so not calced everytime)
        userWaitlist = new ArrayList<Event>();
        userEnrollList = new ArrayList<Event>();
        userHistory = new ArrayList<Event>();

        waitlistAdapter = new EventsAdapter(getContext(), userWaitlist);
        enrolledAdapter = new EventsAdapter(getContext(), userEnrollList);
        historyAdapter = new EventsAdapter(getContext(), userHistory);

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

        // switch to event details when event is clicked on
        events = view.findViewById(R.id.joined_event_display);
        events.setAdapter(eventsAdapter);
        events.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                NavController nav = Navigation.findNavController(view);
                nav.navigate(R.id.joinedToEventDetails);

                EventDataViewModel event = new ViewModelProvider(getActivity()).get(EventDataViewModel.class);
                event.setEvent((Event) parent.getItemAtPosition(position));
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
                if (user.getDeviceid() != null){
                    loadEvents();
                }
                else {
                    switchDisplay(userWaitlist, waitlistAdapter);
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
                eventList.clear();
                eventList.addAll(result);
                eventsAdapter.notifyDataSetChanged();
                loadLists();
                switchDisplay(userWaitlist, waitlistAdapter);
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
                waitlistAdapter.notifyDataSetChanged();
            }
            // before the end of the event
            // user has registered for the event
            if(currentDate.isBefore(eventEnd) && manager.inRegistered(user)) {
                userEnrollList.add(event);
                enrolledAdapter.notifyDataSetChanged();
            }
            // after end of the event
            // eventList only has events where the user is included
            if(currentDate.isAfter(eventEnd) && (manager.inWaitlist(user) || manager.inInvite(user) || manager.inRegistered(user))) {
                userHistory.add(event);
                historyAdapter.notifyDataSetChanged();
            }

        }

        waitlistAdapter.notifyDataSetChanged();
        enrolledAdapter.notifyDataSetChanged();
        historyAdapter.notifyDataSetChanged();
        eventsAdapter.notifyDataSetChanged();

    }

    public void buttonSelected(Button selected, Button notSelected1, Button notSelected2){
        selected.setSelected(true);
        notSelected1.setSelected(false);
        notSelected2.setSelected(false);
    }

    public void switchDisplay(ArrayList<Event> display, EventsAdapter eventsAdapter){
        if (user.getDeviceid() == null) {
            displayMessage("Make an account to join an event!");
            events.setVisibility(GONE);
        } else if (display.isEmpty()) {
            displayMessage("No events joined...");
            events.setVisibility(GONE);
        } else {
            message.setVisibility(GONE);
            events.setVisibility(VISIBLE);
            events.setAdapter(eventsAdapter);
        }
    }

    public void displayMessage(String text){
        message.setText(text);
        message.setVisibility(VISIBLE);
    }

}