package com.example.beethere.ui.allEvents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import com.example.beethere.R;
import com.example.beethere.User;
import com.example.beethere.eventclasses.Event;
import com.example.beethere.eventclasses.EventDataViewModel;
import com.example.beethere.eventclasses.EventsAdapter;
import com.example.beethere.eventclasses.eventDetails.EventDetailsFragment;
import com.example.beethere.ui.myEvents.MyEventsAdapter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;


public class AllEventsFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_events, container, false);


        SearchView search = view.findViewById(R.id.searchView);

        ImageButton filter = view.findViewById(R.id.button_filter);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // filter dialog fragment
            }
        });

        User tempUser = new User("name", "email");
        User tempEntrant = new User("some name", "email");
        Event tempEvent = new Event(tempUser,
                1,
                "title",
                "description",
                "path",
                1,
                Boolean.TRUE,
                LocalDate.of(2025, 12,1),
                LocalDate.of(2025, 12, 31),
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 30),
                LocalTime.of(8, 0),
                LocalTime.of(10, 0),
                50,
                Boolean.FALSE,
                Boolean.TRUE);
        Event tempEvent2 = new Event(tempUser,
                4,
                "something unique",
                "a long and arduous description",
                "some other path",
                5,
                Boolean.TRUE,
                LocalDate.of(2026, 12,1),
                LocalDate.of(2026, 12, 31),
                LocalDate.of(2027, 1, 1),
                LocalDate.of(2027, 1, 30),
                LocalTime.of(8, 0),
                LocalTime.of(10, 0),
                50,
                Boolean.FALSE,
                Boolean.TRUE);


        ArrayList<Event> eventList = new ArrayList<>(); // TODO: change when firebase involved, on retrieving userID and their events if any
        eventList.add(tempEvent);
        eventList.add(tempEvent2);
        ListView events = view.findViewById(R.id.event_display);
        EventsAdapter eventAdapter = new EventsAdapter(getContext(), eventList);
        events.setAdapter(eventAdapter);
        events.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                NavController nav = Navigation.findNavController(view);
                        nav.navigate(R.id.allEventToEventDetails);

                EventDataViewModel event = new ViewModelProvider(getActivity()).get(EventDataViewModel.class);
                event.setEvent((Event) parent.getItemAtPosition(position));
            }
        });



        return view;
    }
}