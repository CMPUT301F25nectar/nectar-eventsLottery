package com.example.beethere.ui.joined;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
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
import com.example.beethere.eventclasses.EventsAdapter;
import com.example.beethere.eventclasses.UserListManager;
import com.example.beethere.eventclasses.eventDetails.StatusFragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class JoinedFragment extends Fragment {

    private ArrayList<Event> eventList;
    private ArrayList<Event> userEvents;
    private EventsAdapter eventsAdapter;


    private User user;
    private Boolean userCreated;

    private DeviceIDViewModel deviceID;

    LocalDate currentDate;
    UserListManager manager;

    ArrayList<Event> userWaitlist;
    ArrayList<Event> userEnrollList;
    ArrayList<Event> userHistory;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_joined, container, false);
        deviceID = new ViewModelProvider(requireActivity()).get(DeviceIDViewModel.class);
        userCreated = Boolean.FALSE;
        user = new User();
        checkUserDB();


        eventList = new ArrayList<>();
        loadEvents();

        currentDate = LocalDate.now();
        manager = new UserListManager();

        userWaitlist = new ArrayList<>();
        userEnrollList = new ArrayList<>();
        userHistory = new ArrayList<>();

        userEvents = new ArrayList<>();
        loadWaitlist();
        userEvents.addAll(userWaitlist);
        eventsAdapter = new EventsAdapter(getContext(), userEvents);


        Button waitlisted = view.findViewById(R.id.button_waitlisted);
        Button enrolled = view.findViewById(R.id.button_enrolled);
        Button history = view.findViewById(R.id.button_history);

        buttonClicked(waitlisted, enrolled, history);


        waitlisted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClicked(waitlisted, enrolled, history);

                if(!userCreated){
                    // display message of no events in list
                } else {
                    loadWaitlist();
                    userEvents.clear();
                    userEvents.addAll(userWaitlist);
                    eventsAdapter.notifyDataSetChanged();
                }
            }
        });

        enrolled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClicked(enrolled, waitlisted, history);

                if(!userCreated){
                    // display message of no events in list
                } else {
                    loadEnrolled();
                    userEvents.clear();
                    userEvents.addAll(userEnrollList);
                    eventsAdapter.notifyDataSetChanged();
                }
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClicked(history, waitlisted, enrolled);

                if(!userCreated){
                    // display message of no events in list
                } else {
                    loadHistory();
                    userEvents.clear();
                    userEvents.addAll(userHistory);
                    eventsAdapter.notifyDataSetChanged();
                }
            }
        });

        ListView events = view.findViewById(R.id.event_display);
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
        return view;
    }

    public LocalDate convertDate(String stringDate) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
                        userCreated = Boolean.FALSE;
                    }
                    // User does exist related to deviceID
                    user = snapshot.toObject(User.class);
                })
                .addOnFailureListener(fail ->
                        userCreated = Boolean.FALSE
                );
    }

    public void loadEvents(){

        DatabaseFunctions functions = new DatabaseFunctions();
        DatabaseCallback<ArrayList<Event>> callback = new DatabaseCallback<>() {
            @Override
            public void onCallback(ArrayList<Event> result) {
                eventList.addAll(result);
                eventsAdapter.notifyDataSetChanged();
            }
            @Override
            public void onError(Exception e) {
                Log.d("Joined", "Joined fragment error getting events from database");
            }
        };
        functions.getEventsDB(callback);
    }

    public void loadWaitlist(){

        userHistory.clear();
        for (Event event : eventList) {
            // current date is before the start of the event
            // which is basically shows up until there is no way that the user could enroll
            // user in waitlist
            // user has been invited
            // user has declined their invite but still has been invited
            // remove event if user has declined?
            manager.setEvent(event);
            if(currentDate.isBefore(convertDate(event.getEventDateStart()))
                    && manager.inWaitlist(user)
                    && manager.inInvite(user)){
                userWaitlist.add(event);
            }
        }
    }

    public void loadEnrolled(){

        userHistory.clear();
        for (Event event : eventList) {
            manager.setEvent(event);
            // current date is before the end of the event
            // and the user has registered for the event
            if(currentDate.isBefore(convertDate(event.getEventDateEnd()))
                    && manager.inRegistered(user)) {
                userEnrollList.add(event);
            }
        }
    }

    public void loadHistory(){

        userHistory.clear();
        for (Event event: eventList) {
            manager.setEvent(event);
            // user is in one of the event lists
            // current data is after the event ends
            if(currentDate.isAfter(convertDate(event.getEventDateEnd()))
                    && manager.inWaitlist(user)
                    && manager.inInvite(user)
                    && manager.inRegistered(user)) {
                userHistory.add(event);
            }
        }
    }

    public void buttonClicked(Button clicked, Button notClicked1, Button notClicked2){
        clicked.setBackgroundColor();
        clicked.setTextColor();

        buttonNotClicked(notClicked1);
        buttonNotClicked(notClicked2);
    }

    public void buttonNotClicked(Button button){
        button.setBackgroundColor();
        button.setTextColor();
    }

}
