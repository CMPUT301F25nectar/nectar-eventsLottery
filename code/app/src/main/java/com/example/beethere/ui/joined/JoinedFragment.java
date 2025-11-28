package com.example.beethere.ui.joined;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

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

    private User user;
    private Boolean userCreated;

    private ArrayList<Event> eventList;
    private ArrayList<Event> displayList;
    private EventsAdapter eventsAdapter;

    ArrayList<Event> userWaitlist;
    ArrayList<Event> userEnrollList;
    ArrayList<Event> userHistory;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // inflate view
        View view = inflater.inflate(R.layout.fragment_joined, container, false);

        // get deviceID
        deviceID = new ViewModelProvider(requireActivity()).get(DeviceIDViewModel.class);
        // initialize formatter
        dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // checking if user exists for deviceID
        userCreated = Boolean.FALSE;
        user = new User();
        checkUserDB();

        // AllEvents
        eventList = new ArrayList<>();
        loadEvents();

        // each user list (stored, so not calced everytime)
        userWaitlist = new ArrayList<>();
        userEnrollList = new ArrayList<>();
        userHistory = new ArrayList<>();

        displayList = new ArrayList<>();
        if (userCreated){
            loadLists();
            displayList.addAll(userWaitlist);
        }
        eventsAdapter = new EventsAdapter(getContext(), displayList);


        Button waitlisted = view.findViewById(R.id.button_waitlisted);
        waitlisted.setSelected(true);
        Button enrolled = view.findViewById(R.id.button_enrolled);
        Button history = view.findViewById(R.id.button_history);


        waitlisted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*buttonClicked(waitlisted, enrolled, history);*/

                if(!userCreated){
                    // display message of no events in list
                } else {
                    displayList.clear();
                    displayList.addAll(userWaitlist);
                    eventsAdapter.notifyDataSetChanged();
                }
            }
        });

        enrolled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!userCreated){
                    // display message of no events in list
                } else {
                    displayList.clear();
                    displayList.addAll(userEnrollList);
                    eventsAdapter.notifyDataSetChanged();
                }
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!userCreated){
                    // display message of no events in list
                } else {
                    displayList.clear();
                    displayList.addAll(userHistory);
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
        // setting up the callback
        DatabaseCallback<ArrayList<Event>> callback = new DatabaseCallback<>() {
            @Override
            public void onCallback(ArrayList<Event> result) {
                // if there is a user account attached to deviceID
                if (userCreated) {
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
                }
                // if not user created handled elsewhere, no events added
            }
            @Override
            public void onError(Exception e) {
                Log.d("Joined", "Joined fragment error getting events from database");
            }
        };
        // no filtering for actually getting the events
        // getting the events function call
        functions.getEventsDB(Boolean.FALSE, callback);
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
    }

}
