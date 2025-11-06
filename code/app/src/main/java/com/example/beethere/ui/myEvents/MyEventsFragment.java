package com.example.beethere.ui.myEvents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity; // or android.support.v4.app.FragmentActivity if using older support libs
import androidx.fragment.app.FragmentManager;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.beethere.User;
import com.example.beethere.eventclasses.Event;
import com.example.beethere.R;
import com.example.beethere.eventclasses.UserListManager;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;


public class MyEventsFragment extends Fragment {

    /**
     * Inflates "my events" page and displays event objects within the users associated
     * events, using a custom made events adapter
     * User can begin to create an event once clicking the "create event" button
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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_events, container, false);
        Button createEventButton = view.findViewById(R.id.createEventButton);
        TextView noEventsMessage = view.findViewById(R.id.noEventsMessage);

        ListView eventsListView = view.findViewById(R.id.myEventsList);
        ArrayList<Event> eventList = new ArrayList<>(); // TODO: change when firebase involved, on retrieving userID and their events if any

        if (!eventList.isEmpty()) {
            noEventsMessage.setVisibility(View.VISIBLE);
        } else {
            noEventsMessage.setVisibility(View.INVISIBLE);
        }

        MyEventsAdapter eventAdapter = new MyEventsAdapter(getContext(), eventList);
        eventsListView.setAdapter(eventAdapter);

        CreateEventFragment1 pageOne = new CreateEventFragment1();
        createEventButton.setOnClickListener(this::showCreationPage);

        return view;
    }

    private void showCreationPage (View v) {
        //TODO: check if account exists, if not, go to make account fragment

        //if account exists do what is below
        NavController nav = Navigation.findNavController(v);
        nav.navigate(R.id.myEventsToCreateEvents);
    }


}//end of fragment






//main activity set up nav controller
//in the in nav, set up fragments that would be moved to
// set up nav graph, everywhere u can go
//make that and thats how you use navigate() (log cat error suggestion)


//set up the nav view

//new nav graph in the navigation
//content fragment container view for the fragemnrts in the nav graph, literal graph
//leads to graph i  navigation
//set it to match the parent
// in main, nav controller to find the fragmmnt

